/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.adapter.ratelimit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.concurrent.TimeUnit;

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
import org.springframework.test.util.ReflectionTestUtils;

/** RedisRateLimitAdapter 테스트 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RedisRateLimitAdapterTest {

  @Mock private StringRedisTemplate redisTemplate;

  @Mock private ValueOperations<String, String> valueOperations;

  private RedisRateLimitAdapter adapter;

  @BeforeEach
  void setUp() {
    given(redisTemplate.opsForValue()).willReturn(valueOperations);
    adapter = new RedisRateLimitAdapter(redisTemplate);

    // @Value 필드 설정
    ReflectionTestUtils.setField(adapter, "defaultMaxRequests", 10);
    ReflectionTestUtils.setField(adapter, "defaultWindowMinutes", 1);
  }

  @Nested
  @DisplayName("isAllowed 메서드 테스트")
  class IsAllowedTest {

    @Test
    @DisplayName("성공: 요청 허용 (카운트가 제한보다 적음)")
    void isAllowed_카운트가제한보다적음_true반환() {
      // given
      String key = "192.168.1.1";
      int maxRequests = 5;
      int windowMinutes = 1;
      String redisKey = "rate_limit:" + key;
      given(valueOperations.increment(redisKey, 1)).willReturn(3L);

      // when
      boolean result = adapter.isAllowed(key, maxRequests, windowMinutes);

      // then
      assertThat(result).isTrue();
      then(valueOperations).should().increment(redisKey, 1);
      then(redisTemplate).should(never()).expire(anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("요청 차단: 카운트가 제한과 같음")
    void isAllowed_카운트가제한과같음_false반환() {
      // given
      String key = "192.168.1.1";
      int maxRequests = 5;
      int windowMinutes = 1;
      String redisKey = "rate_limit:" + key;
      given(valueOperations.increment(redisKey, 1)).willReturn(5L);

      // when
      boolean result = adapter.isAllowed(key, maxRequests, windowMinutes);

      // then
      assertThat(result).isTrue();
      then(valueOperations).should().increment(redisKey, 1);
      then(redisTemplate).should(never()).expire(anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("요청 차단: 카운트가 제한보다 많음")
    void isAllowed_카운트가제한보다많음_false반환() {
      // given
      String key = "192.168.1.1";
      int maxRequests = 5;
      int windowMinutes = 1;
      String redisKey = "rate_limit:" + key;
      given(valueOperations.increment(redisKey, 1)).willReturn(6L);

      // when
      boolean result = adapter.isAllowed(key, maxRequests, windowMinutes);

      // then
      assertThat(result).isFalse();
      then(valueOperations).should().increment(redisKey, 1);
      then(redisTemplate).should(never()).expire(anyString(), anyLong(), any(TimeUnit.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("key가 null이거나 빈 문자열 또는 공백일 때 true 반환하고 Redis 호출 안 함")
    void isAllowed_key가유효하지않음_true반환(String key) {
      // given
      int maxRequests = 5;
      int windowMinutes = 1;

      // when
      boolean result = adapter.isAllowed(key, maxRequests, windowMinutes);

      // then
      assertThat(result).isTrue();
      then(valueOperations).should(never()).increment(anyString(), anyLong());
    }

    @Test
    @DisplayName("Redis에서 null 반환 시 안전을 위해 차단")
    void isAllowed_Redis에서null반환_false반환() {
      // given
      String key = "192.168.1.1";
      int maxRequests = 5;
      int windowMinutes = 1;
      String redisKey = "rate_limit:" + key;
      given(valueOperations.increment(redisKey, 1)).willReturn(null);

      // when
      boolean result = adapter.isAllowed(key, maxRequests, windowMinutes);

      // then
      assertThat(result).isFalse();
      then(valueOperations).should().increment(redisKey, 1);
    }

    @Test
    @DisplayName("첫 번째 요청일 때 TTL 설정")
    void isAllowed_첫요청일때_TTL설정() {
      // given
      String key = "192.168.1.1";
      int maxRequests = 5;
      int windowMinutes = 1;
      String redisKey = "rate_limit:" + key;
      given(valueOperations.increment(redisKey, 1)).willReturn(1L);

      // when
      boolean result = adapter.isAllowed(key, maxRequests, windowMinutes);

      // then
      assertThat(result).isTrue();
      then(valueOperations).should().increment(redisKey, 1);
      then(redisTemplate).should().expire(redisKey, windowMinutes, TimeUnit.MINUTES);
    }
  }

  @Nested
  @DisplayName("incrementRequestCount 메서드 테스트")
  class IncrementRequestCountTest {

    @Test
    @DisplayName("성공: 첫 번째 요청 시 TTL 설정")
    void incrementRequestCount_첫요청_TTL설정() {
      // given
      String key = "192.168.1.1";
      int incrementBy = 1;
      String redisKey = "rate_limit:" + key;
      given(valueOperations.increment(redisKey, incrementBy)).willReturn(1L);

      // when
      adapter.incrementRequestCount(key, incrementBy);

      // then
      then(valueOperations).should().increment(redisKey, incrementBy);
      then(redisTemplate).should().expire(redisKey, 1, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("성공: 기존 요청 시 TTL 설정하지 않음")
    void incrementRequestCount_기존요청_TTL설정하지않음() {
      // given
      String key = "192.168.1.1";
      int incrementBy = 1;
      String redisKey = "rate_limit:" + key;
      given(valueOperations.increment(redisKey, incrementBy)).willReturn(5L);

      // when
      adapter.incrementRequestCount(key, incrementBy);

      // then
      then(valueOperations).should().increment(redisKey, incrementBy);
      then(redisTemplate).should(never()).expire(anyString(), anyLong(), any(TimeUnit.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("key가 null이거나 빈 문자열 또는 공백일 때 아무것도 하지 않음")
    void incrementRequestCount_key가유효하지않음_아무것도하지않음(String key) {
      // given
      int incrementBy = 1;

      // when
      adapter.incrementRequestCount(key, incrementBy);

      // then
      then(valueOperations).should(never()).increment(anyString(), anyInt());
      then(redisTemplate).should(never()).expire(anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("Redis 예외 발생 시 로그만 남기고 계속 진행")
    void incrementRequestCount_Redis예외발생_로그만남기고계속진행() {
      // given
      String key = "192.168.1.1";
      int incrementBy = 1;
      String redisKey = "rate_limit:" + key;
      willThrow(new RuntimeException("Redis error"))
          .given(valueOperations)
          .increment(redisKey, incrementBy);

      // when & then (예외가 발생하지 않아야 함)
      adapter.incrementRequestCount(key, incrementBy);

      // then
      then(valueOperations).should().increment(redisKey, incrementBy);
      then(redisTemplate).should(never()).expire(anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("incrementBy가 1보다 클 때도 정상 처리")
    void incrementRequestCount_incrementBy가1보다큼_정상처리() {
      // given
      String key = "192.168.1.1";
      int incrementBy = 3;
      String redisKey = "rate_limit:" + key;
      given(valueOperations.increment(redisKey, incrementBy)).willReturn(3L);

      // when
      adapter.incrementRequestCount(key, incrementBy);

      // then
      then(valueOperations).should().increment(redisKey, incrementBy);
      then(redisTemplate).should().expire(redisKey, 1, TimeUnit.MINUTES);
    }
  }
}
