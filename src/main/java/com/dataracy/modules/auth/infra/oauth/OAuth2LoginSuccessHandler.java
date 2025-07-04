package com.dataracy.modules.auth.infra.oauth;

import com.dataracy.modules.auth.application.JwtQueryService;
import com.dataracy.modules.auth.application.OAuthQueryService;
import com.dataracy.modules.auth.application.TokenApplicationService;
import com.dataracy.modules.auth.application.dto.response.RegisterTokenResponseDto;
import com.dataracy.modules.auth.domain.model.OAuth2UserInfo;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.user.application.dto.response.RefreshTokenResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuthQueryService oAuthQueryService;
    private final TokenApplicationService tokenApplicationService;

    @Value("${spring.jwt.redirect-onboarding}")
    private String onboardingRedirectUri;

    @Value("${spring.jwt.redirect-base}")
    private String baseRedirectUri;

    /**
     * OAuth2 로그인 성공 후 처리.
     * 쿠키 설정, 레디스 설정 등 외부 I/O 부수 효과는 실패/성공 보장이 다르므로
     * 컨트롤러 또는 핸들러에서 처리해야한다.
     *
     * @param request        HTTP 요청 객체
     * @param response       HTTP 응답 객체
     * @param authentication 인증 객체
     * @throws IOException 리다이렉트 오류 발생 시 예외
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String provider = token.getAuthorizedClientRegistrationId();
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes);
        // 신규 사용자 처리
        if (oAuthQueryService.isNewUser(oAuth2UserInfo)) {
            RegisterTokenResponseDto registerTokenResponseDto = oAuthQueryService.handleNewUser(oAuth2UserInfo);
            CookieUtil.setCookie(response, "registerToken", registerTokenResponseDto.registerToken(), (int) registerTokenResponseDto.registerTokenExpiration() / 1000);
            getRedirectStrategy().sendRedirect(request, response, onboardingRedirectUri);
        }
        // 기존 사용자 처리
        else {
            RefreshTokenResponseDto refreshTokenResponseDto = oAuthQueryService.handleExistingUser(oAuth2UserInfo);
            CookieUtil.setCookie(response, "refreshToken", refreshTokenResponseDto.refreshToken(), (int) refreshTokenResponseDto.refreshTokenExpiration() / 1000);
            tokenApplicationService.saveRefreshToken(refreshTokenResponseDto.userId().toString(), refreshTokenResponseDto.refreshToken());
            getRedirectStrategy().sendRedirect(request, response, baseRedirectUri);
        }
    }
}
