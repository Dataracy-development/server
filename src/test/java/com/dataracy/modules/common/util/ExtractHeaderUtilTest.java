package com.dataracy.modules.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
        assertAll(
                () -> assertThat(token).isPresent(),
                () -> assertThat(token).contains("valid-token-123")
        );
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

    @ParameterizedTest
    @CsvSource({
            "Basic dXNlcjpwYXNz",      // Bearer가 아닌 형식
            "Bearervalid-token",        // Bearer 뒤에 공백 없음
            "''",                       // 빈 문자열
            "bearer valid-token"        // 대소문자 구분
    })
    @DisplayName("extractAccessToken - 유효하지 않은 Authorization 헤더는 empty를 반환한다")
    void extractAccessToken_ShouldReturnEmptyWhenInvalidHeader(String authHeader) {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (!authHeader.isEmpty()) {
            request.addHeader("Authorization", authHeader);
        }

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
        assertAll(
                () -> assertThat(token).isPresent(),
                () -> assertThat(token).contains("")
        );
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
        assertAll(
                () -> assertThat(token).isPresent(),
                () -> assertThat(token).contains("token with spaces")
        );
    }
}