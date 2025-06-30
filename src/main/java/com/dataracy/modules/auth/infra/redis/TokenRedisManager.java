package com.dataracy.modules.auth.infra.redis;

import com.dataracy.modules.auth.infra.jwt.JwtProperties;
import com.dataracy.modules.auth.infra.jwt.JwtUtil;
import com.dataracy.modules.auth.status.AuthErrorStatus;
import com.dataracy.modules.auth.status.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenRedisManager {

    private final StringRedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    /**
     * 리프레시 토큰 저장.
     *
     * @param userId       사용자 ID
     * @param refreshToken 리프레시 토큰
     */
    public void saveRefreshToken(String userId, String refreshToken) {
        try {
            redisTemplate.opsForValue().set(getRefreshTokenKey(userId), refreshToken, jwtProperties.getRefreshTokenExpirationTime(), TimeUnit.DAYS);
            log.info("Saved refresh token for userId: {}", userId);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failure.", e);
            throw new AuthException(AuthErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            log.error("Data access exception while saving refresh token.", e);
            throw new AuthException(AuthErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    /**
     * 리프레시 토큰 검증 및 사용자 ID 추출.
     *
     * @param refreshToken 클라이언트로부터 전달받은 리프레시 토큰
     * @return 사용자 ID
     */
    public Long validateAndExtractUserId(String refreshToken) {
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String storedRefreshToken = getStoredRefreshToken(userId.toString());
        if (!refreshToken.equals(storedRefreshToken)) {
            log.warn("Token mismatch for userId: {}", userId);
            throw new AuthException(AuthErrorStatus.REFRESH_TOKEN_USER_MISMATCH_IN_REDIS);
        }
        return userId;
    }

    /**
     * Redis에서 저장된 리프레시 토큰 조회.
     *
     * @param userId 사용자 ID
     * @return 저장된 리프레시 토큰
     */
    public String getStoredRefreshToken(String userId) {
        String refreshTokenKey = getRefreshTokenKey(userId);
        String token = redisTemplate.opsForValue().get(refreshTokenKey);
        if (token == null) {
            log.warn("Refresh token not found for userId: {}", userId);
            throw new AuthException(AuthErrorStatus.REFRESH_TOKEN_NOT_FOUND_IN_REDIS);
        }
        return token;
    }

    private String getRefreshTokenKey(String userId) {
        return "refreshToken: user" + userId;
    }

    /**
     * 로그아웃 시 해당 어세스토큰을 블랙리스트 처리한다.
     * @param token 토큰
     * @param expirationMillis 유효기간
     */
    public void blacklistAccessToken(String token, long expirationMillis) {
        redisTemplate.opsForValue().set("blacklist:" + token, "logout", Duration.ofMillis(expirationMillis));
    }

    /**
     * 해당 토큰이 블랙리스트 처리되었는지 여부를 파악한다.
     * @param token 어세스토큰
     * @return 블랙리스트 처리 여부
     */
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
    }
}
