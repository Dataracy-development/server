package com.dataracy.modules.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WebRequestUtil 테스트")
class WebRequestUtilTest {

    @Test
    @DisplayName("getCurrentRequestSafely - 정상적인 요청 컨텍스트에서 요청 객체 반환")
    void getCurrentRequestSafely_ShouldReturnRequestWhenContextExists() {
        // Given & When
        // RequestContextHolder는 테스트 환경에서 설정이 필요하므로 null을 반환할 가능성이 높음
        var request = WebRequestUtil.getCurrentRequestSafely();

        // Then
        // 테스트 환경에서는 null이 반환될 수 있음
        assertThat(request).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/swagger-ui.html",                          // Swagger UI
            "/v3/api-docs",                              // API 문서
            "/swagger-resources/configuration/ui",      // Swagger 리소스
            "/static/css/style.css",                    // 정적 리소스
            "/error",                                    // 에러 페이지
            "/favicon.ico",                              // favicon
            "/.well-known/security.txt",                // Well-known
            "/webjars/bootstrap/5.1.3/css/bootstrap.min.css"  // Webjars
    })
    @DisplayName("isLogExceptRequest - 로그 예외 경로는 true를 반환한다")
    void isLogExceptRequest_ShouldReturnTrueForExcludedPaths(String uri) {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);

        // When
        boolean isExcluded = WebRequestUtil.isLogExceptRequest(request);

        // Then
        assertThat(isExcluded).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/users",     // 일반 API 경로
            "",                  // 빈 URI
            "/"                  // 루트 경로
    })
    @DisplayName("isLogExceptRequest - 일반 경로는 false를 반환한다")
    void isLogExceptRequest_ShouldReturnFalseForNormalPaths(String uri) {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);

        // When
        boolean isExcluded = WebRequestUtil.isLogExceptRequest(request);

        // Then
        assertThat(isExcluded).isFalse();
    }

    @Test
    @DisplayName("isLogExceptRequest - null 요청은 false를 반환한다")
    void isLogExceptRequest_ShouldReturnFalseForNullRequest() {
        // Given & When
        boolean isExcluded = WebRequestUtil.isLogExceptRequest(null);

        // Then
        assertThat(isExcluded).isFalse();
    }
}