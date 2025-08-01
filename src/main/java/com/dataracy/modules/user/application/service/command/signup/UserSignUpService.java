package com.dataracy.modules.user.application.service.command.signup;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.in.redis.TokenRedisUseCase;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.support.lock.DistributedLock;
import com.dataracy.modules.reference.application.port.in.authorlevel.ValidateAuthorLevelUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.ValidateOccupationUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.ValidateVisitSourceUseCase;
import com.dataracy.modules.user.application.dto.request.signup.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.signup.SelfSignUpRequest;
import com.dataracy.modules.user.application.port.in.signup.OAuthSignUpUseCase;
import com.dataracy.modules.user.application.port.in.signup.SelfSignUpUseCase;
import com.dataracy.modules.user.application.port.in.validation.DuplicateEmailUseCase;
import com.dataracy.modules.user.application.port.in.validation.DuplicateNicknameUseCase;
import com.dataracy.modules.user.application.port.out.jpa.UserCommandPort;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSignUpService implements SelfSignUpUseCase, OAuthSignUpUseCase {
    private final PasswordEncoder passwordEncoder;

    private final UserCommandPort userCommandPort;

    private final DuplicateNicknameUseCase duplicateNicknameUseCase;
    private final DuplicateEmailUseCase duplicateEmailUseCase;

    private final JwtGenerateUseCase jwtGenerateUseCase;
    private final JwtValidateUseCase jwtValidateUseCase;

    private final ValidateAuthorLevelUseCase validateAuthorLevelUseCase;
    private final ValidateOccupationUseCase validateOccupationUseCase;
    private final ValidateVisitSourceUseCase validateVisitSourceUseCase;
    private final ValidateTopicUseCase validateTopicUseCase;

    private final TokenRedisUseCase tokenRedisUseCase;

    @Value("${default.image.url:}")
    private String defaultImageUrl;

    /**
     * 자체 회원가입 요청을 처리하여 새로운 사용자를 등록하고 리프레시 토큰을 발급한다.
     *
     * 이메일, 닉네임, 비밀번호 등 필수 정보를 검증하고, 작성자 유형, 직업, 방문 경로, 토픽 등 선택 정보를 유효성 검사한 후 사용자 계정을 생성한다.
     * 중복 가입을 방지하기 위해 이메일 기준 분산 락을 적용한다.
     * 회원가입 성공 시 리프레시 토큰을 생성하여 Redis에 저장하고, 토큰과 만료 시간을 포함한 응답을 반환한다.
     *
     * @param requestDto 자체 회원가입 요청 정보
     * @return 리프레시 토큰과 만료 시간이 포함된 응답 객체
     */
    @Override
    @Transactional
    @DistributedLock(
            key = "'lock:signup:email:' + #requestDto.email()",
            waitTime = 300L,
            leaseTime = 2000L,
            retry = 2
    )
    public RefreshTokenResponse signUpSelf(SelfSignUpRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart("SelfSignUpUseCase", "자체 회원가입 서비스 시작 nickname=" + requestDto.nickname());

        // 자체 회원 가입 요청 정보 유효성 검사
        validateSignUpInfo(
                requestDto.email(),
                requestDto.nickname(),
                requestDto.authorLevelId(),
                requestDto.occupationId(),
                requestDto.visitSourceId(),
                requestDto.topicIds()
        );

        // 비밀번호 - 비밀번호 확인 검증
        requestDto.validatePasswordMatch();

        // 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.password());
        String providerId = UUID.randomUUID().toString();

        // 유저 도메인 모델 생성 및 db 저장
        User user = User.of(
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
                defaultImageUrl,
                requestDto.isAdTermsAgreed(),
                false
        );
        User savedUser = userCommandPort.saveUser(user);

        // 리프레시 토큰 발급 및 저장
        String refreshToken = jwtGenerateUseCase.generateRefreshToken(savedUser.getId(), RoleType.ROLE_USER);
        tokenRedisUseCase.saveRefreshToken(savedUser.getId().toString(), refreshToken);
        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(
                refreshToken,
                jwtValidateUseCase.getRefreshTokenExpirationTime()
        );

        LoggerFactory.service().logSuccess("SelfSignUpUseCase", "자체 회원가입 서비스 성공 nickname=" + requestDto.nickname(), startTime);
        return refreshTokenResponse;
    }

    /**
     * 소셜 로그인 기반 회원가입을 처리하고 리프레시 토큰을 발급합니다.
     *
     * 소셜 회원가입 토큰의 유효성을 검증한 후, 온보딩 정보를 바탕으로 신규 사용자를 등록합니다.
     * 이메일 및 닉네임 중복, 필수 및 선택 온보딩 항목의 유효성을 확인하고, 회원 정보를 저장한 뒤 리프레시 토큰을 생성하여 Redis에 저장합니다.
     * 동시 닉네임 회원가입을 방지하기 위해 분산 락을 사용합니다.
     *
     * @param registerToken 소셜 회원가입 토큰
     * @param requestDto 온보딩 요청 정보
     * @return 발급된 리프레시 토큰과 만료 시간 정보
     */
    @Override
    @Transactional
    @DistributedLock(
            key = "'lock:signup:nickname:' + #requestDto.nickname()",
            waitTime = 300L,
            leaseTime = 2000L,
            retry = 2
    )
    public RefreshTokenResponse signUpOAuth(String registerToken, OnboardingRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart("OAuthSignUpUseCase", "소셜 회원가입 서비스 시작 nickname=" + requestDto.nickname());

        // 레지스터 토큰 유효성 체크 및 정보 조회
        jwtValidateUseCase.validateToken(registerToken);
        String provider = jwtValidateUseCase.getProviderFromRegisterToken(registerToken);
        String providerId = jwtValidateUseCase.getProviderIdFromRegisterToken(registerToken);
        String email = jwtValidateUseCase.getEmailFromRegisterToken(registerToken);

        // 소셜 회원 가입 요청 정보 유효성 검사
        validateSignUpInfo(
                email,
                requestDto.nickname(),
                requestDto.authorLevelId(),
                requestDto.occupationId(),
                requestDto.visitSourceId(),
                requestDto.topicIds()
        );

        // 유저 도메인 모델 생성 및 db 저장
        User user = User.of(
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
                defaultImageUrl,
                requestDto.isAdTermsAgreed(),
                false
        );
        User savedUser = userCommandPort.saveUser(user);

        // 리프레시 토큰 발급 및 저장
        String refreshToken = jwtGenerateUseCase.generateRefreshToken(savedUser.getId(), RoleType.ROLE_USER);
        tokenRedisUseCase.saveRefreshToken(savedUser.getId().toString(), refreshToken);
        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(
                refreshToken,
                jwtValidateUseCase.getRefreshTokenExpirationTime()
        );

        LoggerFactory.service().logSuccess("OAuthSignUpUseCase", "소셜 회원가입 서비스 성공 nickname=" + requestDto.nickname(), startTime);
        return refreshTokenResponse;
    }

    /**
     * 회원가입 시 입력된 이메일, 닉네임, 작성자 유형, 직업, 방문 경로, 토픽 ID의 유효성을 검증한다.
     *
     * 이메일과 닉네임의 중복 여부를 확인하고, 작성자 유형의 존재를 필수로 검증한다.
     * 직업, 방문 경로, 토픽 ID는 값이 제공된 경우에만 각각의 유효성을 검사한다.
     */
    private void validateSignUpInfo(
            String email,
            String nickname,
            Long authorLevelId,
            Long occupationId,
            Long visitSourceId,
            List<Long> topicIds
    ) {
        // 이메일 중복 체크
        duplicateEmailUseCase.validateDuplicatedEmail(email);
        // 닉네임 중복 체크
        duplicateNicknameUseCase.validateDuplicatedNickname(nickname);

        // 작성자 유형 유효성 검사(필수 컬럼)
        validateAuthorLevelUseCase.validateAuthorLevel(authorLevelId);
        // 직업 유효성 검사(선택 컬럼)
        if (occupationId != null) {
            validateOccupationUseCase.validateOccupation(occupationId);
        }
        // 방문 경로 유효성 검사(선택 컬럼)
        if (visitSourceId != null) {
            validateVisitSourceUseCase.validateVisitSource(visitSourceId);
        }
        // 토픽 id를 통해 토픽 존재 유효성 검사를 시행한다.
        if (topicIds != null && !topicIds.isEmpty()) {
            for (Long topicId : topicIds) {
                validateTopicUseCase.validateTopic(topicId);
            }
        }
    }
}
