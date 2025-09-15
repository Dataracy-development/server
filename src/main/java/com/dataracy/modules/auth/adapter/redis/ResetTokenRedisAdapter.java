package com.dataracy.modules.auth.adapter.redis;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.auth.application.port.out.token.ManageResetTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ResetTokenRedisAdapter implements ManageResetTokenPort {
    private final StringRedisTemplate redisTemplate;

    private static final Duration TTL = Duration.ofMinutes(10);
    private static final String PREFIX = "resetPasswordToken:";

    /**
     * 주어진 비밀번호 재설정용 토큰에 접두사를 붙여 Redis에 저장할 키를 생성합니다.
     *
     * @param token Redis 키에 사용할 리셋 토큰 문자열
     * @return 접두사가 포함된 Redis 키 문자열
     */
    private String getResetTokenKey(String token) {
        return PREFIX + token;
    }

    /**
     * 주어진 비밀번호 재설정 토큰을 10분간 유효한 상태로 Redis에 저장합니다.
     *
     * @param token 저장할 비밀번호 재설정 토큰
     * @throws CommonException Redis 연결 실패 또는 데이터 접근 오류 발생 시 예외가 발생합니다.
     */
    public void saveResetToken(String token) {
        String key = getResetTokenKey(token);
        try {
            redisTemplate.opsForValue().set(key, token, TTL.toMinutes(), TimeUnit.MINUTES);
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
     * 주어진 비밀번호 재설정 토큰이 Redis에 존재하는지 확인합니다.
     *
     * @param token 확인할 비밀번호 재설정 토큰
     * @return 토큰이 Redis에 존재하면 true, 그렇지 않으면 false
     * @throws CommonException Redis 연결 실패 또는 데이터 접근 오류 발생 시 예외가 발생합니다.
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
