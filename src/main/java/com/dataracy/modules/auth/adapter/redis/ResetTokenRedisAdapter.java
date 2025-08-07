package com.dataracy.modules.auth.adapter.redis;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.auth.application.port.out.cache.CacheResetTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ResetTokenRedisAdapter implements CacheResetTokenPort {
    private final StringRedisTemplate redisTemplate;

    private static final Duration TTL = Duration.ofMinutes(10);
    private static final String PREFIX = "resetPasswordToken:";

    /**
     * Redis 키를 구성합니다.
     */
    private String getResetTokenKey(String token) {
        return PREFIX + token;
    }

    /**
     * 비밀번호 재설정용 토큰을 Redis에 저장합니다.
     *
     * @param token 클라이언트에 발급할 임시 토큰
     */
    public void saveResetToken(String token) {
        String key = getResetTokenKey(token);
        try {
            redisTemplate.opsForValue().set(key, "1", TTL.toMinutes(), TimeUnit.MINUTES);
            LoggerFactory.redis().logSaveOrUpdate(key, "비밀번호 재설정 토큰을 Redis에 저장했습니다.");
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError(key, "레디스 연결 실패", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError(key, "레디스 데이터 접근 실패", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    /**
     * 토큰이 Redis에 존재하는지 확인합니다.
     * 존재하면 true, 없으면 false를 반환합니다.
     *
     * @param token 클라이언트가 제출한 토큰
     * @return 존재 여부
     */
    public boolean isValidResetToken(String token) {
        String key = getResetTokenKey(token);
        try {
            Boolean exists = redisTemplate.hasKey(key);
            LoggerFactory.redis().logExist(key, "비밀번호 재설정 토큰 존재 여부 확인: " + exists);
            return Boolean.TRUE.equals(exists);
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError(key, "레디스 연결 실패", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError(key, "레디스 데이터 접근 실패", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }
}
