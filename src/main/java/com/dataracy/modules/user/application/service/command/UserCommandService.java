package com.dataracy.modules.user.application.service.command;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.common.support.lock.DistributedLock;
import com.dataracy.modules.topic.application.port.in.IsExistTopicUseCase;
import com.dataracy.modules.user.application.dto.request.ChangePasswordRequest;
import com.dataracy.modules.user.application.dto.request.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.SelfSignUpRequest;
import com.dataracy.modules.user.application.port.in.reference.FindAuthorLevelUseCase;
import com.dataracy.modules.user.application.port.in.reference.FindOccupationUseCase;
import com.dataracy.modules.user.application.port.in.reference.FindVisitSourceUseCase;
import com.dataracy.modules.user.application.port.in.signup.OAuthSignUpUseCase;
import com.dataracy.modules.user.application.port.in.signup.SelfSignUpUseCase;
import com.dataracy.modules.user.application.port.in.user.ChangePasswordUseCase;
import com.dataracy.modules.user.application.port.in.user.DuplicateEmailUseCase;
import com.dataracy.modules.user.application.port.in.user.DuplicateNicknameUseCase;
import com.dataracy.modules.user.application.port.out.UserRepositoryPort;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.model.reference.AuthorLevel;
import com.dataracy.modules.user.domain.model.reference.Occupation;
import com.dataracy.modules.user.domain.model.reference.VisitSource;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandService implements SelfSignUpUseCase, OAuthSignUpUseCase, ChangePasswordUseCase {
    private final UserRepositoryPort userRepositoryPort;

    private final JwtGenerateUseCase jwtGenerateUseCase;
    private final JwtValidateUseCase jwtValidateUseCase;
    private final IsExistTopicUseCase isExistTopicUseCase;
    private final PasswordEncoder passwordEncoder;

    private final FindAuthorLevelUseCase findAuthorLevelUseCase;
    private final FindOccupationUseCase findOccupationUseCase;
    private final FindVisitSourceUseCase findVisitSourceUseCase;

    private final DuplicateNicknameUseCase duplicateNicknameUseCase;
    private final DuplicateEmailUseCase duplicateEmailUseCase;

    /**
     * 클라이언트로부터 받은 유저 정보를 토대로 자체 회원가입을 진행한다.(이메일, 닉네임, 비밀번호, 성별)
     * 리프레시 토큰 분산락 처리
     *
     * @param requestDto 자체 회원가입을 위한 요청 Dto
     * @return LoginResponseDto (컨트롤러에서 리프레시 토큰 쿠키 저장을 위한 response)
     */
    @Override
    @Transactional
    @DistributedLock(
            key = "'lock:email:' + #requestDto.email()",
            waitTime = 300L,
            leaseTime = 2000L,
            retry = 2
    )
    public RefreshTokenResponse signUpSelf(SelfSignUpRequest requestDto) {
        // 이메일 중복 체크
        duplicateEmailUseCase.validateDuplicatedEmail(requestDto.email());
        // 닉네임 중복 체크
        duplicateNicknameUseCase.validateDuplicatedNickname(requestDto.nickname());

        // 비밀번호와 비밀번호 확인이 다를 경우
        if (!requestDto.password().equals(requestDto.passwordConfirm())) {
            throw new UserException(UserErrorStatus.NOT_SAME_PASSWORD);
        }

        // 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.password());
        String providerId = UUID.randomUUID().toString();

        // 작성자 유형 id를 통해 작성자 유형 조회 및 유효성 검사
        AuthorLevel authorLevel = findAuthorLevelUseCase.findAuthorLevel(requestDto.authorLevelId());
        // 토픽 id를 통해 토픽 존재 유효성 검사를 시행한다.
        requestDto.topicIds()
                .forEach(isExistTopicUseCase::validateTopicById);
        // 직업 id를 통해 직업 조회 및 유효성 검사
        Occupation occupation = findOccupationUseCase.findOccupation(requestDto.occupationId());
        // 방문 경로 id를 통해 방문 경로 조회 및 유효성 검사
        VisitSource visitSource = findVisitSourceUseCase.findVisitSource(requestDto.visitSourceId());

        // 유저 도메인 모델 생성 및 db 저장
        User user = User.toDomain(
                null,
                ProviderType.LOCAL,
                providerId,
                RoleType.ROLE_USER,
                requestDto.email(),
                encodedPassword,
                requestDto.nickname(),
                authorLevel,
                occupation,
                requestDto.topicIds(),
                visitSource,
                requestDto.isAdTermsAgreed(),
                false
        );
        User savedUser = userRepositoryPort.saveUser(user);

        // 리프레시 토큰 발급
        String refreshToken = jwtGenerateUseCase.generateRefreshToken(savedUser.getId(), RoleType.ROLE_USER);

        log.info("자체 회원가입 성공: {}", user.getEmail());
        return new RefreshTokenResponse(savedUser.getId(),
                refreshToken,
                jwtValidateUseCase.getRefreshTokenExpirationTime()
        );
    }

    /**
     * 회원가입 처리.
     * 닉네임 동시성 분산락 처리
     *
     * @param registerToken 회원가입 토큰
     * @param requestDto    회원가입 요청 정보
     */
    @Override
    @Transactional
    @DistributedLock(
            key = "'lock:nickname:' + #requestDto.nickname()",
            waitTime = 300L,
            leaseTime = 2000L,
            retry = 2
    )
    public RefreshTokenResponse signUpOAuth(String registerToken, OnboardingRequest requestDto) {
        // 레지스터 토큰 유효성 체크 및 정보 조회
        jwtValidateUseCase.validateToken(registerToken);
        String provider = jwtValidateUseCase.getProviderFromRegisterToken(registerToken);
        String providerId = jwtValidateUseCase.getProviderIdFromRegisterToken(registerToken);
        String email = jwtValidateUseCase.getEmailFromRegisterToken(registerToken);

        // 이메일 중복 체크
        duplicateEmailUseCase.validateDuplicatedEmail(email);
        // 닉네임 중복 체크
        duplicateNicknameUseCase.validateDuplicatedNickname(requestDto.nickname());

        // 작성자 유형 id를 통해 작성자 유형 조회 및 유효성 검사
        AuthorLevel authorLevel = findAuthorLevelUseCase.findAuthorLevel(requestDto.authorLevelId());
        // 토픽 id를 통해 토픽 존재 유효성 검사를 시행한다.
        requestDto.topicIds()
                .forEach(isExistTopicUseCase::validateTopicById);
        // 직업 id를 통해 직업 조회 및 유효성 검사
        Occupation occupation = findOccupationUseCase.findOccupation(requestDto.occupationId());
        // 방문 경로 id를 통해 방문 경로 조회 및 유효성 검사
        VisitSource visitSource = findVisitSourceUseCase.findVisitSource(requestDto.visitSourceId());

        // 유저 도메인 모델 생성 및 db 저장
        User user = User.toDomain(
                null,
                ProviderType.of(provider),
                providerId,
                RoleType.ROLE_USER,
                email,
                null,
                requestDto.nickname(),
                authorLevel,
                occupation,
                requestDto.topicIds(),
                visitSource,
                requestDto.isAdTermsAgreed(),
                false
        );
        User savedUser = userRepositoryPort.saveUser(user);

        // 리프레시 토큰 발급
        String refreshToken = jwtGenerateUseCase.generateRefreshToken(savedUser.getId(), RoleType.ROLE_USER);

        log.info("소셜 회원가입 성공: {}", email);
        return new RefreshTokenResponse(savedUser.getId(),
                refreshToken,
                jwtValidateUseCase.getRefreshTokenExpirationTime()
        );
    }

    /**
     * 유저의 비밀번호를 변경한다.
     *
     * @param userId 유저 id
     * @param requestDto 비밀번호를 담은 dto
     */
    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest requestDto) {
        if (!requestDto.password().equals(requestDto.passwordConfirm())) {
            throw new UserException(UserErrorStatus.NOT_SAME_PASSWORD);
        }

        User savedUser = userRepositoryPort.findUserById(userId);
        switch (savedUser.getProvider()) {
            case GOOGLE -> throw new UserException(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_GOOGLE);
            case KAKAO -> throw new UserException(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_KAKAO);
        }

        // 패스워드 암호화 및 변경
        String encodedPassword = passwordEncoder.encode(requestDto.password());
        userRepositoryPort.changePassword(userId, encodedPassword);
    }
}
