package com.dataracy.modules.auth.adapter.handler;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RegisterTokenResponse;
import com.dataracy.modules.auth.application.port.out.oauth.SelectSocialProviderPort;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.user.application.port.in.command.auth.HandleUserUseCase;
import com.dataracy.modules.user.application.port.in.query.auth.IsNewUserUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final SelectSocialProviderPort selectSocialProviderPort;

    private final IsNewUserUseCase isNewUserUseCase;
    private final HandleUserUseCase handleUserUseCase;
    private final JwtProperties jwtProperties;

    /**
     * OAuth2 로그인 성공 후 처리.
     * 쿠키 설정 등 웹 관련은 컨트롤러 또는 핸들러에서 담당한다.
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

    /**
     * 신규 소셜 로그인 유저를 처리하여 레지스터 토큰을 쿠키에 저장하고 온보딩 페이지로 리다이렉트합니다.
     *
     * @param oAuthUserInfo 소셜 로그인에서 추출한 유저 정보
     * @param request 현재 HTTP 요청
     * @param response 현재 HTTP 응답
     * @throws IOException 리다이렉트 과정에서 입출력 오류가 발생할 경우
     */
    private void handleNewUser(OAuthUserInfo oAuthUserInfo, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 신규 유저일 경우 레지스터 토큰을 발급한다.
        RegisterTokenResponse registerTokenResponse = handleUserUseCase.handleNewUser(oAuthUserInfo);
        // 리프레시 토큰을 쿠키에 저장
        long expirationSeconds = registerTokenResponse.registerTokenExpiration() / 1000;
        int maxAge = expirationSeconds > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) expirationSeconds;

        // 레지스터 토큰을 쿠키에 저장한다.
        CookieUtil.setCookie(
                response,
                "registerToken",
                registerTokenResponse.registerToken(),
                maxAge
        );
        // 소셜 로그인 온보딩 페이지로 리다이렉션
        getRedirectStrategy().sendRedirect(request, response, jwtProperties.getRedirectOnboarding());
    }

    /**
     * 기존 유저에게 리프레시 토큰을 발급하여 쿠키에 저장한 뒤, 메인 페이지로 리다이렉트합니다.
     *
     * @param oAuthUserInfo OAuth2 인증을 통해 추출된 사용자 정보
     * @param request 현재 HTTP 요청 객체
     * @param response 현재 HTTP 응답 객체
     * @throws IOException 리다이렉트 과정에서 입출력 오류가 발생할 경우
     */
    private void handleExistingUser(OAuthUserInfo oAuthUserInfo, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 기존 유저일 경우 리프레시 토큰을 발급한다.
        RefreshTokenResponse refreshTokenResponseDto = handleUserUseCase.handleExistingUser(oAuthUserInfo);

        // 리프레시 토큰을 쿠키에 저장
        long expirationSeconds = refreshTokenResponseDto.refreshTokenExpiration() / 1000;
        int maxAge = expirationSeconds > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) expirationSeconds;

        // 리프레시 토큰을 쿠키에 저장한다.
        CookieUtil.setCookie(
                response,
                "refreshToken",
                refreshTokenResponseDto.refreshToken(),
                maxAge
        );
        // 메인페이지로 리다이렉션
        getRedirectStrategy().sendRedirect(request, response, jwtProperties.getRedirectBase());
    }
}
