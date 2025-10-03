/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.adapter.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;

class GoogleOAuthUserInfoAdapterTest {

  private GoogleOAuthUserInfoAdapter googleOAuthUserInfoAdapter;

  @BeforeEach
  void setUp() {
    googleOAuthUserInfoAdapter = new GoogleOAuthUserInfoAdapter();
  }

  @Test
  @DisplayName("extract - 구글 제공자로 유효한 속성을 추출한다")
  void extract_WhenGoogleProvider_ReturnsOAuthUserInfo() {
    // given
    String provider = "google";
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "test@example.com");
    attributes.put("name", "Test User");
    attributes.put("sub", "123456789");

    // when
    OAuthUserInfo result = googleOAuthUserInfoAdapter.extract(provider, attributes);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.email()).isEqualTo("test@example.com"),
        () -> assertThat(result.name()).isEqualTo("Test User"),
        () -> assertThat(result.provider()).isEqualTo("google"),
        () -> assertThat(result.providerId()).isEqualTo("123456789"));
  }

  @Test
  @DisplayName("extract - 구글 제공자로 null 속성을 추출한다")
  void extract_WhenGoogleProviderWithNullValues_ReturnsOAuthUserInfo() {
    // given
    String provider = "google";
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", null);
    attributes.put("name", null);
    attributes.put("sub", null);

    // when
    OAuthUserInfo result = googleOAuthUserInfoAdapter.extract(provider, attributes);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.email()).isNull(),
        () -> assertThat(result.name()).isNull(),
        () -> assertThat(result.provider()).isEqualTo("google"),
        () -> assertThat(result.providerId()).isNull());
  }

  @Test
  @DisplayName("extract - 구글 제공자로 일부 속성만 있는 경우를 처리한다")
  void extract_WhenGoogleProviderWithPartialValues_ReturnsOAuthUserInfo() {
    // given
    String provider = "google";
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "test@example.com");
    attributes.put("name", null);
    attributes.put("sub", "123456789");

    // when
    OAuthUserInfo result = googleOAuthUserInfoAdapter.extract(provider, attributes);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.email()).isEqualTo("test@example.com"),
        () -> assertThat(result.name()).isNull(),
        () -> assertThat(result.provider()).isEqualTo("google"),
        () -> assertThat(result.providerId()).isEqualTo("123456789"));
  }

  @Test
  @DisplayName("extract - 구글이 아닌 제공자로 호출하면 null을 반환한다")
  void extract_WhenNonGoogleProvider_ReturnsNull() {
    // given
    String provider = "kakao";
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "test@example.com");
    attributes.put("name", "Test User");
    attributes.put("sub", "123456789");

    // when
    OAuthUserInfo result = googleOAuthUserInfoAdapter.extract(provider, attributes);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("extract - 빈 속성 맵으로 호출한다")
  void extract_WhenEmptyAttributes_ReturnsOAuthUserInfo() {
    // given
    String provider = "google";
    Map<String, Object> attributes = new HashMap<>();

    // when
    OAuthUserInfo result = googleOAuthUserInfoAdapter.extract(provider, attributes);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.email()).isNull(),
        () -> assertThat(result.name()).isNull(),
        () -> assertThat(result.provider()).isEqualTo("google"),
        () -> assertThat(result.providerId()).isNull());
  }

  @Test
  @DisplayName("extract - null 제공자로 호출하면 예외가 발생한다")
  void extract_WhenNullProvider_ThrowsException() {
    // given
    String provider = null;
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "test@example.com");

    // when & then
    NullPointerException exception =
        catchThrowableOfType(
            () -> googleOAuthUserInfoAdapter.extract(provider, attributes),
            NullPointerException.class);
    assertAll(() -> assertThat(exception).isNotNull());
  }

  @Test
  @DisplayName("extract - null 속성으로 호출하면 예외가 발생한다")
  void extract_WhenNullAttributes_ThrowsException() {
    // given
    String provider = "google";
    Map<String, Object> attributes = null;

    // when & then
    NullPointerException exception =
        catchThrowableOfType(
            () -> googleOAuthUserInfoAdapter.extract(provider, attributes),
            NullPointerException.class);
    assertAll(() -> assertThat(exception).isNotNull());
  }
}
