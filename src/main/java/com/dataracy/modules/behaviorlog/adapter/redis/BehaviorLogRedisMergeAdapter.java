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
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorLogRedisMergeAdapter implements BehaviorLogMergePort {

    private static final String REDIS_PREFIX = "behaviorlog:anonymous:";
    private static final Duration TTL = Duration.ofDays(7);

    private final StringRedisTemplate redisTemplate;

    @Override
    public void merge(String anonymousId, Long userId) {
        try {
            String key = buildKey(anonymousId);
            redisTemplate.opsForValue().set(key, userId.toString(), TTL);
            log.info("행동 로그 병합 완료: anonymousId={} → userId={}",
                    anonymousId, userId.toString().replaceAll("\\d(?=\\d{2})", "*"));
        } catch (RedisConnectionFailureException e) {
            log.error("Redis 연결 실패", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            log.error("Redis 접근 중 예외 발생", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    @Override
    public Optional<Long> findMergedUserId(String anonymousId) {
        try {
            String key = buildKey(anonymousId);
            String value = redisTemplate.opsForValue().get(key);
            return Optional.ofNullable(value).map(Long::parseLong);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis 연결 실패", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            log.error("Redis 접근 중 예외 발생", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    private String buildKey(String anonymousId) {
        return REDIS_PREFIX + anonymousId;
    }
}
