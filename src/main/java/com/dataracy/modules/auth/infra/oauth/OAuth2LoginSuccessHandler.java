package com.dataracy.modules.auth.infra.oauth;

import com.dataracy.modules.auth.domain.model.OAuth2UserInfo;
import com.dataracy.modules.auth.infra.jwt.JwtUtil;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.user.application.UserApplicationService;
import com.dataracy.modules.user.application.UserQueryService;
import com.dataracy.modules.user.application.dto.LoginResponseDto;
import com.dataracy.modules.user.application.dto.RegisterTokenResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserApplicationService userApplicationService;
    private final UserQueryService userQueryService;

    /**
     * OAuth2 로그인 성공 후 처리.
     *
     * @param request        HTTP 요청 객체
     * @param response       HTTP 응답 객체
     * @param authentication 인증 객체
     * @throws IOException 리다이렉트 오류 발생 시 예외
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(token);
        // 신규 사용자 처리
        if (userQueryService.isNewUser(oAuth2UserInfo)) {
            RegisterTokenResponseDto registerTokenResponseDto = userQueryService.handleNewUser(oAuth2UserInfo);
            CookieUtil.setCookie(response, "registerToken", registerTokenResponseDto.registerToken(), (int) registerTokenResponseDto.registerTokenExpiration() / 1000);
            getRedirectStrategy().sendRedirect(request, response, jwtUtil.getRedirectOnboardingUrl());
        }
        // 기존 사용자 처리
        else {
            LoginResponseDto loginResponseDto = userQueryService.handleExistingUser(oAuth2UserInfo);
            CookieUtil.setCookie(response, "refreshToken", loginResponseDto.refreshToken(), (int) loginResponseDto.refreshTokenExpiration() / 1000);
            tokenRedisManager.saveRefreshToken(loginResponseDto.userId().toString(), loginResponseDto.refreshToken());
            getRedirectStrategy().sendRedirect(request, response, jwtUtil.getRedirectBaseUrl());
        }
    }
}
