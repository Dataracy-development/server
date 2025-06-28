package com.dataracy.modules.user.application;

import com.dataracy.modules.auth.application.JwtApplicationService;
import com.dataracy.modules.auth.application.JwtQueryService;
import com.dataracy.modules.user.application.dto.request.OnboardingRequestDto;
import com.dataracy.modules.user.application.dto.response.LoginResponseDto;
import com.dataracy.modules.user.domain.enums.*;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.repository.UserRepository;
import com.dataracy.modules.user.status.UserErrorStatus;
import com.dataracy.modules.user.status.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserApplicationService {

    private final UserRepository userRepository;
    private final JwtApplicationService jwtApplicationService;
    private final JwtQueryService jwtQueryService;

    /**
     * 회원가입 처리.
     *
     * @param registerToken 회원가입 토큰
     * @param requestDto    회원가입 요청 정보
     */
    @Transactional
    public LoginResponseDto signupUserOAuth2(String registerToken, OnboardingRequestDto requestDto) {

        if (registerToken == null) {
            throw new UserException(UserErrorStatus.EXPIRED_REGISTER_TOKEN);
        }

        jwtQueryService.validateToken(registerToken);
        String provider = jwtQueryService.getProviderFromRegisterToken(registerToken);
        String providerId = jwtQueryService.getProviderIdFromRegisterToken(registerToken);
        String email = jwtQueryService.getEmailFromRegisterToken(registerToken);

        User user = User.toDomain(
                null,
                ProviderStatusType.of(provider),
                providerId,
                RoleStatusType.ROLE_USER,
                email,
                null,
                requestDto.nickname(),
                AuthorLevelStatusType.of(requestDto.authorLevel()),
                OccupationStatusType.of(requestDto.occupation()),
//                requestDto.domains(),
                VisitSourceStatusType.of(requestDto.visitSource()),
                requestDto.isAdTermsAgreed(),
                false
        );

        User savedUser = userRepository.saveUser(user);
        long refreshTokenExpirationTime = jwtQueryService.getRefreshTokenExpirationTime();
        String refreshToken = jwtApplicationService.generateAccessOrRefreshToken(savedUser.getId(), RoleStatusType.ROLE_USER, refreshTokenExpirationTime);

        log.info("소셜 회원가입 성공: {}", email);
        return new LoginResponseDto(savedUser.getId(), refreshToken, refreshTokenExpirationTime);
    }
}
