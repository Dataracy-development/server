package com.dataracy.modules.project.adapter.elasticsearch.redis;

import com.dataracy.modules.project.application.port.out.ProjectViewCountRedisPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ProjectViewCountRedisRepository implements ProjectViewCountRedisPort {

    private final StringRedisTemplate redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(5);

    public void increaseViewCount(Long projectId, String viewerId, String targetType) {
        String dedupKey = String.format("viewDedup:%s:%s:%s", targetType, projectId, viewerId);
        Boolean alreadyViewed = redisTemplate.hasKey(dedupKey);

        if (Boolean.FALSE.equals(alreadyViewed)) {
            String countKey = String.format("viewCount:%s:%s", targetType, projectId);

            redisTemplate.opsForValue().increment(countKey);
            redisTemplate.expire(dedupKey, TTL);
        }
    }

    public Long getViewCount(Long projectId, String targetType) {
        String key = String.format("viewCount:%s:%s", targetType, projectId);
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0L;
    }

    public Set<String> getAllViewCountKeys(String targetType) {
        return redisTemplate.keys(String.format("viewCount:%s:*", targetType));
    }

    public void clearViewCount(Long targetId, String targetType) {
        redisTemplate.delete(String.format("viewCount:%s:%s", targetType, targetId));
    }
}

