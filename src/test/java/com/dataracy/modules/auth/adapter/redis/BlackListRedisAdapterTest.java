package com.dataracy.modules.auth.adapter.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/** BlackListRedisAdapter 테스트 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BlackListRedisAdapterTest {

  // Test constants
  private static final Long ONE_HOUR_IN_MILLIS = 3600000L;
  private static final Long NEGATIVE_EXPIRATION_MILLIS = -1000L;

  @Mock private StringRedisTemplate redisTemplate;

  @Mock private ValueOperations<String, String> valueOperations;

  private BlackListRedisAdapter adapter;

  @BeforeEach
  void setUp() {
    given(redisTemplate.opsForValue()).willReturn(valueOperations);
    adapter = new BlackListRedisAdapter(redisTemplate);
  }

  @Nested
  @DisplayName("setBlackListToken 메서드 테스트")
  class SetBlackListTokenTest {

    @Test
    @DisplayName("성공: 토큰을 블랙리스트에 등록")
    void setBlackListTokenSuccess() {
      // given
      String token = "test-jwt-token";
      long expirationMillis = ONE_HOUR_IN_MILLIS; // 1시간
      String expectedKey = "blacklist:" + token;

      // when
      adapter.setBlackListToken(token, expirationMillis);

      // then
      then(valueOperations)
          .should()
          .set(eq(expectedKey), eq("logout"), eq(Duration.ofMillis(expirationMillis)));
    }

    @Test
    @DisplayName("만료 시간이 0일 때도 정상 처리")
    void setBlackListTokenWithZeroExpiration() {
      // given
      String token = "test-token";
      long expirationMillis = 0L;
      String expectedKey = "blacklist:" + token;

      // when
      adapter.setBlackListToken(token, expirationMillis);

      // then
      then(valueOperations)
          .should()
          .set(eq(expectedKey), eq("logout"), eq(Duration.ofMillis(expirationMillis)));
    }

    @Test
    @DisplayName("음수 만료 시간일 때도 정상 처리")
    void setBlackListTokenWithNegativeExpiration() {
      // given
      String token = "test-token";
      long expirationMillis = NEGATIVE_EXPIRATION_MILLIS;
      String expectedKey = "blacklist:" + token;

      // when
      adapter.setBlackListToken(token, expirationMillis);

      // then
      then(valueOperations)
          .should()
          .set(eq(expectedKey), eq("logout"), eq(Duration.ofMillis(expirationMillis)));
    }
  }

  @Nested
  @DisplayName("isBlacklisted 메서드 테스트")
  class IsBlacklistedTest {

    @Test
    @DisplayName("토큰이 블랙리스트에 있을 때 true 반환")
    void isBlacklistedReturnsTrueWhenTokenExists() {
      // given
      String token = "blacklisted-token";
      String expectedKey = "blacklist:" + token;
      given(redisTemplate.hasKey(expectedKey)).willReturn(true);

      // when
      boolean result = adapter.isBlacklisted(token);

      // then
      assertThat(result).isTrue();
      then(redisTemplate).should().hasKey(expectedKey);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"valid-token", "test-token"})
    @DisplayName("블랙리스트에 없는 토큰은 false 반환 (null, 빈 문자열, 일반 토큰 모두 포함)")
    void isBlacklistedReturnsFalseWhenTokenNotExists(String token) {
      // given
      String expectedKey = "blacklist:" + token;
      given(redisTemplate.hasKey(expectedKey)).willReturn(false);

      // when
      boolean result = adapter.isBlacklisted(token);

      // then
      assertThat(result).isFalse();
      then(redisTemplate).should().hasKey(expectedKey);
    }

    @Test
    @DisplayName("Redis가 null을 반환할 때 false 반환")
    void isBlacklistedReturnsFalseWhenRedisReturnsNull() {
      // given
      String token = "test-token";
      String expectedKey = "blacklist:" + token;
      given(redisTemplate.hasKey(expectedKey)).willReturn(null);

      // when
      boolean result = adapter.isBlacklisted(token);

      // then
      assertThat(result).isFalse();
      then(redisTemplate).should().hasKey(expectedKey);
    }
  }
}
