package com.dataracy.modules.user.application.service.command;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.in.redis.TokenRedisUseCase;
import com.dataracy.modules.common.support.lock.DistributedLock;
import com.dataracy.modules.reference.application.port.in.authorlevel.ValidateAuthorLevelUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.ValidateOccupationUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.ValidateVisitSourceUseCase;
import com.dataracy.modules.user.application.dto.request.ChangePasswordRequest;
import com.dataracy.modules.user.application.dto.request.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.SelfSignUpRequest;
import com.dataracy.modules.user.application.port.in.signup.OAuthSignUpUseCase;
import com.dataracy.modules.user.application.port.in.signup.SelfSignUpUseCase;
import com.dataracy.modules.user.application.port.in.user.ChangePasswordUseCase;
import com.dataracy.modules.user.application.port.out.UserRepositoryPort;
import com.dataracy.modules.user.application.service.query.UserValidationService;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
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
    private final PasswordEncoder passwordEncoder;

    private final UserRepositoryPort userRepositoryPort;
    private final UserValidationService userValidationService;

    private final JwtGenerateUseCase jwtGenerateUseCase;
    private final JwtValidateUseCase jwtValidateUseCase;

    private final ValidateTopicUseCase validateTopicUseCase;
    private final ValidateAuthorLevelUseCase validateAuthorLevelUseCase;
    private final ValidateOccupationUseCase validateOccupationUseCase;
    private final ValidateVisitSourceUseCase validateVisitSourceUseCase;

    private final TokenRedisUseCase tokenRedisUseCase;

    /**
     * 자체 회원가입 요청을 처리하여 새로운 사용자를 등록하고 리프레시 토큰을 발급한다.
     *
     * 이메일, 닉네임, 비밀번호 등 필수 정보를 검증하고, 작성자 유형, 직업, 방문 경로, 토픽 등 선택 정보를 유효성 검사 후 사용자 계정을 생성한다.
     * 중복 가입 방지를 위해 이메일 기준 분산 락을 적용하며, 회원가입 성공 시 리프레시 토큰을 생성하여 Redis에 저장하고 토큰과 만료 시간을 반환한다.
     *
     * @param requestDto 자체 회원가입 요청 정보
     * @return 리프레시 토큰과 만료 시간이 포함된 응답 객체
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
        // 비밀번호 - 비밀번호 확인 검증
        requestDto.validatePasswordMatch();

        // 이메일 중복 체크
        userValidationService.validateDuplicatedEmail(requestDto.email());
        // 닉네임 중복 체크
        userValidationService.validateDuplicatedNickname(requestDto.nickname());

        // 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.password());
        String providerId = UUID.randomUUID().toString();

        // 작성자 유형 유효성 검사(필수 컬럼)
        validateAuthorLevelUseCase.validateAuthorLevel(requestDto.authorLevelId());
        // 직업 유효성 검사(선택 컬럼)
        if (requestDto.occupationId() != null) {
            validateOccupationUseCase.validateOccupation(requestDto.occupationId());
        }
        // 방문 경로 유효성 검사(선택 컬럼)
        if (requestDto.visitSourceId() != null) {
            validateVisitSourceUseCase.validateVisitSource(requestDto.visitSourceId());
        }

        // 토픽 id를 통해 토픽 존재 유효성 검사를 시행한다.
        if (requestDto.topicIds() != null) {
            requestDto.topicIds()
                    .forEach(validateTopicUseCase::validateTopic);
        }

        // 유저 도메인 모델 생성 및 db 저장
        User user = User.toDomain(
                null,
                ProviderType.LOCAL,
                providerId,
                RoleType.ROLE_USER,
                requestDto.email(),
                encodedPassword,
                requestDto.nickname(),
                requestDto.authorLevelId(),
                requestDto.occupationId(),
                requestDto.topicIds(),
                requestDto.visitSourceId(),
                requestDto.isAdTermsAgreed(),
                false
        );
        User savedUser = userRepositoryPort.saveUser(user);

        // 리프레시 토큰 발급
        String refreshToken = jwtGenerateUseCase.generateRefreshToken(savedUser.getId(), RoleType.ROLE_USER);

        // 리프레시 토큰 레디스 저장
        tokenRedisUseCase.saveRefreshToken(savedUser.getId().toString(), refreshToken);

        log.info("자체 회원가입 성공: {}", user.getEmail());
        return new RefreshTokenResponse(
                refreshToken,
                jwtValidateUseCase.getRefreshTokenExpirationTime()
        );
    }

    /**
     * OAuth 기반 회원가입 요청을 처리하고 리프레시 토큰을 발급합니다.
     *
     * 소셜 로그인에서 발급된 회원가입 토큰과 온보딩 정보를 검증하여 신규 사용자를 등록합니다.
     * 이메일 및 닉네임 중복을 방지하고, 필수 및 선택 온보딩 항목의 유효성을 확인합니다.
     * 회원 정보 저장 후 리프레시 토큰을 생성하여 Redis에 저장하며, 토큰과 만료 시간을 반환합니다.
     *
     * @param registerToken 소셜 회원가입 토큰
     * @param requestDto 온보딩 요청 정보
     * @return 발급된 리프레시 토큰과 만료 시간 정보
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
        userValidationService.validateDuplicatedEmail(email);
        // 닉네임 중복 체크
        userValidationService.validateDuplicatedNickname(requestDto.nickname());

        // 작성자 유형 유효성 검사(필수 컬럼)
        validateAuthorLevelUseCase.validateAuthorLevel(requestDto.authorLevelId());
        // 직업 유효성 검사(선택 컬럼)
        if (requestDto.occupationId() != null) {
            validateOccupationUseCase.validateOccupation(requestDto.occupationId());
        }
        // 방문 경로 유효성 검사(선택 컬럼)
        if (requestDto.visitSourceId() != null) {
            validateVisitSourceUseCase.validateVisitSource(requestDto.visitSourceId());
        }

        // 토픽 id를 통해 토픽 존재 유효성 검사를 시행한다.
        if (requestDto.topicIds() != null) {
            requestDto.topicIds()
                    .forEach(validateTopicUseCase::validateTopic);
        }

        // 유저 도메인 모델 생성 및 db 저장
        User user = User.toDomain(
                null,
                ProviderType.of(provider),
                providerId,
                RoleType.ROLE_USER,
                email,
                null,
                requestDto.nickname(),
                requestDto.authorLevelId(),
                requestDto.occupationId(),
                requestDto.topicIds(),
                requestDto.visitSourceId(),
                requestDto.isAdTermsAgreed(),
                false
        );
        User savedUser = userRepositoryPort.saveUser(user);

        // 리프레시 토큰 발급
        String refreshToken = jwtGenerateUseCase.generateRefreshToken(savedUser.getId(), RoleType.ROLE_USER);

        // 리프레시 토큰 레디스 저장
        tokenRedisUseCase.saveRefreshToken(savedUser.getId().toString(), refreshToken);

        log.info("소셜 회원가입 성공: {}", email);
        return new RefreshTokenResponse(
                refreshToken,
                jwtValidateUseCase.getRefreshTokenExpirationTime()
        );
    }

    /**
     * 주어진 유저의 비밀번호를 새 비밀번호로 변경한다.
     *
     * Google 또는 Kakao 계정으로 가입한 유저는 비밀번호 변경이 불가하며, 존재하지 않는 유저 ID일 경우 예외가 발생한다.
     *
     * @param userId 비밀번호를 변경할 유저의 ID
     * @param requestDto 새 비밀번호 및 확인값이 포함된 요청 DTO
     */
    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest requestDto) {
        // 비밀번호 - 비밀번호 확인 검증
        requestDto.validatePasswordMatch();

        User savedUser = userRepositoryPort.findUserById(userId)
                .orElseThrow(() -> new UserException(UserErrorStatus.NOT_FOUND_USER));
        switch (savedUser.getProvider()) {
            case GOOGLE -> throw new UserException(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_GOOGLE);
            case KAKAO -> throw new UserException(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_KAKAO);
        }

        // 패스워드 암호화 및 변경
        String encodedPassword = passwordEncoder.encode(requestDto.password());
        userRepositoryPort.changePassword(userId, encodedPassword);
    }
}
