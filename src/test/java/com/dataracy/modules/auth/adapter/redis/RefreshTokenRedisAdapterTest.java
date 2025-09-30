package com.dataracy.modules.auth.adapter.redis;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
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

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RefreshTokenRedisAdapterTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    
    @Mock
    private ValueOperations<String, String> valueOperations;
    
    @Mock
    private JwtProperties jwtProperties;

    private RefreshTokenRedisAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RefreshTokenRedisAdapter(redisTemplate, jwtProperties);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(jwtProperties.getRefreshTokenExpirationTime()).willReturn(7L);
    }

    @Nested
    @DisplayName("saveRefreshToken 메서드 테스트")
    class SaveRefreshTokenTest {

        @Test
        @DisplayName("성공: 리프레시 토큰 저장")
        void saveRefreshToken_성공() {
            // given
            String userId = "user123";
            String refreshToken = "refresh-token-123";

            // when
            adapter.saveRefreshToken(userId, refreshToken);

            // then
            then(valueOperations).should().set(
                eq("refreshToken:user:user123"),
                eq(refreshToken),
                eq(7L),
                eq(TimeUnit.DAYS)
            );
        }

        @Test
        @DisplayName("예외 발생 시 CommonException 변환")
        void saveRefreshToken_예외발생_CommonException변환() {
            // given
            String userId = "user456";
            String refreshToken = "refresh-token-456";
            RedisConnectionFailureException exception = new RedisConnectionFailureException("Redis connection failed");
            
            doThrow(exception).when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

            // when & then
            assertThatThrownBy(() -> adapter.saveRefreshToken(userId, refreshToken))
                .isInstanceOf(CommonException.class);
        }
    }

    @Nested
    @DisplayName("getRefreshToken 메서드 테스트")
    class GetRefreshTokenTest {

        @Test
        @DisplayName("성공: 리프레시 토큰 조회")
        void getRefreshToken_성공() {
            // given
            String userId = "user123";
            String refreshToken = "refresh-token-123";
            given(valueOperations.get("refreshToken:user:user123")).willReturn(refreshToken);

            // when
            String result = adapter.getRefreshToken(userId);

            // then
            assertThat(result).isEqualTo(refreshToken);
            then(valueOperations).should().get("refreshToken:user:user123");
        }

        @Test
        @DisplayName("토큰이 존재하지 않을 때 null 반환")
        void getRefreshToken_토큰존재하지않음_null반환() {
            // given
            String userId = "user999";
            given(valueOperations.get("refreshToken:user:user999")).willReturn(null);

            // when
            String result = adapter.getRefreshToken(userId);

            // then
            assertThat(result).isNull();
            then(valueOperations).should().get("refreshToken:user:user999");
        }
    }

    @Nested
    @DisplayName("deleteRefreshToken 메서드 테스트")
    class DeleteRefreshTokenTest {

        @Test
        @DisplayName("성공: 리프레시 토큰 삭제")
        void deleteRefreshToken_성공() {
            // given
            String userId = "user222";

            // when
            adapter.deleteRefreshToken(userId);

            // then
            then(redisTemplate).should().delete("refreshToken:user:user222");
        }

        @Test
        @DisplayName("예외 발생 시 CommonException 변환")
        void deleteRefreshToken_예외발생_CommonException변환() {
            // given
            String userId = "user333";
            RedisConnectionFailureException exception = new RedisConnectionFailureException("Redis connection failed");
            
            given(redisTemplate.delete(anyString())).willThrow(exception);

            // when & then
            assertThatThrownBy(() -> adapter.deleteRefreshToken(userId))
                .isInstanceOf(CommonException.class);
        }
    }
}
