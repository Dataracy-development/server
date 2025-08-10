package com.dataracy.modules.project.adapter.redis;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.project.application.port.out.cache.CacheProjectViewCountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ProjectViewCountRedisAdapter implements CacheProjectViewCountPort {
    private final StringRedisTemplate redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(5);

    /**
     * 주어진 프로젝트와 대상 유형에 대해 뷰어가 5분 이내 최초로 조회할 때만 조회수를 1 증가시킵니다.
     *
     * 동일 뷰어가 5분 내에 여러 번 조회해도 조회수는 한 번만 증가합니다.
     *
     * @param projectId 조회수를 증가시킬 프로젝트의 ID
     * @param viewerId  조회를 시도한 뷰어의 고유 식별자
     * @param targetType 조회 대상의 유형(예: 프로젝트, 게시글 등)
     * @throws CommonException 레디스 연결 실패 또는 데이터 접근 예외 발생 시
     */
    @Override
    public void increaseViewCount(Long projectId, String viewerId, String targetType) {
        try {
            String dedupKey = String.format("viewDedup:%s:%s:%s", targetType, projectId, viewerId);

            Boolean wasSet = redisTemplate.opsForValue().setIfAbsent(dedupKey, "1", TTL);
            if (Boolean.TRUE.equals(wasSet)) {
                String countKey = String.format("viewCount:%s:%s", targetType, projectId);
                redisTemplate.opsForValue().increment(countKey);
                LoggerFactory.redis().logSaveOrUpdate("viewCount:" + targetType + ":" + projectId, "해당 프로젝트를 조회하였습니다. projectId=" + projectId);
            }
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError("viewCount:" + targetType + ":" + projectId, "레디스 서버 연결에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError("viewCount:" + targetType + ":" + projectId, "네트워크 오류로 데이터 접근에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    /**
     * 지정된 프로젝트와 대상 유형에 대한 현재 조회수를 반환합니다.
     *
     * @param projectId 조회수를 조회할 프로젝트의 ID
     * @param targetType 조회 대상의 유형
     * @return 해당 프로젝트와 대상 유형의 조회수. 값이 없으면 0을 반환합니다.
     */
    @Override
    public Long getViewCount(Long projectId, String targetType) {
        try {
            Instant startTime = LoggerFactory.redis().logQueryStart("viewCount:" + targetType + ":" + projectId, "해당 프로젝트의 조회수 조회 시작. projectId=" + projectId);

            String key = String.format("viewCount:%s:%s", targetType, projectId);
            String value = redisTemplate.opsForValue().get(key);
            Long viewCount = value != null ? Long.parseLong(value) : 0L;

            LoggerFactory.redis().logQueryEnd("viewCount:" + targetType + ":" + projectId, "해당 프로젝트의 조회수 조회 종료. projectId=" + projectId, startTime);
            return viewCount;
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError("viewCount:" + targetType + ":" + projectId, "레디스 서버 연결에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError("viewCount:" + targetType + ":" + projectId, "네트워크 오류로 데이터 접근에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

//    public Set<String> getAllViewCountKeys(String targetType) {
//        try {
//            Instant startTime = LoggerFactory.redis().logQueryStart("viewCount:" + targetType + ":*", "지정된 타겟 타입에 해당하는 모든 조회수 Redis 키를 반환. targetType=" + targetType);
//            Set<String> keys = redisTemplate.keys(String.format("viewCount:%s:*", targetType));
//            LoggerFactory.redis().logQueryEnd("viewCount:" + targetType + ":*", "지정된 타겟 타입에 해당하는 모든 조회수 Redis 키를 반환. targetType=" + targetType, startTime);
//            return keys;
//        } catch (RedisConnectionFailureException e) {
//            LoggerFactory.redis().logError("viewCount:" + targetType + ":*", "레디스 서버 연결에 실패했습니다.", e);
//            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
//        } catch (DataAccessException e) {
//            LoggerFactory.redis().logError("viewCount:" + targetType + ":*", "네트워크 오류로 데이터 접근에 실패했습니다.", e);
//            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
//        }

    /**
     * 지정된 타겟 타입에 해당하는 모든 조회수 Redis 키의 집합을 반환합니다.
     *
     * @param targetType 조회수 키를 검색할 타겟 타입
     * @return 해당 타겟 타입의 모든 조회수 Redis 키 집합
     * @throws CommonException 레디스 연결 실패 또는 데이터 접근 예외 발생 시
     */
    @Override
    public Set<String> getAllViewCountKeys(String targetType) {
        try {
            Instant startTime = LoggerFactory.redis().logQueryStart("viewCount:" + targetType + ":*", "지정된 타겟 타입에 해당하는 모든 조회수 Redis 키를 반환. targetType=" + targetType);

            Set<String> keys = new HashSet<>();
            ScanOptions options = ScanOptions.scanOptions()
                    .match(String.format("viewCount:%s:*", targetType))
                    .build();

            try (Cursor<String> cursor = redisTemplate.scan(options)) {
                while (cursor.hasNext()) {
                    keys.add(cursor.next());
                }
            }

            LoggerFactory.redis().logQueryEnd("viewCount:" + targetType + ":*", "지정된 타겟 타입에 해당하는 모든 조회수 Redis 키를 반환. targetType=" + targetType, startTime);
            return keys;
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError("viewCount:" + targetType + ":*", "레디스 서버 연결에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError("viewCount:" + targetType + ":*", "네트워크 오류로 데이터 접근에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    /**
     * 지정된 대상 ID와 타입에 해당하는 조회수 카운트 Redis 키를 삭제합니다.
     *
     * @param targetId 조회수 카운트를 삭제할 대상의 ID
     * @param targetType 조회수 카운트를 삭제할 대상의 타입
     * @throws CommonException Redis 연결 실패 또는 데이터 접근 예외 발생 시 예외가 발생합니다.
     */
    @Override
    public void clearViewCount(Long targetId, String targetType) {
        try {
            redisTemplate.delete(String.format("viewCount:%s:%s", targetType, targetId));
            LoggerFactory.redis().logDelete("viewCount:" + targetType + ":" + targetId, "지정된 대상 ID와 대상 타입에 해당하는 조회수 카운트 Redis 키를 삭제. targetId=" + targetId);
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError("viewCount:" + targetType + ":" + targetId, "레디스 서버 연결에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError("viewCount:" + targetType + ":" + targetId, "네트워크 오류로 데이터 접근에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }
}
