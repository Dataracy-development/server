package com.dataracy.modules.behaviorlog.adapter.redis;

import java.time.Duration;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogMergePort;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorLogRedisMergeAdapter implements BehaviorLogMergePort {

  private static final String REDIS_PREFIX = "behaviorlog:anonymous:";
  private static final Duration TTL = Duration.ofDays(7);

  private final StringRedisTemplate redisTemplate;

  /**
   * 익명 사용자의 행동 로그를 지정된 userId와 병합하여 Redis에 저장합니다.
   *
   * @param anonymousId 병합할 익명 사용자의 식별자
   * @param userId 병합 대상 사용자 ID
   * @throws CommonException Redis 연결 실패 또는 데이터 접근 예외 발생 시
   */
  @Override
  public void merge(String anonymousId, Long userId) {
    try {
      String key = buildKey(anonymousId);
      redisTemplate.opsForValue().set(key, userId.toString(), TTL);
      log.info("행동 로그 병합 완료: anonymousId={} → userId={}", anonymousId, userId);
    } catch (RedisConnectionFailureException e) {
      log.error("Redis 연결 실패", e);
      throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
    } catch (DataAccessException e) {
      log.error("Redis 접근 중 예외 발생", e);
      throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
    }
  }

  /**
   * 주어진 익명 ID에 매핑된 사용자 ID를 Redis에서 조회하여 반환합니다.
   *
   * @param anonymousId 익명 사용자 식별자
   * @return 익명 ID에 매핑된 사용자 ID가 존재하면 해당 값을 포함하는 Optional, 없으면 Optional.empty()
   */
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

  /**
   * 주어진 익명 ID에 대한 Redis 키를 생성합니다.
   *
   * @param anonymousId 익명 사용자 식별자
   * @return Redis에 저장될 키 문자열
   */
  private String buildKey(String anonymousId) {
    return REDIS_PREFIX + anonymousId;
  }
}
