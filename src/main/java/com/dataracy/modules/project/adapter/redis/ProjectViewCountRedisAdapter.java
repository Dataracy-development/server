package com.dataracy.modules.project.adapter.redis;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.project.application.port.out.view.ManageProjectViewCountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ProjectViewCountRedisAdapter implements ManageProjectViewCountPort {
    private final StringRedisTemplate redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(5);

    // Redis 키 및 메시지 상수 정의
    private static final String VIEW_COUNT_PREFIX = "viewCount:";
    private static final String REDIS_CONNECTION_FAILURE_MESSAGE = "레디스 서버 연결에 실패했습니다.";
    private static final String DATA_ACCESS_FAILURE_MESSAGE = "네트워크 오류로 데이터 접근에 실패했습니다.";

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
                LoggerFactory.redis().logSaveOrUpdate(VIEW_COUNT_PREFIX + targetType + ":" + projectId, "해당 프로젝트를 조회하였습니다. projectId=" + projectId);
            }
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError(VIEW_COUNT_PREFIX + targetType + ":" + projectId, REDIS_CONNECTION_FAILURE_MESSAGE, e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError(VIEW_COUNT_PREFIX + targetType + ":" + projectId, DATA_ACCESS_FAILURE_MESSAGE, e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    /**
         * 지정된 프로젝트와 대상 유형의 조회수를 Redis에서 가져옵니다.
         *
         * 키 `viewCount:{targetType}:{projectId}`의 값을 읽어 Long으로 반환합니다. 키가 존재하지 않으면 0L을 반환합니다.
         *
         * @param projectId 조회수를 조회할 프로젝트의 ID
         * @param targetType 조회 대상의 유형
         * @return 해당 프로젝트와 대상 유형의 조회수(키가 없으면 0L)
         * @throws CommonException REDIS_CONNECTION_FAILURE - Redis 연결 실패 시
         * @throws CommonException DATA_ACCESS_EXCEPTION - Redis 데이터 접근 중 오류 발생 시
         */
    @Override
    public Long getViewCount(Long projectId, String targetType) {
        try {
            Instant startTime = LoggerFactory.redis().logQueryStart(VIEW_COUNT_PREFIX + targetType + ":" + projectId, "해당 프로젝트의 조회수 조회 시작. projectId=" + projectId);

            String key = String.format("viewCount:%s:%s", targetType, projectId);
            String value = redisTemplate.opsForValue().get(key);
            Long viewCount = value != null ? Long.parseLong(value) : 0L;

            LoggerFactory.redis().logQueryEnd(VIEW_COUNT_PREFIX + targetType + ":" + projectId, "해당 프로젝트의 조회수 조회 종료. projectId=" + projectId, startTime);
            return viewCount;
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError(VIEW_COUNT_PREFIX + targetType + ":" + projectId, REDIS_CONNECTION_FAILURE_MESSAGE, e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError(VIEW_COUNT_PREFIX + targetType + ":" + projectId, DATA_ACCESS_FAILURE_MESSAGE, e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

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
            Instant startTime = LoggerFactory.redis().logQueryStart(VIEW_COUNT_PREFIX + targetType + ":*", "지정된 타겟 타입에 해당하는 모든 조회수 Redis 키를 반환. targetType=" + targetType);

            Set<String> keys = new HashSet<>();
            ScanOptions options = ScanOptions.scanOptions()
                    .match(String.format("viewCount:%s:*", targetType))
                    .build();

            try (Cursor<String> cursor = redisTemplate.scan(options)) {
                while (cursor.hasNext()) {
                    keys.add(cursor.next());
                }
            }

            LoggerFactory.redis().logQueryEnd(VIEW_COUNT_PREFIX + targetType + ":*", "지정된 타겟 타입에 해당하는 모든 조회수 Redis 키를 반환. targetType=" + targetType, startTime);
            return keys;
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError(VIEW_COUNT_PREFIX + targetType + ":*", REDIS_CONNECTION_FAILURE_MESSAGE, e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError(VIEW_COUNT_PREFIX + targetType + ":*", DATA_ACCESS_FAILURE_MESSAGE, e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    /**
     * 지정된 대상 유형과 ID에 대응하는 조회수 Redis 키(viewCount:{targetType}:{targetId})를 삭제합니다.
     *
     * 삭제 작업 중 Redis 연결 실패 또는 데이터 접근 오류가 발생하면 CommonException으로 래핑되어 전파됩니다.
     *
     * @param targetId   조회수 키를 삭제할 대상 ID
     * @param targetType 조회수 키에 사용된 대상 타입
     * @throws CommonException Redis 연결 실패 또는 데이터 접근 예외 발생 시 발생
     */
    @Override
    public void clearViewCount(Long targetId, String targetType) {
        try {
            redisTemplate.delete(String.format("viewCount:%s:%s", targetType, targetId));
            LoggerFactory.redis().logDelete(VIEW_COUNT_PREFIX + targetType + ":" + targetId, "지정된 대상 ID와 대상 타입에 해당하는 조회수 카운트 Redis 키를 삭제. targetId=" + targetId);
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError(VIEW_COUNT_PREFIX + targetType + ":" + targetId, REDIS_CONNECTION_FAILURE_MESSAGE, e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError(VIEW_COUNT_PREFIX + targetType + ":" + targetId, DATA_ACCESS_FAILURE_MESSAGE, e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    /**
     * 지정한 대상의 조회수를 Redis에서 원자적으로 꺼내어 삭제(pop)합니다.
     *
     * 대상 키(viewCount:{targetType}:{projectId})에 저장된 값을 가져와 키를 삭제한 뒤 해당 값을 Long으로 반환합니다.
     * 값이 존재하지 않거나 숫자 파싱에 실패하면 0L을 반환합니다.
     *
     * @param projectId  조회수를 팝할 대상의 ID
     * @param targetType 키에 사용되는 대상 타입(예: "project")
     * @return 팝된 조회수(Long). 키가 없거나 값이 손상된 경우 0L을 반환합니다.
     * @throws CommonException REDIS_CONNECTION_FAILURE - Redis 연결 실패 시
     * @throws CommonException DATA_ACCESS_EXCEPTION - Redis 접근 중 기타 데이터 오류 발생 시
     */
    @Override
    public Long popViewCount(Long projectId, String targetType) {
        String key = String.format("viewCount:%s:%s", targetType, projectId);

        try {
            Instant startTime = LoggerFactory.redis().logQueryStart(VIEW_COUNT_PREFIX + targetType + ":" + projectId, "조회수 pop 작업 시작. projectId=" + projectId);

            Long viewCount = redisTemplate.execute(connection -> {
                var keySerializer = redisTemplate.getStringSerializer();
                var valSerializer = redisTemplate.getStringSerializer();
                byte[] rawKey = keySerializer.serialize(key);
                byte[] raw = connection.stringCommands().getDel(rawKey);
                if (raw == null) return 0L;
                String str = valSerializer.deserialize(raw);
                try {
                    return Long.parseLong(str);
                } catch (NumberFormatException nfe) {
                    // 손상된 값 방어: 로그 남기고 0으로 처리
                    LoggerFactory.redis().logError(key, "정수 파싱 실패. value=" + str, nfe);
                    return 0L;
                }
            }, false);

            LoggerFactory.redis().logQueryEnd(VIEW_COUNT_PREFIX + targetType + ":" + projectId, "조회수 pop 작업 종료. projectId=" + projectId, startTime);
            return viewCount;
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError(key, REDIS_CONNECTION_FAILURE_MESSAGE, e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError(key, DATA_ACCESS_FAILURE_MESSAGE, e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }
}
