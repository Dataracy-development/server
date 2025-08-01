package com.dataracy.modules.auth.adapter.redis;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.port.out.redis.TokenRedisPort;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

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
            LoggerFactory.redis().logSaveOrUpdate(userId, "리프레시 토큰 레디스 저장에 성공했습니다.");
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError(userId, "레디스 서버 연결에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError(userId, "네트워크 오류로 데이터 접근에 실패했습니다.", e);
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
            Instant startTime = LoggerFactory.redis().logQueryStart(userId, "레디스에서 인증된 해당 유저의 리프레시 토큰을 찾아 반환 시작");
            String refreshTokenKey = getRefreshTokenKey(userId);
            String token = redisTemplate.opsForValue().get(refreshTokenKey);
            if (token == null) {
                LoggerFactory.redis().logWarning(userId, "레디스에 해당 리프레시 토큰이 존재하지 않습니다.");
            }
            LoggerFactory.redis().logQueryEnd(userId, "레디스에서 인증된 해당 유저의 리프레시 토큰을 찾아 반환 성공", startTime);
            return token;
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError(userId, "레디스 서버 연결에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError(userId, "네트워크 오류로 데이터 접근에 실패했습니다.", e);
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
            LoggerFactory.redis().logDelete(userId, "해당 유저의 레디스에 저장된 리프레시 토큰을 삭제한다.");
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError(userId, "레디스 서버 연결에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError(userId, "네트워크 오류로 데이터 접근에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }
}
