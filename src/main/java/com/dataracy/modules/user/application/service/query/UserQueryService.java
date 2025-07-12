package com.dataracy.modules.user.application.service.query;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RegisterTokenResponse;
import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.in.redis.TokenRedisUseCase;
import com.dataracy.modules.user.application.dto.request.ConfirmPasswordRequest;
import com.dataracy.modules.user.application.port.in.auth.HandleUserUseCase;
import com.dataracy.modules.user.application.port.in.auth.IsNewUserUseCase;
import com.dataracy.modules.user.application.port.in.user.ConfirmPasswordUseCase;
import com.dataracy.modules.user.application.port.in.user.IsLoginPossibleUseCase;
import com.dataracy.modules.user.application.port.out.UserRepositoryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryService implements IsNewUserUseCase, HandleUserUseCase, IsLoginPossibleUseCase, ConfirmPasswordUseCase {
    private final UserRepositoryPort userRepositoryPort;

    private final JwtValidateUseCase jwtValidateUseCase;
    private final JwtGenerateUseCase jwtGenerateUseCase;

    private final PasswordEncoder passwordEncoder;

    private final TokenRedisUseCase tokenRedisUseCase;
    /**
     * OAuth2 사용자 신규 여부 확인.
     *
     * @param oAuthUserInfo 소셜로부터 받은 oAuth2UserInfo
     * @return 신규 사용자 여부
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isNewUser(OAuthUserInfo oAuthUserInfo) {
        return userRepositoryPort.findUserByProviderId(oAuthUserInfo.providerId()) == null;
    }

    /**
     * 신규 사용자 처리.
     *
     * @param oAuthUserInfo    OAuth2 유저정보
     */
    @Override
    public RegisterTokenResponse handleNewUser(OAuthUserInfo oAuthUserInfo) {
        String registerToken = jwtGenerateUseCase.generateRegisterToken(
                oAuthUserInfo.provider(),
                oAuthUserInfo.providerId(),
                oAuthUserInfo.email()
        );
        log.info("신규 사용자 등록 토큰 생성 완료 - 이메일: {}", oAuthUserInfo.email());
        return new RegisterTokenResponse(registerToken, jwtValidateUseCase.getRegisterTokenExpirationTime());
    }

    /**
     * 기존 사용자 처리.
     * oAuth2UserInfo의 providerId를 토대로 기존유저의 정보를 반환한다.
     *
     * @param oAuthUserInfo    OAuth2 유저정보
     */
    @Override
    @Transactional(readOnly = true)
    public RefreshTokenResponse handleExistingUser(OAuthUserInfo oAuthUserInfo) {
        User existUser = userRepositoryPort.findUserByProviderId(oAuthUserInfo.providerId());
        long refreshTokenExpirationTime = jwtValidateUseCase.getRefreshTokenExpirationTime();
        String refreshToken = jwtGenerateUseCase.generateRefreshToken(existUser.getId(), existUser.getRole());

        // 리프레시 토큰 레디스 저장
        tokenRedisUseCase.saveRefreshToken(existUser.getId().toString(), refreshToken);
        log.info("기존 사용자 처리 완료: {}", existUser.getId());
        return new RefreshTokenResponse(refreshToken, refreshTokenExpirationTime);
    }

    /**
     * 이메일이 일치하는 유저를 db에서 찾는다.
     * @param email 이메일
     * @return 유저
     */
    @Override
    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        return userRepositoryPort.findUserByEmail(email);
    }

    /**
     * 비밀번호를 확인한다.
     *
     * @param userId 유저id
     * @param requestDto 비밀번호
     */
    @Override
    @Transactional(readOnly = true)
    public void confirmPassword(Long userId, ConfirmPasswordRequest requestDto) {
        User user = userRepositoryPort.findUserById(userId);
        boolean isMatched = passwordEncoder.matches(requestDto.password(), user.getPassword());
        if (!isMatched) {
            throw new UserException(UserErrorStatus.FAIL_CONFIRM_PASSWORD);
        }
    }
}
