/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.auth.adapter.web.request.RefreshTokenWebRequest;
import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.auth.adapter.web.response.ReIssueTokenWebResponse;
import com.dataracy.modules.auth.adapter.web.response.RefreshTokenWebResponse;
import com.dataracy.modules.auth.application.dto.request.RefreshTokenRequest;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;

/** AuthDevWebMapper 테스트 */
@DisplayName("AuthDevWebMapper 테스트")
class AuthDevWebMapperTest {

  private final AuthDevWebMapper authDevWebMapper = new AuthDevWebMapper();

  @Test
  @DisplayName("자체 로그인 웹 요청 DTO를 애플리케이션 요청 DTO로 변환")
  void toApplicationDto_자체_로그인_웹_요청_변환_성공() {
    // given
    String email = "test@example.com";
    String password = "password123";
    SelfLoginWebRequest webRequest = new SelfLoginWebRequest(email, password);

    // when
    SelfLoginRequest result = authDevWebMapper.toApplicationDto(webRequest);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.email()).isEqualTo(email),
        () -> assertThat(result.password()).isEqualTo(password));
  }

  @Test
  @DisplayName("리프레시 토큰 발급 애플리케이션 응답 DTO를 웹 응답 DTO로 변환")
  void toWebDto_리프레시_토큰_발급_응답_변환_성공() {
    // given
    String refreshToken = "refresh_token_123";
    long expiration = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L; // 7일 후
    RefreshTokenResponse responseDto = new RefreshTokenResponse(refreshToken, expiration);

    // when
    RefreshTokenWebResponse result = authDevWebMapper.toWebDto(responseDto);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.refreshToken()).isEqualTo(refreshToken),
        () -> assertThat(result.refreshTokenExpiration()).isEqualTo(expiration));
  }

  @Test
  @DisplayName("토큰 재발급 웹 요청 DTO를 애플리케이션 요청 DTO로 변환")
  void toApplicationDto_토큰_재발급_웹_요청_변환_성공() {
    // given
    String refreshToken = "refresh_token_123";
    RefreshTokenWebRequest webRequest = new RefreshTokenWebRequest(refreshToken);

    // when
    RefreshTokenRequest result = authDevWebMapper.toApplicationDto(webRequest);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.refreshToken()).isEqualTo(refreshToken));
  }

  @Test
  @DisplayName("토큰 재발급 애플리케이션 응답 DTO를 웹 응답 DTO로 변환")
  void toWebDto_토큰_재발급_응답_변환_성공() {
    // given
    String accessToken = "access_token_123";
    String refreshToken = "refresh_token_123";
    long accessTokenExpiration = System.currentTimeMillis() + 60 * 60 * 1000L; // 1시간 후
    long refreshTokenExpiration = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L; // 7일 후

    ReIssueTokenResponse responseDto =
        new ReIssueTokenResponse(
            accessToken, refreshToken, accessTokenExpiration, refreshTokenExpiration);

    // when
    ReIssueTokenWebResponse result = authDevWebMapper.toWebDto(responseDto);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.accessToken()).isEqualTo(accessToken),
        () -> assertThat(result.refreshToken()).isEqualTo(refreshToken),
        () -> assertThat(result.accessTokenExpiration()).isEqualTo(accessTokenExpiration),
        () -> assertThat(result.refreshTokenExpiration()).isEqualTo(refreshTokenExpiration));
  }
}
