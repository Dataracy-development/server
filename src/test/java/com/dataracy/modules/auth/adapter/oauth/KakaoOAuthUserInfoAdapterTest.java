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

class KakaoOAuthUserInfoAdapterTest {

  // Test constants
  private static final Integer CURRENT_YEAR = 2024;

  private KakaoOAuthUserInfoAdapter kakaoOAuthUserInfoAdapter;

  @BeforeEach
  void setUp() {
    kakaoOAuthUserInfoAdapter = new KakaoOAuthUserInfoAdapter();
  }

  @Test
  @DisplayName("extract - 카카오 제공자로 유효한 속성을 추출한다")
  void extractWhenKakaoProviderReturnsOAuthUserInfo() {
    // given
    String provider = "kakao";
    Map<String, Object> kakaoAccount = new HashMap<>();
    kakaoAccount.put("email", "test@example.com");

    Map<String, Object> properties = new HashMap<>();
    properties.put("nickname", "Test User");

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("kakao_account", kakaoAccount);
    attributes.put("properties", properties);
    attributes.put("id", "14562");

    // when
    OAuthUserInfo result = kakaoOAuthUserInfoAdapter.extract(provider, attributes);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.email()).isEqualTo("test@example.com"),
        () -> assertThat(result.name()).isEqualTo("Test User"),
        () -> assertThat(result.provider()).isEqualTo("kakao"),
        () -> assertThat(result.providerId()).isEqualTo("14562"));
  }

  @Test
  @DisplayName("extract - 카카오 제공자로 null 속성을 추출한다")
  void extractWhenKakaoProviderWithNullValuesReturnsOAuthUserInfo() {
    // given
    String provider = "kakao";
    Map<String, Object> kakaoAccount = new HashMap<>();
    kakaoAccount.put("email", null);

    Map<String, Object> properties = new HashMap<>();
    properties.put("nickname", null);

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("kakao_account", kakaoAccount);
    attributes.put("properties", properties);
    attributes.put("id", null);

    // when
    OAuthUserInfo result = kakaoOAuthUserInfoAdapter.extract(provider, attributes);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.email()).isNull(),
        () -> assertThat(result.name()).isNull(),
        () -> assertThat(result.provider()).isEqualTo("kakao"),
        () -> assertThat(result.providerId()).isNull());
  }

  @Test
  @DisplayName("extract - 카카오 제공자로 일부 속성만 있는 경우를 처리한다")
  void extractWhenKakaoProviderWithPartialValuesReturnsOAuthUserInfo() {
    // given
    String provider = "kakao";
    Map<String, Object> kakaoAccount = new HashMap<>();
    kakaoAccount.put("email", "test@example.com");

    Map<String, Object> properties = new HashMap<>();
    properties.put("nickname", null);

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("kakao_account", kakaoAccount);
    attributes.put("properties", properties);
    attributes.put("id", "14562");

    // when
    OAuthUserInfo result = kakaoOAuthUserInfoAdapter.extract(provider, attributes);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.email()).isEqualTo("test@example.com"),
        () -> assertThat(result.name()).isNull(),
        () -> assertThat(result.provider()).isEqualTo("kakao"),
        () -> assertThat(result.providerId()).isEqualTo("14562"));
  }

  @Test
  @DisplayName("extract - 카카오가 아닌 제공자로 호출하면 null을 반환한다")
  void extractWhenNonKakaoProviderReturnsNull() {
    // given
    String provider = "google";
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("kakao_account", new HashMap<>());
    attributes.put("properties", new HashMap<>());
    attributes.put("id", "14562");

    // when
    OAuthUserInfo result = kakaoOAuthUserInfoAdapter.extract(provider, attributes);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("extract - 빈 속성 맵으로 호출하면 예외가 발생한다")
  void extractWhenEmptyAttributesThrowsException() {
    // given
    String provider = "kakao";
    Map<String, Object> attributes = new HashMap<>();

    // when & then
    NullPointerException exception =
        catchThrowableOfType(
            () -> kakaoOAuthUserInfoAdapter.extract(provider, attributes),
            NullPointerException.class);
    assertAll(() -> assertThat(exception).isNotNull());
  }

  @Test
  @DisplayName("extract - null 제공자로 호출하면 예외가 발생한다")
  void extractWhenNullProviderThrowsException() {
    // given
    String provider = null;
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("kakao_account", new HashMap<>());

    // when & then
    NullPointerException exception =
        catchThrowableOfType(
            () -> kakaoOAuthUserInfoAdapter.extract(provider, attributes),
            NullPointerException.class);
    assertAll(() -> assertThat(exception).isNotNull());
  }

  @Test
  @DisplayName("extract - null 속성으로 호출하면 예외가 발생한다")
  void extractWhenNullAttributesThrowsException() {
    // given
    String provider = "kakao";
    Map<String, Object> attributes = null;

    // when & then
    NullPointerException exception =
        catchThrowableOfType(
            () -> kakaoOAuthUserInfoAdapter.extract(provider, attributes),
            NullPointerException.class);
    assertAll(() -> assertThat(exception).isNotNull());
  }

  @Test
  @DisplayName("extract - kakao_account가 null인 경우 예외가 발생한다")
  void extractWhenKakaoAccountIsNullThrowsException() {
    // given
    String provider = "kakao";
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("kakao_account", null);
    attributes.put("properties", new HashMap<>());
    attributes.put("id", "14562");

    // when & then
    NullPointerException exception =
        catchThrowableOfType(
            () -> kakaoOAuthUserInfoAdapter.extract(provider, attributes),
            NullPointerException.class);
    assertAll(() -> assertThat(exception).isNotNull());
  }

  @Test
  @DisplayName("extract - properties가 null인 경우 예외가 발생한다")
  void extractWhenPropertiesIsNullThrowsException() {
    // given
    String provider = "kakao";
    Map<String, Object> kakaoAccount = new HashMap<>();
    kakaoAccount.put("email", "test@example.com");

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("kakao_account", kakaoAccount);
    attributes.put("properties", null);
    attributes.put("id", "14562");

    // when & then
    NullPointerException exception =
        catchThrowableOfType(
            () -> kakaoOAuthUserInfoAdapter.extract(provider, attributes),
            NullPointerException.class);
    assertAll(() -> assertThat(exception).isNotNull());
  }
}
