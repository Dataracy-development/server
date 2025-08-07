package com.dataracy.modules.auth.adapter.redis;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.port.out.cache.CacheRefreshTokenPort;
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
public class RefreshTokenRedisAdapter implements CacheRefreshTokenPort {
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
     * 지정한 유저 ID에 대한 리프레시 토큰을 Redis에 저장합니다.
     *
     * 토큰은 설정된 만료 기간(일 단위) 동안 유지됩니다.
     *
     * @param userId 리프레시 토큰을 저장할 유저의 ID
     * @param refreshToken 저장할 리프레시 토큰 값
     * @throws CommonException Redis 연결 실패 또는 데이터 접근 오류 발생 시 예외가 발생합니다.
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
     * 주어진 유저 ID에 해당하는 리프레시 토큰을 Redis에서 조회하여 반환합니다.
     *
     * @param userId 리프레시 토큰을 조회할 유저의 ID
     * @return 저장된 리프레시 토큰, 존재하지 않으면 null 반환
     * @throws CommonException Redis 연결 실패 또는 데이터 접근 오류 발생 시 예외를 발생시킵니다.
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
     * 지정된 userId에 해당하는 리프레시 토큰을 Redis에서 삭제합니다.
     *
     * @param userId 리프레시 토큰을 삭제할 사용자 ID
     * @throws CommonException Redis 연결 실패 또는 데이터 접근 오류 발생 시 예외가 발생합니다.
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
