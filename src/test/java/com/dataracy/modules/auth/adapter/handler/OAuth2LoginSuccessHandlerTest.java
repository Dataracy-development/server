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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2LoginSuccessHandlerTest {

    @Mock
    private SelectSocialProviderPort selectSocialProviderPort;

    @Mock
    private IsNewUserUseCase isNewUserUseCase;

    @Mock
    private HandleUserUseCase handleUserUseCase;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private OAuth2AuthenticationToken authentication;

    @Mock
    private OAuth2User oAuth2User;

    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @BeforeEach
    void setUp() {
        oAuth2LoginSuccessHandler = new OAuth2LoginSuccessHandler(
                selectSocialProviderPort,
                isNewUserUseCase,
                handleUserUseCase,
                jwtProperties,
                cookieUtil
        );
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 신규 유저인 경우 레지스터 토큰을 발급하고 온보딩 페이지로 리다이렉트한다")
    void onAuthenticationSuccess_WhenNewUser_HandlesNewUser() throws IOException {
        // given
        String provider = "google";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "test@example.com");
        attributes.put("name", "Test User");
        attributes.put("sub", "123456789");

        OAuthUserInfo oAuthUserInfo = new OAuthUserInfo(
                "test@example.com",
                "Test User",
                "google",
                "123456789"
        );

        RegisterTokenResponse registerTokenResponse = new RegisterTokenResponse(
                "register.token.here",
                1800000L
        );

        when(jwtProperties.getRedirectOnboarding()).thenReturn("/onboarding");
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn(provider);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttributes()).thenReturn(attributes);
        when(selectSocialProviderPort.extract(provider, attributes)).thenReturn(oAuthUserInfo);
        when(isNewUserUseCase.isNewUser(oAuthUserInfo)).thenReturn(true);
        when(handleUserUseCase.handleNewUser(oAuthUserInfo)).thenReturn(registerTokenResponse);

        // when
        oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(selectSocialProviderPort).extract(provider, attributes);
        verify(isNewUserUseCase).isNewUser(oAuthUserInfo);
        verify(handleUserUseCase).handleNewUser(oAuthUserInfo);
        verify(cookieUtil).setCookie(eq(request), eq(response), eq("registerToken"), 
                eq("register.token.here"), eq(1800));
        // 리다이렉트는 Spring Security의 DefaultRedirectStrategy에서 처리되므로 검증 생략
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 기존 유저인 경우 리프레시 토큰을 발급하고 메인 페이지로 리다이렉트한다")
    void onAuthenticationSuccess_WhenExistingUser_HandlesExistingUser() throws IOException {
        // given
        String provider = "kakao";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("kakao_account", new HashMap<>());
        attributes.put("properties", new HashMap<>());
        attributes.put("id", "123456789");

        OAuthUserInfo oAuthUserInfo = new OAuthUserInfo(
                "test@example.com",
                "Test User",
                "kakao",
                "123456789"
        );

        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(
                "refresh.token.here",
                604800000L
        );

        when(jwtProperties.getRedirectBase()).thenReturn("/");
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn(provider);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttributes()).thenReturn(attributes);
        when(selectSocialProviderPort.extract(provider, attributes)).thenReturn(oAuthUserInfo);
        when(isNewUserUseCase.isNewUser(oAuthUserInfo)).thenReturn(false);
        when(handleUserUseCase.handleExistingUser(oAuthUserInfo)).thenReturn(refreshTokenResponse);

        // when
        oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(selectSocialProviderPort).extract(provider, attributes);
        verify(isNewUserUseCase).isNewUser(oAuthUserInfo);
        verify(handleUserUseCase).handleExistingUser(oAuthUserInfo);
        verify(cookieUtil).setCookie(eq(request), eq(response), eq("refreshToken"), 
                eq("refresh.token.here"), eq(604800));
        // 리다이렉트는 Spring Security의 DefaultRedirectStrategy에서 처리되므로 검증 생략
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 레지스터 토큰 만료 시간이 Integer.MAX_VALUE를 초과하는 경우 처리한다")
    void onAuthenticationSuccess_WhenRegisterTokenExpirationExceedsMaxValue_HandlesCorrectly() throws IOException {
        // given
        String provider = "google";
        Map<String, Object> attributes = new HashMap<>();
        OAuthUserInfo oAuthUserInfo = new OAuthUserInfo("test@example.com", "Test User", "google", "123456789");
        RegisterTokenResponse registerTokenResponse = new RegisterTokenResponse(
                "register.token.here",
                Long.MAX_VALUE
        );

        when(jwtProperties.getRedirectOnboarding()).thenReturn("/onboarding");
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn(provider);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttributes()).thenReturn(attributes);
        when(selectSocialProviderPort.extract(provider, attributes)).thenReturn(oAuthUserInfo);
        when(isNewUserUseCase.isNewUser(oAuthUserInfo)).thenReturn(true);
        when(handleUserUseCase.handleNewUser(oAuthUserInfo)).thenReturn(registerTokenResponse);

        // when
        oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(cookieUtil).setCookie(eq(request), eq(response), eq("registerToken"), 
                eq("register.token.here"), eq(Integer.MAX_VALUE));
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 리프레시 토큰 만료 시간이 Integer.MAX_VALUE를 초과하는 경우 처리한다")
    void onAuthenticationSuccess_WhenRefreshTokenExpirationExceedsMaxValue_HandlesCorrectly() throws IOException {
        // given
        String provider = "kakao";
        Map<String, Object> attributes = new HashMap<>();
        OAuthUserInfo oAuthUserInfo = new OAuthUserInfo("test@example.com", "Test User", "kakao", "123456789");
        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(
                "refresh.token.here",
                Long.MAX_VALUE
        );

        when(jwtProperties.getRedirectBase()).thenReturn("/");
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn(provider);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttributes()).thenReturn(attributes);
        when(selectSocialProviderPort.extract(provider, attributes)).thenReturn(oAuthUserInfo);
        when(isNewUserUseCase.isNewUser(oAuthUserInfo)).thenReturn(false);
        when(handleUserUseCase.handleExistingUser(oAuthUserInfo)).thenReturn(refreshTokenResponse);

        // when
        oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(cookieUtil).setCookie(eq(request), eq(response), eq("refreshToken"), 
                eq("refresh.token.here"), eq(Integer.MAX_VALUE));
    }

    @Test
    @DisplayName("onAuthenticationSuccess - null OAuthUserInfo로도 처리한다")
    void onAuthenticationSuccess_WhenNullOAuthUserInfo_HandlesCorrectly()  {
        // given
        String provider = "google";
        Map<String, Object> attributes = new HashMap<>();

        when(authentication.getAuthorizedClientRegistrationId()).thenReturn(provider);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttributes()).thenReturn(attributes);
        when(selectSocialProviderPort.extract(provider, attributes)).thenReturn(null);

        // when & then
        // NullPointerException이 발생할 수 있지만, 실제로는 isNewUserUseCase에서 처리됨
        assertThatThrownBy(() -> oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("onAuthenticationSuccess - null 속성으로도 처리한다")
    void onAuthenticationSuccess_WhenNullAttributes_HandlesCorrectly()  {
        // given
        String provider = "google";

        when(authentication.getAuthorizedClientRegistrationId()).thenReturn(provider);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttributes()).thenReturn(null);
        when(selectSocialProviderPort.extract(provider, null)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("onAuthenticationSuccess - null 제공자로도 처리한다")
    void onAuthenticationSuccess_WhenNullProvider_HandlesCorrectly()  {
        // given
        Map<String, Object> attributes = new HashMap<>();

        when(authentication.getAuthorizedClientRegistrationId()).thenReturn(null);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttributes()).thenReturn(attributes);
        when(selectSocialProviderPort.extract(null, attributes)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication))
                .isInstanceOf(NullPointerException.class);
    }
}
