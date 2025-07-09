package com.dataracy.modules.behaviorlog.adapter.redis;

import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogMergePort;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorLogRedisAdapter implements BehaviorLogMergePort {

    private final StringRedisTemplate redisTemplate;

    /**
     * 리프레시 토큰 저장.
     *
     * @param anonymousId 익명 ID
     * @param userId 유저 ID
     */
    @Override
    public void merge(String anonymousId, Long userId) {
        try {
            String key = "behaviorlog:anonymous:" + anonymousId;
            redisTemplate.opsForValue().set(key, userId.toString(), Duration.ofDays(7));
            log.info("익명 → 유저 병합 저장 완료: anonymousId={}, userId={}", anonymousId, userId);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failure.", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            log.error("Data access exception while saving refresh token.", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }
}
