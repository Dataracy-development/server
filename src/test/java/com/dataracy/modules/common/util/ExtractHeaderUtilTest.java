/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;

@DisplayName("ExtractHeaderUtil 테스트")
class ExtractHeaderUtilTest {

  @ParameterizedTest(name = "extractAccessToken - {0}")
  @CsvSource({
    "'Bearer valid-token-123', 'valid-token-123', true",
    "'Bearer ', '', true",
    "'Bearer token with spaces', 'token with spaces', true"
  })
  @DisplayName("extractAccessToken - 다양한 Bearer 토큰 추출")
  void extractAccessToken_ShouldExtractVariousBearerTokens(
      String authHeader, String expectedToken, boolean shouldBePresent) {
    // Given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", authHeader);

    // When
    Optional<String> token = ExtractHeaderUtil.extractAccessToken(request);

    // Then
    if (shouldBePresent) {
      assertAll(
          () -> assertThat(token).isPresent(), () -> assertThat(token).contains(expectedToken));
    } else {
      assertThat(token).isEmpty();
    }
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
    "Basic dXNlcjpwYXNz", // Bearer가 아닌 형식
    "Bearervalid-token", // Bearer 뒤에 공백 없음
    "''", // 빈 문자열
    "bearer valid-token" // 대소문자 구분
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
  @DisplayName("extractAccessToken - Bearer 토큰이 공백만 있는 경우")
  void extractAccessToken_ShouldReturnWhitespaceTokenWhenTokenIsWhitespace() {
    // Given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer    ");

    // When
    Optional<String> token = ExtractHeaderUtil.extractAccessToken(request);

    // Then
    assertThat(token).isPresent().contains("   ");
  }

  @ParameterizedTest
  @ValueSource(strings = {"Basic token123", "Bearer"})
  @DisplayName("extractAccessToken - 유효하지 않은 Authorization 헤더 형식")
  void extractAccessToken_ShouldReturnEmptyForInvalidFormats(String invalidHeader) {
    // Given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", invalidHeader);

    // When
    Optional<String> token = ExtractHeaderUtil.extractAccessToken(request);

    // Then
    assertThat(token).isEmpty();
  }

  @Test
  @DisplayName("extractAccessToken - Bearer 공백만 있는 경우")
  void extractAccessToken_ShouldReturnEmptyForBearerWithOnlySpace() {
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
  @DisplayName("extractAccessToken - 정확한 Bearer 형식으로 토큰 추출")
  void extractAccessToken_ShouldExtractTokenWithCorrectBearerFormat() {
    // Given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer token123");

    // When
    Optional<String> token = ExtractHeaderUtil.extractAccessToken(request);

    // Then
    assertThat(token).isPresent().contains("token123");
  }
}
