package com.dataracy.modules.project.adapter.redis;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectViewCountRedisAdapterTest {

    @Mock StringRedisTemplate redisTemplate;
    @Mock ValueOperations<String, String> valueOps;

    @InjectMocks ProjectViewCountRedisAdapter adapter;

    @Test
    @DisplayName("increaseViewCount_should_increment_when_setIfAbsent_true")
    void increaseViewCount_should_increment_when_setIfAbsent_true() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOps);
        given(valueOps.setIfAbsent(anyString(), anyString(), any())).willReturn(Boolean.TRUE);

        // when
        adapter.increaseViewCount(10L, "viewerA", "PROJECT");

        // then
        then(valueOps).should().increment("viewCount:PROJECT:10");
    }

    @Test
    @DisplayName("increaseViewCount_should_not_increment_when_setIfAbsent_false")
    void increaseViewCount_should_not_increment_when_setIfAbsent_false() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOps);
        given(valueOps.setIfAbsent(anyString(), anyString(), any())).willReturn(Boolean.FALSE);

        // when
        adapter.increaseViewCount(10L, "viewerA", "PROJECT");

        // then
        then(valueOps).should(never()).increment(anyString());
    }

    @Test
    @DisplayName("getViewCount_should_return_zero_when_absent")
    void getViewCount_should_return_zero_when_absent() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOps);
        given(valueOps.get("viewCount:PROJECT:99")).willReturn(null);

        // when
        Long count = adapter.getViewCount(99L, "PROJECT");

        // then
        assertThat(count).isEqualTo(0L);
    }

    @Test
    @DisplayName("clearViewCount_should_delete_key")
    void clearViewCount_should_delete_key() {
        // when
        adapter.clearViewCount(77L, "PROJECT");

        // then
        then(redisTemplate).should().delete("viewCount:PROJECT:77");
    }

    @Test
    @DisplayName("increaseViewCount_should_wrap_redis_connection_failure")
    void increaseViewCount_should_wrap_redis_connection_failure() {
        // given
        given(redisTemplate.opsForValue()).willThrow(new RedisConnectionFailureException("down"));

        // when & then
        assertThatThrownBy(() -> adapter.increaseViewCount(1L, "v", "PROJECT"))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("getViewCount_should_wrap_data_access_exception")
    void getViewCount_should_wrap_data_access_exception() {
        // given
        given(redisTemplate.opsForValue()).willThrow(new DataAccessException("network") {});

        // when & then
        assertThatThrownBy(() -> adapter.getViewCount(1L, "PROJECT"))
                .isInstanceOf(CommonException.class);
    }
}
