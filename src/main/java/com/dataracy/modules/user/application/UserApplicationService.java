package com.dataracy.modules.user.application;

import com.dataracy.modules.auth.application.JwtApplicationService;
import com.dataracy.modules.auth.application.JwtQueryService;
import com.dataracy.modules.common.lock.DistributedLock;
import com.dataracy.modules.topic.application.TopicQueryService;
import com.dataracy.modules.user.application.dto.request.CheckNicknameRequestDto;
import com.dataracy.modules.user.application.dto.request.OnboardingRequestDto;
import com.dataracy.modules.user.application.dto.request.SelfLoginRequestDto;
import com.dataracy.modules.user.application.dto.request.SelfSignupRequestDto;
import com.dataracy.modules.user.application.dto.response.RefreshTokenResponseDto;
import com.dataracy.modules.user.domain.converter.AuthorLevelStatusTypeConverter;
import com.dataracy.modules.user.domain.converter.OccupationStatusTypeConverter;
import com.dataracy.modules.user.domain.converter.ProviderStatusTypeConverter;
import com.dataracy.modules.user.domain.converter.VisitSourceStatusTypeConverter;
import com.dataracy.modules.user.domain.enums.*;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.repository.UserRepository;
import com.dataracy.modules.user.status.UserErrorStatus;
import com.dataracy.modules.user.status.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;
    private final JwtApplicationService jwtApplicationService;
    private final JwtQueryService jwtQueryService;
    private final TopicQueryService topicQueryService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 클라이언트로부터 받은 유저 정보를 토대로 자체 회원가입을 진행한다.(이메일, 닉네임, 비밀번호, 성별)
     *
     * @param requestDto 자체 회원가입을 위한 Dto
     * @return LoginResponseDto (컨트롤러에서 리프레시 토큰 쿠키 저장을 위한 response)
     */
    @DistributedLock(
            key = "'lock:nickname:' + #requestDto.nickname()",
            waitTime = 300L,
            leaseTime = 2000L,
            retry = 2
    )
    @Transactional
    public RefreshTokenResponseDto signupUserSelf(SelfSignupRequestDto requestDto) {

        if (userRepository.existsByNickname(requestDto.nickname())) {
            throw new UserException(UserErrorStatus.DUPLICATED_NICKNAME);
        }

        if (userRepository.existsByEmail(requestDto.email())) {
            throw new UserException(UserErrorStatus.CONFLICT_DUPLICATE_EMAIL);
        }
        String encodedPassword = passwordEncoder.encode(requestDto.password());

        // 요청된 도메인(String) → topicId(Long) 변환
        List<Long> topicIds = requestDto.topics().stream()
                .map(topicQueryService::findTopicIdByName)
                .toList();

        User user = User.toDomain(
                null,
                ProviderStatusTypeConverter.of("LOCAL"),
                null,
                RoleStatusType.ROLE_USER,
                requestDto.email(),
                encodedPassword,
                requestDto.nickname(),
                AuthorLevelStatusTypeConverter.of(requestDto.authorLevel()),
                OccupationStatusTypeConverter.of(requestDto.occupation()),
                topicIds,
                VisitSourceStatusTypeConverter.of(requestDto.visitSource()),
                requestDto.isAdTermsAgreed(),
                false
        );

        User savedUser = userRepository.saveUser(user);
        long refreshTokenExpirationTime = jwtQueryService.getRefreshTokenExpirationTime();
        String refreshToken = jwtApplicationService.generateAccessOrRefreshToken(savedUser.getId(), RoleStatusType.ROLE_USER, refreshTokenExpirationTime);

        log.info("자체 회원가입 성공: {}", user.getEmail());
        return new RefreshTokenResponseDto(savedUser.getId(), refreshToken, jwtQueryService.getRefreshTokenExpirationTime());
    }

    /**
     * 회원가입 처리.
     * 닉네임 동시성 분산락 처리
     *
     * @param registerToken 회원가입 토큰
     * @param requestDto    회원가입 요청 정보
     */
    @DistributedLock(
            key = "'lock:nickname:' + #requestDto.nickname()",
            waitTime = 300L,
            leaseTime = 2000L,
            retry = 2
    )
    @Transactional
    public RefreshTokenResponseDto signupUserOAuth2(String registerToken, OnboardingRequestDto requestDto) {

        if (userRepository.existsByNickname(requestDto.nickname())) {
            throw new UserException(UserErrorStatus.DUPLICATED_NICKNAME);
        }

        if (registerToken == null) {
            throw new UserException(UserErrorStatus.EXPIRED_REGISTER_TOKEN);
        }

        jwtQueryService.validateToken(registerToken);
        String provider = jwtQueryService.getProviderFromRegisterToken(registerToken);
        String providerId = jwtQueryService.getProviderIdFromRegisterToken(registerToken);
        String email = jwtQueryService.getEmailFromRegisterToken(registerToken);

        if (userRepository.findUserByProviderId(providerId) != null){
            throw new UserException(UserErrorStatus.ALREADY_SIGN_UP_USER);
        }

        // String domains → topicIds 변환
        List<Long> topicIds = requestDto.topics().stream()
                .map(topicQueryService::findTopicIdByName)
                .toList();

        User user = User.toDomain(
                null,
                ProviderStatusTypeConverter.of(provider),
                providerId,
                RoleStatusType.ROLE_USER,
                email,
                null,
                requestDto.nickname(),
                AuthorLevelStatusTypeConverter.of(requestDto.authorLevel()),
                OccupationStatusTypeConverter.of(requestDto.occupation()),
                topicIds,
                VisitSourceStatusTypeConverter.of(requestDto.visitSource()),
                requestDto.isAdTermsAgreed(),
                false
        );

        User savedUser = userRepository.saveUser(user);
        long refreshTokenExpirationTime = jwtQueryService.getRefreshTokenExpirationTime();
        String refreshToken = jwtApplicationService.generateAccessOrRefreshToken(savedUser.getId(), RoleStatusType.ROLE_USER, refreshTokenExpirationTime);

        log.info("소셜 회원가입 성공: {}", email);
        return new RefreshTokenResponseDto(savedUser.getId(), refreshToken, refreshTokenExpirationTime);
    }

    /**
     * 닉네임 중복 확인.
     *
     * @param requestDto 닉네임 중복 확인 요청 정보
     */
    @Transactional(readOnly = true)
    public void checkNickname(CheckNicknameRequestDto requestDto) {

        String nickname = requestDto.nickname();

        if (userRepository.existsByNickname(nickname)) {
            throw new UserException(UserErrorStatus.DUPLICATED_NICKNAME);
        }

        log.info("닉네임 사용 가능: {}", nickname);
    }
}
