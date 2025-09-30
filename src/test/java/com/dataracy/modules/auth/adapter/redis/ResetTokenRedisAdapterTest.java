package com.dataracy.modules.auth.adapter.redis;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
/**
 * ResetTokenRedisAdapter 테스트
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ResetTokenRedisAdapterTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    
    @Mock
    private ValueOperations<String, String> valueOperations;
    
    private ResetTokenRedisAdapter adapter;

    @BeforeEach
    void setUp() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        adapter = new ResetTokenRedisAdapter(redisTemplate);
    }

    @Nested
    @DisplayName("saveResetToken 메서드 테스트")
    class SaveResetTokenTest {

        @Test
        @DisplayName("성공: 토큰을 Redis에 저장")
        void saveResetToken_성공() {
            // given
            String token = "test-reset-token-123";
            String expectedKey = "resetPasswordToken:" + token;

            // when
            adapter.saveResetToken(token);

            // then
            then(valueOperations).should()
                    .set(eq(expectedKey), eq(token), eq(10L), eq(TimeUnit.MINUTES));
        }

        @Test
        @DisplayName("Redis 연결 실패 시 CommonException 발생")
        void saveResetToken_Redis연결실패_예외발생() {
            // given
            String token = "test-token";
            willThrow(new  RedisConnectionFailureException("Redis connection failed"))
                    .given(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

            // when & then
            CommonException exception = catchThrowableOfType(
                    () -> adapter.saveResetToken(token),
                    CommonException.class
            );
            assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        }

        @Test
        @DisplayName("DataAccessException 발생 시 CommonException 발생")
        void saveResetToken_DataAccessException_예외발생() {
            // given
            String token = "test-token";
            willThrow(new  DataAccessException("Data access failed") {})
                    .given(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

            // when & then
            CommonException exception = catchThrowableOfType(
                    () -> adapter.saveResetToken(token),
                    CommonException.class
            );
            assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    @Nested
    @DisplayName("isValidResetToken 메서드 테스트")
    class IsValidResetTokenTest {

        @Test
        @DisplayName("토큰이 존재할 때 true 반환")
        void isValidResetToken_토큰존재_true반환() {
            // given
            String token = "valid-token";
            String expectedKey = "resetPasswordToken:" + token;
            given(redisTemplate.hasKey(expectedKey)).willReturn(true);

            // when
            boolean result = adapter.isValidResetToken(token);

            // then
            assertThat(result).isTrue();
            then(redisTemplate).should().hasKey(expectedKey);
        }

        @Test
        @DisplayName("토큰이 존재하지 않을 때 false 반환")
        void isValidResetToken_토큰미존재_false반환() {
            // given
            String token = "invalid-token";
            String expectedKey = "resetPasswordToken:" + token;
            given(redisTemplate.hasKey(expectedKey)).willReturn(false);

            // when
            boolean result = adapter.isValidResetToken(token);

            // then
            assertThat(result).isFalse();
            then(redisTemplate).should().hasKey(expectedKey);
        }

        @Test
        @DisplayName("Redis 연결 실패 시 CommonException 발생")
        void isValidResetToken_Redis연결실패_예외발생() {
            // given
            String token = "test-token";
            willThrow(new  RedisConnectionFailureException("Redis connection failed"))
                    .given(redisTemplate).hasKey(anyString());

            // when & then
            CommonException exception = catchThrowableOfType(
                    () -> adapter.isValidResetToken(token),
                    CommonException.class
            );
            assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        }

        @Test
        @DisplayName("DataAccessException 발생 시 CommonException 발생")
        void isValidResetToken_DataAccessException_예외발생() {
            // given
            String token = "test-token";
            willThrow(new  DataAccessException("Data access failed") {})
                    .given(redisTemplate).hasKey(anyString());

            // when & then
            CommonException exception = catchThrowableOfType(
                    () -> adapter.isValidResetToken(token),
                    CommonException.class
            );
            assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }

        @Test
        @DisplayName("Redis가 null을 반환할 때 false 반환")
        void isValidResetToken_Redis가null반환_false반환() {
            // given
            String token = "test-token";
            String expectedKey = "resetPasswordToken:" + token;
            given(redisTemplate.hasKey(expectedKey)).willReturn(null);

            // when
            boolean result = adapter.isValidResetToken(token);

            // then
            assertThat(result).isFalse();
            then(redisTemplate).should().hasKey(expectedKey);
        }
    }
}
