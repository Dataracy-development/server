package com.dataracy.modules.user.application.service.command;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.JwtValidateUseCase;
import com.dataracy.modules.common.support.lock.DistributedLock;
import com.dataracy.modules.topic.application.port.in.FindTopicUseCase;
import com.dataracy.modules.user.application.dto.request.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.SelfSignUpRequest;
import com.dataracy.modules.user.application.port.in.OAuthSignUpUseCase;
import com.dataracy.modules.user.application.port.in.SelfSignUpUseCase;
import com.dataracy.modules.user.application.port.out.UserRepositoryPort;
import com.dataracy.modules.user.application.service.validator.UserDuplicateValidator;
import com.dataracy.modules.user.domain.enums.*;
import com.dataracy.modules.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandService implements SelfSignUpUseCase, OAuthSignUpUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtGenerateUseCase jwtGenerateUseCase;
    private final JwtValidateUseCase jwtValidateUseCase;
    private final FindTopicUseCase findTopicUseCase;
    private final UserDuplicateValidator userDuplicateValidator;
    private final PasswordEncoder passwordEncoder;

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

        // 요청된 도메인(String) → topicId(Long) 변환
        List<Long> topicIds = requestDto.topics().stream()
                .map(findTopicUseCase::findTopicIdByName)
                .toList();

        // 유저 도메인 모델 생성 및 db 저장
        User user = User.toDomain(
                null,
                ProviderType.LOCAL,
                null,
                RoleType.ROLE_USER,
                requestDto.email(),
                encodedPassword,
                requestDto.nickname(),
                AuthorLevelType.of(requestDto.authorLevel()),
                OccupationType.of(requestDto.occupation()),
                topicIds,
                VisitSourceType.of(requestDto.visitSource()),
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

        // String domains → topicIds 변환
        List<Long> topicIds = requestDto.topics().stream()
                .map(findTopicUseCase::findTopicIdByName)
                .toList();

        // 유저 도메인 모델 생성 및 db 저장
        User user = User.toDomain(
                null,
                ProviderType.of(provider),
                providerId,
                RoleType.ROLE_USER,
                email,
                null,
                requestDto.nickname(),
                AuthorLevelType.of(requestDto.authorLevel()),
                OccupationType.of(requestDto.occupation()),
                topicIds,
                VisitSourceType.of(requestDto.visitSource()),
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
