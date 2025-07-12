package com.dataracy.modules.auth.adapter.handler;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RegisterTokenResponse;
import com.dataracy.modules.auth.application.port.out.oauth.SelectSocialProviderPort;
import com.dataracy.modules.auth.application.port.out.redis.TokenRedisPort;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.user.application.port.in.auth.HandleUserUseCase;
import com.dataracy.modules.user.application.port.in.auth.IsNewUserUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 소셜 로그인 성공 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final SelectSocialProviderPort selectSocialProviderPort;
    private final IsNewUserUseCase isNewUserUseCase;
    private final HandleUserUseCase handleUserUseCase;

    private final TokenRedisPort tokenRedisPort;
    private final JwtProperties jwtProperties;

    /**
     * OAuth2 로그인 성공 후 처리.
     * 쿠키 설정, 레디스 설정 등 외부 I/O 부수 효과는 실패/성공 보장이 다르므로
     * 컨트롤러 또는 핸들러에서 처리해야한다.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param authentication 인증 객체
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 인증 객체에서 유저 정보 조회
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String provider = token.getAuthorizedClientRegistrationId();
        Map<String, Object> attributes = token.getPrincipal().getAttributes();

        // 어느 소셜 로그인을 사용했는지와 그에 맞게 유저 정보를 저장한다.
        OAuthUserInfo oAuthUserInfo = selectSocialProviderPort.extract(provider, attributes);

        // 신규 유저 처리
        if (isNewUserUseCase.isNewUser(oAuthUserInfo)) {
            handleNewUser(oAuthUserInfo, request, response);
        }
        // 기존 유저 처리
        else {
            handleExistingUser(oAuthUserInfo, request, response);
        }
    }

    // 신규 유저 처리
    private void handleNewUser(OAuthUserInfo oAuthUserInfo, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 신규 유저일 경우 레지스터 토큰을 발급한다.
        RegisterTokenResponse registerTokenResponse = handleUserUseCase.handleNewUser(oAuthUserInfo);
        // 레지스터 토큰을 쿠키에 저장한다.
        CookieUtil.setCookie(
                response,
                "registerToken",
                registerTokenResponse.registerToken(),
                (int) registerTokenResponse.registerTokenExpiration() / 1000);
        // 소셜 로그인 온보딩 페이지로 리다이렉션
        getRedirectStrategy().sendRedirect(request, response, jwtProperties.getRedirectOnboarding());
    }

    // 기존 유저 처리
    private void handleExistingUser(OAuthUserInfo oAuthUserInfo, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 기존 유저일 경우 리프레시 토큰을 발급한다.
        RefreshTokenResponse refreshTokenResponseDto = handleUserUseCase.handleExistingUser(oAuthUserInfo);
        // 리프레시 토큰을 쿠키에 저장한다.
        CookieUtil.setCookie(
                response,
                "refreshToken",
                refreshTokenResponseDto.refreshToken(),
                (int) refreshTokenResponseDto.refreshTokenExpiration() / 1000
        );
        // 메인페이지로 리다이렉션
        getRedirectStrategy().sendRedirect(request, response, jwtProperties.getRedirectBase());
    }
}
