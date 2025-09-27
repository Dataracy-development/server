package com.dataracy.modules.common.util;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtractHeaderUtilTest {

    @Mock
    private JwtValidateUseCase jwtValidateUseCase;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private ExtractHeaderUtil extractHeaderUtil;

    @BeforeEach
    void setUp() {
        extractHeaderUtil = new ExtractHeaderUtil(jwtValidateUseCase, cookieUtil);
    }

    @Test
    @DisplayName("extractAccessToken - Bearer 토큰을 성공적으로 추출한다")
    void extractAccessToken_ValidBearerToken_Success() {
        // given
        String token = "valid_token_123";
        String authHeader = "Bearer " + token;
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        // when
        Optional<String> result = ExtractHeaderUtil.extractAccessToken(request);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(token);
    }

    @Test
    @DisplayName("extractAccessToken - Authorization 헤더가 없으면 빈 Optional을 반환한다")
    void extractAccessToken_NoAuthorizationHeader_ReturnsEmpty() {
        // given
        when(request.getHeader("Authorization")).thenReturn(null);

        // when
        Optional<String> result = ExtractHeaderUtil.extractAccessToken(request);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("extractAccessToken - Bearer가 아닌 형식이면 빈 Optional을 반환한다")
    void extractAccessToken_InvalidFormat_ReturnsEmpty() {
        // given
        when(request.getHeader("Authorization")).thenReturn("Basic token123");

        // when
        Optional<String> result = ExtractHeaderUtil.extractAccessToken(request);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("extractAccessToken - Bearer만 있고 토큰이 없으면 빈 문자열을 반환한다")
    void extractAccessToken_BearerOnly_ReturnsEmptyString() {
        // given
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // when
        Optional<String> result = ExtractHeaderUtil.extractAccessToken(request);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEmpty();
    }

    @Test
    @DisplayName("extractAuthenticatedUserIdFromRequest - 유효한 토큰에서 사용자 ID를 추출한다")
    void extractAuthenticatedUserIdFromRequest_ValidToken_Success() {
        // given
        String token = "valid_token_123";
        String authHeader = "Bearer " + token;
        Long expectedUserId = 1L;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtValidateUseCase.getUserIdFromToken(token)).thenReturn(expectedUserId);

        // when
        Long result = extractHeaderUtil.extractAuthenticatedUserIdFromRequest(request);

        // then
        assertThat(result).isEqualTo(expectedUserId);
        verify(jwtValidateUseCase).getUserIdFromToken(token);
    }

    @Test
    @DisplayName("extractAuthenticatedUserIdFromRequest - 토큰이 없으면 null을 반환한다")
    void extractAuthenticatedUserIdFromRequest_NoToken_ReturnsNull() {
        // given
        when(request.getHeader("Authorization")).thenReturn(null);

        // when
        Long result = extractHeaderUtil.extractAuthenticatedUserIdFromRequest(request);

        // then
        assertThat(result).isNull();
        verify(jwtValidateUseCase, never()).getUserIdFromToken(anyString());
    }

    @Test
    @DisplayName("extractAuthenticatedUserIdFromRequest - 토큰 검증 실패 시 null을 반환한다")
    void extractAuthenticatedUserIdFromRequest_TokenValidationFails_ReturnsNull() {
        // given
        String token = "invalid_token";
        String authHeader = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtValidateUseCase.getUserIdFromToken(token)).thenThrow(new RuntimeException("Invalid token"));

        // when
        Long result = extractHeaderUtil.extractAuthenticatedUserIdFromRequest(request);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("extractViewerIdFromRequest - 인증된 사용자 ID를 반환한다")
    void extractViewerIdFromRequest_AuthenticatedUser_Success() {
        // given
        String token = "valid_token_123";
        String authHeader = "Bearer " + token;
        Long userId = 1L;
        String expectedUserId = "1";

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtValidateUseCase.getUserIdFromToken(token)).thenReturn(userId);

        // when
        String result = extractHeaderUtil.extractViewerIdFromRequest(request, response);

        // then
        assertThat(result).isEqualTo(expectedUserId);
        verify(jwtValidateUseCase).getUserIdFromToken(token);
        verify(cookieUtil, never()).getOrCreateAnonymousId(any(), any());
    }

    @Test
    @DisplayName("extractViewerIdFromRequest - 토큰이 없으면 익명 ID를 반환한다")
    void extractViewerIdFromRequest_NoToken_ReturnsAnonymousId() {
        // given
        String anonymousId = "anonymous_123";
        when(request.getHeader("Authorization")).thenReturn(null);
        when(cookieUtil.getOrCreateAnonymousId(request, response)).thenReturn(anonymousId);

        // when
        String result = extractHeaderUtil.extractViewerIdFromRequest(request, response);

        // then
        assertThat(result).isEqualTo(anonymousId);
        verify(cookieUtil).getOrCreateAnonymousId(request, response);
    }

    @Test
    @DisplayName("extractViewerIdFromRequest - 토큰 검증 실패 시 익명 ID를 반환한다")
    void extractViewerIdFromRequest_TokenValidationFails_ReturnsAnonymousId() {
        // given
        String token = "invalid_token";
        String authHeader = "Bearer " + token;
        String anonymousId = "anonymous_123";

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtValidateUseCase.getUserIdFromToken(token)).thenThrow(new RuntimeException("Invalid token"));
        when(cookieUtil.getOrCreateAnonymousId(request, response)).thenReturn(anonymousId);

        // when
        String result = extractHeaderUtil.extractViewerIdFromRequest(request, response);

        // then
        assertThat(result).isEqualTo(anonymousId);
        verify(cookieUtil).getOrCreateAnonymousId(request, response);
    }

    @Test
    @DisplayName("extractViewerIdFromRequest - 토큰에서 사용자 ID가 null이면 익명 ID를 반환한다")
    void extractViewerIdFromRequest_NullUserId_ReturnsAnonymousId() {
        // given
        String token = "valid_token_123";
        String authHeader = "Bearer " + token;
        String anonymousId = "anonymous_123";

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtValidateUseCase.getUserIdFromToken(token)).thenReturn(null);
        when(cookieUtil.getOrCreateAnonymousId(request, response)).thenReturn(anonymousId);

        // when
        String result = extractHeaderUtil.extractViewerIdFromRequest(request, response);

        // then
        assertThat(result).isEqualTo(anonymousId);
        verify(cookieUtil).getOrCreateAnonymousId(request, response);
    }

    @Test
    @DisplayName("extractViewerIdFromRequest - 토큰이 빈 문자열이면 익명 ID를 반환한다")
    void extractViewerIdFromRequest_EmptyToken_ReturnsAnonymousId() {
        // given
        String authHeader = "Bearer ";
        String anonymousId = "anonymous_123";

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(cookieUtil.getOrCreateAnonymousId(request, response)).thenReturn(anonymousId);
        when(jwtValidateUseCase.getUserIdFromToken("")).thenReturn(null);

        // when
        String result = extractHeaderUtil.extractViewerIdFromRequest(request, response);

        // then
        assertThat(result).isEqualTo(anonymousId);
        verify(cookieUtil).getOrCreateAnonymousId(request, response);
    }
}
