package com.dataracy.modules.project.adapter.redis;

import com.dataracy.modules.project.application.port.out.ProjectViewCountRedisPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ProjectViewCountRedisAdapter implements ProjectViewCountRedisPort {

    private final StringRedisTemplate redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(5);

    /**
     * 주어진 프로젝트와 뷰어에 대해 중복 방지 기간 내 최초 조회 시 조회수를 1 증가시킵니다.
     *
     * 동일 뷰어가 5분 이내에 여러 번 조회해도 조회수는 한 번만 증가합니다.
     *
     * @param projectId 조회수를 증가시킬 프로젝트의 ID
     * @param viewerId  조회를 시도한 뷰어의 고유 식별자
     * @param targetType 조회 대상의 유형(예: 프로젝트, 게시글 등)
     */
    public void increaseViewCount(Long projectId, String viewerId, String targetType) {
        String dedupKey = String.format("viewDedup:%s:%s:%s", targetType, projectId, viewerId);
        Boolean alreadyViewed = redisTemplate.hasKey(dedupKey);

        if (Boolean.FALSE.equals(alreadyViewed)) {
            String countKey = String.format("viewCount:%s:%s", targetType, projectId);

            redisTemplate.opsForValue().increment(countKey);
            redisTemplate.expire(dedupKey, TTL);
        }
    }

    /**
     * 지정된 프로젝트와 대상 유형에 대한 조회수를 Redis에서 조회합니다.
     *
     * @param projectId 조회수를 확인할 프로젝트의 ID
     * @param targetType 조회 대상의 유형
     * @return 해당 프로젝트와 대상 유형의 조회수. 값이 없으면 0을 반환합니다.
     */
    public Long getViewCount(Long projectId, String targetType) {
        String key = String.format("viewCount:%s:%s", targetType, projectId);
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0L;
    }

    /**
     * 지정된 타겟 타입에 해당하는 모든 조회수 Redis 키를 반환합니다.
     *
     * @param targetType 조회수 키를 조회할 타겟 타입
     * @return 해당 타겟 타입의 모든 조회수 Redis 키 집합
     */
    public Set<String> getAllViewCountKeys(String targetType) {
        return redisTemplate.keys(String.format("viewCount:%s:*", targetType));
    }

    /**
     * 지정된 대상 ID와 대상 타입에 해당하는 조회수 카운트 Redis 키를 삭제합니다.
     *
     * @param targetId  조회수 카운트를 삭제할 대상의 ID
     * @param targetType 조회수 카운트를 삭제할 대상의 타입
     */
    public void clearViewCount(Long targetId, String targetType) {
        redisTemplate.delete(String.format("viewCount:%s:%s", targetType, targetId));
    }
}

