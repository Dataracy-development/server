package com.dataracy.modules.user.application.service.command;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.common.support.lock.DistributedLock;
import com.dataracy.modules.topic.application.port.in.IsExistTopicUseCase;
import com.dataracy.modules.user.application.dto.request.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.SelfSignUpRequest;
import com.dataracy.modules.user.application.port.in.signup.OAuthSignUpUseCase;
import com.dataracy.modules.user.application.port.in.signup.SelfSignUpUseCase;
import com.dataracy.modules.user.application.port.in.reference.FindAuthorLevelUseCase;
import com.dataracy.modules.user.application.port.in.reference.FindOccupationUseCase;
import com.dataracy.modules.user.application.port.in.reference.FindVisitSourceUseCase;
import com.dataracy.modules.user.application.port.out.UserRepositoryPort;
import com.dataracy.modules.user.application.service.validator.UserDuplicateValidator;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.model.reference.AuthorLevel;
import com.dataracy.modules.user.domain.model.reference.Occupation;
import com.dataracy.modules.user.domain.model.reference.VisitSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandService implements SelfSignUpUseCase, OAuthSignUpUseCase {
    private final UserRepositoryPort userRepositoryPort;

    private final JwtGenerateUseCase jwtGenerateUseCase;
    private final JwtValidateUseCase jwtValidateUseCase;
    private final IsExistTopicUseCase isExistTopicUseCase;
    private final UserDuplicateValidator userDuplicateValidator;
    private final PasswordEncoder passwordEncoder;

    private final FindAuthorLevelUseCase findAuthorLevelUseCase;
    private final FindOccupationUseCase findOccupationUseCase;
    private final FindVisitSourceUseCase findVisitSourceUseCase;

    /**
     * 클라이언트로부터 받은 유저 정보를 토대로 자체 회원가입을 진행한다.(이메일, 닉네임, 비밀번호, 성별)
     *
     * @param requestDto 자체 회원가입을 위한 요청 Dto
     * @return LoginResponseDto (컨트롤러에서 리프레시 토큰 쿠키 저장을 위한 response)
     */
    @Override
    @DistributedLock(
            key = "'lock:nickname:' + #requestDto.nickname()",
            waitTime = 300L,
            leaseTime = 2000L,
            retry = 2
    )
    @Transactional
    public RefreshTokenResponse signUpSelf(SelfSignUpRequest requestDto) {
        // 닉네임 중복 체크
        userDuplicateValidator.duplicateNickname(requestDto.nickname());
        // 이메일 중복 체크
        userDuplicateValidator.duplicateEmail(requestDto.email());

        // 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.password());

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
                null,
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
    @DistributedLock(
            key = "'lock:nickname:' + #requestDto.nickname()",
            waitTime = 300L,
            leaseTime = 2000L,
            retry = 2
    )
    @Transactional
    public RefreshTokenResponse signUpOAuth(String registerToken, OnboardingRequest requestDto) {
        // 닉네임 중복 체크
        userDuplicateValidator.duplicateNickname(requestDto.nickname());

        // 레지스터 토큰 유효성 체크 및 정보 조회
        jwtValidateUseCase.validateToken(registerToken);
        String provider = jwtValidateUseCase.getProviderFromRegisterToken(registerToken);
        String providerId = jwtValidateUseCase.getProviderIdFromRegisterToken(registerToken);
        String email = jwtValidateUseCase.getEmailFromRegisterToken(registerToken);

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
}
