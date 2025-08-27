package com.dataracy.modules.email.adapter.redis;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

class EmailCodeRedisAdapterTest {

    StringRedisTemplate redisTemplate;
    ValueOperations<String, String> valueOps;
    EmailCodeRedisAdapter adapter;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        given(redisTemplate.opsForValue()).willReturn(valueOps);
        adapter = new EmailCodeRedisAdapter(redisTemplate);
        ReflectionTestUtils.setField(adapter, "EXPIRE_MINUTES", 5L);
    }

    @Nested
    @DisplayName("이메일 인증 코드 저장")
    class SaveCode {

        @Test
        @DisplayName("성공: 키패턴과 TTL로 저장한다")
        void success() {
            // given
            String email = "user@example.com";
            String code = "123456";
            String expectedKey = "email:verification:signup:" + email;

            // when
            adapter.saveCode(email, code, EmailVerificationType.SIGN_UP);

            // then
            then(valueOps).should()
                    .set(eq(expectedKey), eq(code), eq(5L), eq(TimeUnit.MINUTES));
        }

        @Test
        @DisplayName("Redis 연결 실패 시 CommonException(REDIS_CONNECTION_FAILURE)")
        void connectionFailure() {
            // given
            willThrow(new RedisConnectionFailureException("fail"))
                    .given(valueOps).set(anyString(), anyString(), anyLong(), any());

            // when
            CommonException ex = catchThrowableOfType(
                    () -> adapter.saveCode("e@x.com", "000000", EmailVerificationType.PASSWORD_RESET),
                    CommonException.class
            );

            // then
            assertThat(ex).isNotNull();
            assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        }

        @Test
        @DisplayName("DataAccessException 시 CommonException(DATA_ACCESS_EXCEPTION)")
        void dataAccess() {
            // given
            willThrow(mock(DataAccessException.class))
                    .given(valueOps).set(anyString(), anyString(), anyLong(), any());

            // when
            CommonException ex = catchThrowableOfType(
                    () -> adapter.saveCode("e@x.com", "000000", EmailVerificationType.PASSWORD_SEARCH),
                    CommonException.class
            );

            // then
            assertThat(ex).isNotNull();
            assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    @Nested
    @DisplayName("이메일 인증코드 검증")
    class VerifyCode {

        @Test
        @DisplayName("성공: 저장된 값을 조회하여 그대로 반환한다")
        void success() {
            // given
            String expectedKey = "email:verification:password:search:user@example.com";
            given(valueOps.get(expectedKey)).willReturn("222222");

            // when
            String result = adapter.verifyCode("user@example.com", "222222", EmailVerificationType.PASSWORD_SEARCH);

            // then
            assertThat(result).isEqualTo("222222");
            then(redisTemplate).should().opsForValue();
        }

        @Test
        @DisplayName("Redis 연결 실패 시 CommonException(REDIS_CONNECTION_FAILURE)")
        void connectionFailure() {
            // given
            given(redisTemplate.opsForValue()).willThrow(new RedisConnectionFailureException("down"));

            // when
            CommonException ex = catchThrowableOfType(
                    () -> adapter.verifyCode("x@y.com", "111111", EmailVerificationType.SIGN_UP),
                    CommonException.class
            );

            // then
            assertThat(ex).isNotNull();
            assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        }

        @Test
        @DisplayName("DataAccessException 시 CommonException(DATA_ACCESS_EXCEPTION)")
        void dataAccess() {
            // given
            given(redisTemplate.opsForValue()).willThrow(mock(DataAccessException.class));

            // when
            CommonException ex = catchThrowableOfType(
                    () -> adapter.verifyCode("x@y.com", "111111", EmailVerificationType.SIGN_UP),
                    CommonException.class
            );

            // then
            assertThat(ex).isNotNull();
            assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    @Nested
    @DisplayName("이메일 인증코드 제거")
    class DeleteCode {

        @Test
        @DisplayName("성공: 목적별 키로 삭제")
        void success() {
            // given
            String expectedKey = "email:verification:password:reset:user@example.com";

            // when
            adapter.deleteCode("user@example.com", EmailVerificationType.PASSWORD_RESET);

            // then
            then(redisTemplate).should().delete(expectedKey);
        }

        @Test
        @DisplayName("Redis 연결 실패 시 CommonException(REDIS_CONNECTION_FAILURE)")
        void connectionFailure() {
            // given
            willThrow(new RedisConnectionFailureException("down"))
                    .given(redisTemplate).delete(anyString());

            // when
            CommonException ex = catchThrowableOfType(
                    () -> adapter.deleteCode("x@y.com", EmailVerificationType.PASSWORD_SEARCH),
                    CommonException.class
            );

            // then
            assertThat(ex).isNotNull();
            assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        }

        @Test
        @DisplayName("DataAccessException 시 CommonException(DATA_ACCESS_EXCEPTION)")
        void dataAccess() {
            // given
            willThrow(mock(DataAccessException.class))
                    .given(redisTemplate).delete(anyString());

            // when
            CommonException ex = catchThrowableOfType(
                    () -> adapter.deleteCode("x@y.com", EmailVerificationType.PASSWORD_SEARCH),
                    CommonException.class
            );

            // then
            assertThat(ex).isNotNull();
            assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }
}
