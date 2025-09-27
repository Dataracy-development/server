package com.dataracy.modules.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;

class WebRequestUtilTest {

    @BeforeEach
    void setUp() {
        // RequestContextHolder 초기화
        RequestContextHolder.resetRequestAttributes();
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 RequestContextHolder 정리
        RequestContextHolder.resetRequestAttributes();
    }

    @Nested
    @DisplayName("getCurrentRequestSafely 메서드 테스트")
    class GetCurrentRequestSafelyTest {

        @Test
        @DisplayName("요청 컨텍스트가 있을 때 HttpServletRequest 반환")
        void getCurrentRequestSafely_WhenRequestContextExists_ReturnsHttpServletRequest() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/test");
            ServletRequestAttributes attributes = new ServletRequestAttributes(request);
            RequestContextHolder.setRequestAttributes(attributes);

            // when
            HttpServletRequest result = WebRequestUtil.getCurrentRequestSafely();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(request);
        }

        @Test
        @DisplayName("요청 컨텍스트가 없을 때 null 반환")
        void getCurrentRequestSafely_WhenNoRequestContext_ReturnsNull() {
            // given
            RequestContextHolder.resetRequestAttributes();

            // when
            HttpServletRequest result = WebRequestUtil.getCurrentRequestSafely();

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("isLogExceptRequest 메서드 테스트")
    class IsLogExceptRequestTest {

        @Test
        @DisplayName("Swagger UI 경로일 때 true 반환")
        void isLogExceptRequest_WhenSwaggerPath_ReturnsTrue() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/swagger-ui/index.html");

            // when
            boolean result = WebRequestUtil.isLogExceptRequest(request);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("API 문서 경로일 때 true 반환")
        void isLogExceptRequest_WhenApiDocsPath_ReturnsTrue() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/v3/api-docs");

            // when
            boolean result = WebRequestUtil.isLogExceptRequest(request);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("정적 리소스 경로일 때 true 반환")
        void isLogExceptRequest_WhenStaticPath_ReturnsTrue() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/static/css/style.css");

            // when
            boolean result = WebRequestUtil.isLogExceptRequest(request);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("favicon 경로일 때 true 반환")
        void isLogExceptRequest_WhenFaviconPath_ReturnsTrue() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/favicon.ico");

            // when
            boolean result = WebRequestUtil.isLogExceptRequest(request);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("일반 API 경로일 때 false 반환")
        void isLogExceptRequest_WhenNormalApiPath_ReturnsFalse() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/users");

            // when
            boolean result = WebRequestUtil.isLogExceptRequest(request);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("request가 null일 때 false 반환")
        void isLogExceptRequest_WhenRequestIsNull_ReturnsFalse() {
            // when
            boolean result = WebRequestUtil.isLogExceptRequest(null);

            // then
            assertThat(result).isFalse();
        }
    }
}