package com.dataracy.modules.user.application.service.query.auth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RegisterTokenResponse;
import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.in.redis.TokenRedisUseCase;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.application.port.in.auth.HandleUserUseCase;
import com.dataracy.modules.user.application.port.in.auth.IsNewUserUseCase;
import com.dataracy.modules.user.application.port.in.validation.IsLoginPossibleUseCase;
import com.dataracy.modules.user.application.port.out.jpa.UserQueryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserAuthService implements
        IsNewUserUseCase,
        HandleUserUseCase,
        IsLoginPossibleUseCase
{
    private final PasswordEncoder passwordEncoder;

    private final UserQueryPort userQueryPort;

    private final JwtValidateUseCase jwtValidateUseCase;
    private final JwtGenerateUseCase jwtGenerateUseCase;
    private final TokenRedisUseCase tokenRedisUseCase;

    /****
     * 주어진 OAuth 사용자 정보로 해당 사용자가 기존에 존재하는지 확인하여 신규 사용자인지 여부를 반환합니다.
     *
     * @param oAuthUserInfo 소셜 인증에서 받은 사용자 정보
     * @return 사용자가 존재하지 않으면 true(신규 사용자), 존재하면 false
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isNewUser(OAuthUserInfo oAuthUserInfo) {
        Instant startTime = LoggerFactory.service().logStart("IsNewUserUseCase", "신규 유저 여부 확인 서비스 시작 email=" + oAuthUserInfo.email());
        boolean isNew = userQueryPort.findUserByProviderId(oAuthUserInfo.providerId()).isEmpty();
        LoggerFactory.service().logSuccess("IsNewUserUseCase", "신규 유저 여부 확인 서비스 성공 email=" + oAuthUserInfo.email(), startTime);

        return isNew;
    }

    /**
     * OAuth 제공자 정보를 기반으로 신규 사용자 등록용 JWT 토큰과 만료 시간을 반환합니다.
     *
     * @param oAuthUserInfo OAuth 인증을 통해 획득한 사용자 정보
     * @return 등록 토큰과 만료 시간이 포함된 RegisterTokenResponse
     */
    @Override
    public RegisterTokenResponse handleNewUser(OAuthUserInfo oAuthUserInfo) {
        Instant startTime = LoggerFactory.service().logStart("HandleUserUseCase", "신규 유저 시 핸들링 서비스 시작 email=" + oAuthUserInfo.email());
        String registerToken = jwtGenerateUseCase.generateRegisterToken(
                oAuthUserInfo.provider(),
                oAuthUserInfo.providerId(),
                oAuthUserInfo.email()
        );
        RegisterTokenResponse registerTokenResponse = new RegisterTokenResponse(registerToken, jwtValidateUseCase.getRegisterTokenExpirationTime());
        LoggerFactory.service().logSuccess("HandleUserUseCase", "신규 유저 핸들링 서비스 성공 email=" + oAuthUserInfo.email(), startTime);

        return registerTokenResponse;
    }

    /**
     * OAuth 제공자 ID로 기존 사용자를 조회하여 리프레시 토큰을 발급하고 Redis에 저장한 후, 토큰과 만료 정보를 반환합니다.
     *
     * 사용자가 존재하지 않을 경우 {@code UserException}이 발생합니다.
     *
     * @param oAuthUserInfo OAuth 제공자에서 받은 사용자 정보
     * @return 발급된 리프레시 토큰과 만료 시간을 포함하는 응답 객체
     */
    @Override
    @Transactional(readOnly = true)
    public RefreshTokenResponse handleExistingUser(OAuthUserInfo oAuthUserInfo) {
        Instant startTime = LoggerFactory.service().logStart("HandleUserUseCase", "기존 유저 핸들링 서비스 시작 email=" + oAuthUserInfo.email());
        User existUser = userQueryPort.findUserByProviderId(oAuthUserInfo.providerId())
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("HandleUserUseCase", "[기존 유저 처리] 소셜 식별자 아이디에 해당하는 사용자를 찾을 수 없습니다");
                    return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });

        String refreshToken = jwtGenerateUseCase.generateRefreshToken(existUser.getId(), existUser.getRole());
        tokenRedisUseCase.saveRefreshToken(existUser.getId().toString(), refreshToken);
        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(refreshToken, jwtValidateUseCase.getRefreshTokenExpirationTime());

        LoggerFactory.service().logSuccess("HandleUserUseCase", "기존 유저 핸들링 서비스 성공 email=" + oAuthUserInfo.email(), startTime);
        return refreshTokenResponse;
    }

    /**
     * 이메일과 비밀번호로 사용자의 로그인 자격을 검증하고, 인증에 성공한 경우 사용자 정보를 반환합니다.
     *
     * 사용자가 존재하지 않거나 비밀번호가 일치하지 않을 경우 `UserException`이 발생합니다.
     *
     * @param email 로그인에 사용할 이메일 주소
     * @param password 로그인에 사용할 비밀번호
     * @return 인증에 성공한 사용자의 정보
     */
    @Override
    @Transactional(readOnly = true)
    public UserInfo isLogin(String email, String password) {
        Instant startTime = LoggerFactory.service().logStart("IsLoginPossibleUseCase", "입력받은 이메일, 비밀번호로 로그인이 가능한지 여부를 확인하는 서비스 시작 email=" + email);

        User user = userQueryPort.findUserByEmail(email)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("IsLoginPossibleUseCase", "[로그인 가능 여부] 이메일에 해당하는 유저가 존재하지 않습니다. email=" + email);
                    return new UserException(UserErrorStatus.BAD_REQUEST_LOGIN);
                });

        if (!user.isPasswordMatch(passwordEncoder, password)) {
            LoggerFactory.service().logWarning("IsLoginPossibleUseCase", "[로그인 가능 여부] 제공받은 비밀번호와 실제 비밀번호가 일치하지 않습니다. 재로그인을 시도해주세요.");
            throw new UserException(UserErrorStatus.BAD_REQUEST_LOGIN);
        }

        UserInfo userInfo = user.toUserInfo();

        LoggerFactory.service().logSuccess("IsLoginPossibleUseCase", "입력받은 이메일, 비밀번호로 로그인이 가능한지 여부를 확인하는 서비스 성공 email=" + email, startTime);
        return userInfo;
    }
}
