package com.dataracy.modules.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CookieUtil 테스트")
class CookieUtilTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private CookieUtil cookieUtil;

    @BeforeEach
    void setUp() {
        // Set default profile to local for testing
        ReflectionTestUtils.setField(cookieUtil, "activeProfile", "local");
    }

    @Test
    @DisplayName("setCookie - HTTP 환경에서 쿠키 설정")
    void setCookie_ShouldSetCookieWithCorrectAttributes_WhenHttpEnvironment() {
        // Given
        when(request.getHeader("X-Forwarded-Proto")).thenReturn(null);
        when(request.getHeader("X-Forwarded-Ssl")).thenReturn(null);
        when(request.isSecure()).thenReturn(false);
        
        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        // When
        cookieUtil.setCookie(request, response, "testCookie", "testValue", 3600);

        // Then
        verify(response).addHeader(headerCaptor.capture(), valueCaptor.capture());
        assertThat(headerCaptor.getValue()).isEqualTo("Set-Cookie");
        String cookieValue = valueCaptor.getValue();
        assertAll(
                () -> assertThat(cookieValue).contains("testCookie=testValue"),
                () -> assertThat(cookieValue).contains("HttpOnly"),
                () -> assertThat(cookieValue).contains("SameSite=Lax"),
                () -> assertThat(cookieValue).contains("Path=/"),
                () -> assertThat(cookieValue).contains("Max-Age=3600"),
                () -> assertThat(cookieValue).doesNotContain("Secure"),
                () -> assertThat(cookieValue).doesNotContain("Domain")
        );
    }

    @Test
    @DisplayName("setCookie - HTTPS 환경에서 쿠키 설정")
    void setCookie_ShouldSetSecureCookie_WhenHttpsEnvironment() {
        // Given
        when(request.getHeader("X-Forwarded-Proto")).thenReturn("https");
        
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        // When
        cookieUtil.setCookie(request, response, "testCookie", "testValue", 3600);

        // Then
        verify(response).addHeader(eq("Set-Cookie"), valueCaptor.capture());
        String cookieValue = valueCaptor.getValue();
        assertAll(
                () -> assertThat(cookieValue).contains("Secure"),
                () -> assertThat(cookieValue).contains("SameSite=None")
        );
    }

    @Test
    @DisplayName("setCookie - 운영 환경에서 쿠키 설정")
    void setCookie_ShouldSetProductionCookie_WhenProdProfile() {
        // Given
        ReflectionTestUtils.setField(cookieUtil, "activeProfile", "prod");
        
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        // When
        cookieUtil.setCookie(request, response, "testCookie", "testValue", 3600);

        // Then
        verify(response).addHeader(eq("Set-Cookie"), valueCaptor.capture());
        String cookieValue = valueCaptor.getValue();
        assertAll(
                () -> assertThat(cookieValue).contains("Secure"),
                () -> assertThat(cookieValue).contains("SameSite=None"),
                () -> assertThat(cookieValue).contains("Domain=.dataracy.co.kr")
        );
    }

    @Test
    @DisplayName("getRefreshTokenFromCookies - refreshToken 쿠키가 존재할 때")
    void getRefreshTokenFromCookies_ShouldReturnToken_WhenRefreshTokenExists() {
        // Given
        Cookie refreshTokenCookie = new Cookie("refreshToken", "test-refresh-token");
        Cookie otherCookie = new Cookie("otherCookie", "otherValue");
        Cookie[] cookies = {refreshTokenCookie, otherCookie};
        
        when(request.getCookies()).thenReturn(cookies);

        // When
        Optional<String> result = cookieUtil.getRefreshTokenFromCookies(request);

        // Then
        assertAll(
                () -> assertThat(result).isPresent(),
                () -> assertThat(result).contains("test-refresh-token")
        );
    }

    @Test
    @DisplayName("getRefreshTokenFromCookies - refreshToken 쿠키가 존재하지 않을 때")
    void getRefreshTokenFromCookies_ShouldReturnEmpty_WhenRefreshTokenNotExists() {
        // Given
        Cookie otherCookie = new Cookie("otherCookie", "otherValue");
        Cookie[] cookies = {otherCookie};
        
        when(request.getCookies()).thenReturn(cookies);

        // When
        Optional<String> result = cookieUtil.getRefreshTokenFromCookies(request);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getRefreshTokenFromCookies - 쿠키가 null일 때")
    void getRefreshTokenFromCookies_ShouldReturnEmpty_WhenCookiesAreNull() {
        // Given
        when(request.getCookies()).thenReturn(null);

        // When
        Optional<String> result = cookieUtil.getRefreshTokenFromCookies(request);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deleteCookie - 쿠키 삭제")
    void deleteCookie_ShouldSetCookieWithZeroMaxAge() {
        // Given
        when(request.getHeader("X-Forwarded-Proto")).thenReturn(null);
        when(request.getHeader("X-Forwarded-Ssl")).thenReturn(null);
        when(request.isSecure()).thenReturn(false);
        
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        // When
        cookieUtil.deleteCookie(request, response, "testCookie");

        // Then
        verify(response).addHeader(eq("Set-Cookie"), valueCaptor.capture());
        String cookieValue = valueCaptor.getValue();
        assertAll(
                () -> assertThat(cookieValue).contains("testCookie="),
                () -> assertThat(cookieValue).contains("Max-Age=0"),
                () -> assertThat(cookieValue).contains("HttpOnly")
        );
    }

    @Test
    @DisplayName("deleteAllAuthCookies - 모든 인증 쿠키 삭제")
    void deleteAllAuthCookies_ShouldDeleteAllAuthenticationCookies() {
        // Given
        when(request.getHeader("X-Forwarded-Proto")).thenReturn(null);
        when(request.getHeader("X-Forwarded-Ssl")).thenReturn(null);
        when(request.isSecure()).thenReturn(false);

        // When
        cookieUtil.deleteAllAuthCookies(request, response);

        // Then
        verify(response, times(5)).addHeader(eq("Set-Cookie"), anyString());
        
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        verify(response, times(5)).addHeader(eq("Set-Cookie"), valueCaptor.capture());
        
        assertAll(
                () -> assertThat(valueCaptor.getAllValues()).anyMatch(cookie -> cookie.contains("accessToken=")),
                () -> assertThat(valueCaptor.getAllValues()).anyMatch(cookie -> cookie.contains("refreshToken=")),
                () -> assertThat(valueCaptor.getAllValues()).anyMatch(cookie -> cookie.contains("accessTokenExpiration=")),
                () -> assertThat(valueCaptor.getAllValues()).anyMatch(cookie -> cookie.contains("refreshTokenExpiration=")),
                () -> assertThat(valueCaptor.getAllValues()).anyMatch(cookie -> cookie.contains("registerToken="))
        );
    }

    @Test
    @DisplayName("getOrCreateAnonymousId - 기존 anonymousId 쿠키가 존재할 때")
    void getOrCreateAnonymousId_ShouldReturnExistingId_WhenAnonymousIdExists() {
        // Given
        Cookie anonymousIdCookie = new Cookie("anonymousId", "existing-anonymous-id");
        Cookie[] cookies = {anonymousIdCookie};
        
        when(request.getCookies()).thenReturn(cookies);

        // When
        String result = cookieUtil.getOrCreateAnonymousId(request, response);

        // Then
        assertThat(result).isEqualTo("existing-anonymous-id");
        verify(response, never()).addHeader(anyString(), anyString());
    }

    @Test
    @DisplayName("getOrCreateAnonymousId - anonymousId 쿠키가 존재하지 않을 때")
    void getOrCreateAnonymousId_ShouldCreateNewId_WhenAnonymousIdNotExists() {
        // Given
        Cookie otherCookie = new Cookie("otherCookie", "otherValue");
        Cookie[] cookies = {otherCookie};
        
        when(request.getCookies()).thenReturn(cookies);
        when(request.getHeader("X-Forwarded-Proto")).thenReturn(null);
        when(request.getHeader("X-Forwarded-Ssl")).thenReturn(null);
        when(request.isSecure()).thenReturn(false);

        // When
        String result = cookieUtil.getOrCreateAnonymousId(request, response);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
        );
        verify(response).addHeader(eq("Set-Cookie"), contains("anonymousId=" + result));
    }

    @Test
    @DisplayName("getOrCreateAnonymousId - 쿠키가 null일 때")
    void getOrCreateAnonymousId_ShouldCreateNewId_WhenCookiesAreNull() {
        // Given
        when(request.getCookies()).thenReturn(null);
        when(request.getHeader("X-Forwarded-Proto")).thenReturn(null);
        when(request.getHeader("X-Forwarded-Ssl")).thenReturn(null);
        when(request.isSecure()).thenReturn(false);

        // When
        String result = cookieUtil.getOrCreateAnonymousId(request, response);

        // Then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
        );
        verify(response).addHeader(eq("Set-Cookie"), contains("anonymousId=" + result));
    }

    @Test
    @DisplayName("isSecureEnvironment - X-Forwarded-Ssl 헤더로 HTTPS 감지")
    void setCookie_ShouldDetectHttps_WhenXForwardedSslHeader() {
        // Given
        when(request.getHeader("X-Forwarded-Proto")).thenReturn(null);
        when(request.getHeader("X-Forwarded-Ssl")).thenReturn("on");
        
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        // When
        cookieUtil.setCookie(request, response, "testCookie", "testValue", 3600);

        // Then
        verify(response).addHeader(eq("Set-Cookie"), valueCaptor.capture());
        String cookieValue = valueCaptor.getValue();
        assertAll(
                () -> assertThat(cookieValue).contains("Secure"),
                () -> assertThat(cookieValue).contains("SameSite=None")
        );
    }

    @Test
    @DisplayName("isSecureEnvironment - 개발 환경에서 도메인 설정")
    void setCookie_ShouldSetDomain_WhenDevProfile() {
        // Given
        ReflectionTestUtils.setField(cookieUtil, "activeProfile", "dev");
        
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        // When
        cookieUtil.setCookie(request, response, "testCookie", "testValue", 3600);

        // Then
        verify(response).addHeader(eq("Set-Cookie"), valueCaptor.capture());
        String cookieValue = valueCaptor.getValue();
        assertThat(cookieValue).contains("Domain=.dataracy.co.kr");
    }
}
