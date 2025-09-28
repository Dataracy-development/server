package com.dataracy.modules.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ExtractHeaderUtil 테스트")
class ExtractHeaderUtilTest {

    @Test
    @DisplayName("extractAccessToken - 유효한 Bearer 토큰 추출")
    void extractAccessToken_ShouldExtractValidBearerToken() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token-123");

        // When
        Optional<String> token = ExtractHeaderUtil.extractAccessToken(request);

        // Then
        assertThat(token).isPresent();
        assertThat(token.get()).isEqualTo("valid-token-123");
    }

    @Test
    @DisplayName("extractAccessToken - Authorization 헤더가 없는 경우")
    void extractAccessToken_ShouldReturnEmptyWhenNoHeader() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // When
        Optional<String> token = ExtractHeaderUtil.extractAccessToken(request);

        // Then
        assertThat(token).isEmpty();
    }

    @Test
    @DisplayName("extractAccessToken - Bearer가 아닌 형식의 헤더")
    void extractAccessToken_ShouldReturnEmptyWhenNotBearer() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

        // When
        Optional<String> token = ExtractHeaderUtil.extractAccessToken(request);

        // Then
        assertThat(token).isEmpty();
    }

    @Test
    @DisplayName("extractAccessToken - Bearer 뒤에 공백이 없는 경우")
    void extractAccessToken_ShouldReturnEmptyWhenNoSpaceAfterBearer() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearervalid-token");

        // When
        Optional<String> token = ExtractHeaderUtil.extractAccessToken(request);

        // Then
        assertThat(token).isEmpty();
    }

    @Test
    @DisplayName("extractAccessToken - Bearer만 있고 토큰이 없는 경우")
    void extractAccessToken_ShouldReturnEmptyWhenOnlyBearer() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer ");

        // When
        Optional<String> token = ExtractHeaderUtil.extractAccessToken(request);

        // Then
        assertThat(token).isPresent();
        assertThat(token.get()).isEmpty();
    }

    @Test
    @DisplayName("extractAccessToken - null 헤더 값 처리")
    void extractAccessToken_ShouldHandleNullHeader() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        // null 헤더는 addHeader로 설정할 수 없으므로 헤더를 설정하지 않음

        // When
        Optional<String> token = ExtractHeaderUtil.extractAccessToken(request);

        // Then
        assertThat(token).isEmpty();
    }

    @Test
    @DisplayName("extractAccessToken - 빈 문자열 헤더 처리")
    void extractAccessToken_ShouldHandleEmptyHeader() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "");

        // When
        Optional<String> token = ExtractHeaderUtil.extractAccessToken(request);

        // Then
        assertThat(token).isEmpty();
    }

    @Test
    @DisplayName("extractAccessToken - 공백이 포함된 토큰 처리")
    void extractAccessToken_ShouldHandleTokenWithSpaces() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token with spaces");

        // When
        Optional<String> token = ExtractHeaderUtil.extractAccessToken(request);

        // Then
        assertThat(token).isPresent();
        assertThat(token.get()).isEqualTo("token with spaces");
    }

    @Test
    @DisplayName("extractAccessToken - 대소문자 구분 테스트")
    void extractAccessToken_ShouldBeCaseSensitive() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "bearer valid-token");

        // When
        Optional<String> token = ExtractHeaderUtil.extractAccessToken(request);

        // Then
        assertThat(token).isEmpty();
    }
}