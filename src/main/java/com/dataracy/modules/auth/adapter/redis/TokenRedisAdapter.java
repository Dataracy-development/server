package com.dataracy.modules.auth.adapter.redis;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.port.out.redis.TokenRedisPort;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenRedisAdapter implements TokenRedisPort {
    private final StringRedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;

    /**
     * 리프레시 토큰 키 설정
     * @param userId 유저 id
     * @return 레디스 키
     */
    private String getRefreshTokenKey(String userId) {
        return "refreshToken:user" + userId;
    }

    /**
     * 리프레시 토큰 저장.
     *
     * @param userId 유저 ID
     * @param refreshToken 리프레시 토큰
     */
    @Override
    public void saveRefreshToken(String userId, String refreshToken) {
        try {
            redisTemplate.opsForValue().set(
                    getRefreshTokenKey(userId),
                    refreshToken,
                    jwtProperties.getRefreshTokenExpirationTime(),
                    TimeUnit.DAYS
            );
            log.info("Saved refresh token for userId: {}", userId);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failure.", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            log.error("Data access exception while saving refresh token.", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    /**
     * Redis에서 저장된 리프레시 토큰 조회.
     *
     * @param userId 유저 ID
     * @return 저장된 리프레시 토큰
     */
    @Override
    public String getRefreshToken(String userId) {
        try {
            String refreshTokenKey = getRefreshTokenKey(userId);
            String token = redisTemplate.opsForValue().get(refreshTokenKey);
            if (token == null) {
                log.warn("Refresh token not found for userId: {}", userId);
            }
            return token;
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failure.", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            log.error("Data access exception while saving refresh token.", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    /**
     * Redis에서 해당 userId의 리프레시 토큰을 삭제한다.
     * @param userId
     */
    @Override
    public void deleteRefreshToken(String userId) {
        try {
            redisTemplate.delete(getRefreshTokenKey(userId));
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failure.", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            log.error("Data access exception while saving refresh token.", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }
}
