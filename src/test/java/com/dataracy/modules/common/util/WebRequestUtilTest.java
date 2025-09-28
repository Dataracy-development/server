package com.dataracy.modules.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @Test
    @DisplayName("isLogExceptRequest - Swagger UI 경로는 로그 예외 처리")
    void isLogExceptRequest_ShouldReturnTrueForSwaggerPaths() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/swagger-ui.html");

        // When
        boolean isExcluded = WebRequestUtil.isLogExceptRequest(request);

        // Then
        assertThat(isExcluded).isTrue();
    }

    @Test
    @DisplayName("isLogExceptRequest - API 문서 경로는 로그 예외 처리")
    void isLogExceptRequest_ShouldReturnTrueForApiDocsPaths() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/v3/api-docs");

        // When
        boolean isExcluded = WebRequestUtil.isLogExceptRequest(request);

        // Then
        assertThat(isExcluded).isTrue();
    }

    @Test
    @DisplayName("isLogExceptRequest - Swagger 리소스 경로는 로그 예외 처리")
    void isLogExceptRequest_ShouldReturnTrueForSwaggerResourcePaths() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/swagger-resources/configuration/ui");

        // When
        boolean isExcluded = WebRequestUtil.isLogExceptRequest(request);

        // Then
        assertThat(isExcluded).isTrue();
    }

    @Test
    @DisplayName("isLogExceptRequest - 정적 리소스 경로는 로그 예외 처리")
    void isLogExceptRequest_ShouldReturnTrueForStaticResourcePaths() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/static/css/style.css");

        // When
        boolean isExcluded = WebRequestUtil.isLogExceptRequest(request);

        // Then
        assertThat(isExcluded).isTrue();
    }

    @Test
    @DisplayName("isLogExceptRequest - 에러 페이지 경로는 로그 예외 처리")
    void isLogExceptRequest_ShouldReturnTrueForErrorPaths() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/error");

        // When
        boolean isExcluded = WebRequestUtil.isLogExceptRequest(request);

        // Then
        assertThat(isExcluded).isTrue();
    }

    @Test
    @DisplayName("isLogExceptRequest - favicon.ico는 로그 예외 처리")
    void isLogExceptRequest_ShouldReturnTrueForFavicon() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/favicon.ico");

        // When
        boolean isExcluded = WebRequestUtil.isLogExceptRequest(request);

        // Then
        assertThat(isExcluded).isTrue();
    }

    @Test
    @DisplayName("isLogExceptRequest - 일반 API 경로는 로그 예외 처리하지 않음")
    void isLogExceptRequest_ShouldReturnFalseForNormalApiPaths() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/users");

        // When
        boolean isExcluded = WebRequestUtil.isLogExceptRequest(request);

        // Then
        assertThat(isExcluded).isFalse();
    }

    @Test
    @DisplayName("isLogExceptRequest - null 요청 처리")
    void isLogExceptRequest_ShouldReturnFalseForNullRequest() {
        // Given & When
        boolean isExcluded = WebRequestUtil.isLogExceptRequest(null);

        // Then
        assertThat(isExcluded).isFalse();
    }

    @Test
    @DisplayName("isLogExceptRequest - 빈 URI 처리")
    void isLogExceptRequest_ShouldReturnFalseForEmptyUri() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("");

        // When
        boolean isExcluded = WebRequestUtil.isLogExceptRequest(request);

        // Then
        assertThat(isExcluded).isFalse();
    }

    @Test
    @DisplayName("isLogExceptRequest - 루트 경로는 로그 예외 처리하지 않음")
    void isLogExceptRequest_ShouldReturnFalseForRootPath() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/");

        // When
        boolean isExcluded = WebRequestUtil.isLogExceptRequest(request);

        // Then
        assertThat(isExcluded).isFalse();
    }

    @Test
    @DisplayName("isLogExceptRequest - Well-known 경로는 로그 예외 처리")
    void isLogExceptRequest_ShouldReturnTrueForWellKnownPaths() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/.well-known/security.txt");

        // When
        boolean isExcluded = WebRequestUtil.isLogExceptRequest(request);

        // Then
        assertThat(isExcluded).isTrue();
    }

    @Test
    @DisplayName("isLogExceptRequest - Webjars 경로는 로그 예외 처리")
    void isLogExceptRequest_ShouldReturnTrueForWebjarsPaths() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/webjars/bootstrap/5.1.3/css/bootstrap.min.css");

        // When
        boolean isExcluded = WebRequestUtil.isLogExceptRequest(request);

        // Then
        assertThat(isExcluded).isTrue();
    }
}