package com.dataracy.modules.behaviorlog.adapter.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;

/** BehaviorLogRedisMergeAdapter 테스트 */
@ExtendWith(MockitoExtension.class)
class BehaviorLogRedisMergeAdapterTest {

  // Test constants
  private static final Long PROJECT_ID = 1L;
  private static final Long USER_ID = 4L;
  private static final Long TARGET_ID = 3L;
  private static final Long EXPECTED_USER_ID = 456L;

  @Mock private StringRedisTemplate redisTemplate;

  @Mock private ValueOperations<String, String> valueOperations;

  private BehaviorLogRedisMergeAdapter adapter;

  @BeforeEach
  void setUp() {
    given(redisTemplate.opsForValue()).willReturn(valueOperations);
    adapter = new BehaviorLogRedisMergeAdapter(redisTemplate);
  }

  @Nested
  @DisplayName("merge 메서드 테스트")
  class MergeTest {

    @Test
    @DisplayName("성공: 익명 사용자 행동 로그를 userId와 병합")
    void mergeReturnsSuccess() {
      // given
      String anonymousId = "anonymous-1";
      Long userId = PROJECT_ID;
      String expectedKey = "behaviorlog:anonymous:" + anonymousId;

      // when
      adapter.merge(anonymousId, userId);

      // then
      then(valueOperations)
          .should()
          .set(eq(expectedKey), eq(userId.toString()), eq(Duration.ofDays(7)));
    }

    @Test
    @DisplayName("Redis 연결 실패 시 CommonException 발생")
    void mergeWhenRedisConnectionFailsThrowsException() {
      // given
      String anonymousId = "anonymous-1";
      Long userId = PROJECT_ID;
      willThrow(new RedisConnectionFailureException("Redis connection failed"))
          .given(valueOperations)
          .set(anyString(), anyString(), any(Duration.class));

      // when & then
      CommonException exception =
          catchThrowableOfType(() -> adapter.merge(anonymousId, userId), CommonException.class);
      assertAll(
          () -> assertThat(exception).isNotNull(),
          () ->
              assertThat(exception.getErrorCode())
                  .isEqualTo(CommonErrorStatus.REDIS_CONNECTION_FAILURE));
    }

    @Test
    @DisplayName("DataAccessException 발생 시 CommonException 발생")
    void mergeWhenDataAccessExceptionThrowsException() {
      // given
      String anonymousId = "anonymous-1";
      Long userId = PROJECT_ID;
      willThrow(new DataAccessException("Data access failed") {})
          .given(valueOperations)
          .set(anyString(), anyString(), any(Duration.class));

      // when & then
      CommonException exception =
          catchThrowableOfType(() -> adapter.merge(anonymousId, userId), CommonException.class);
      assertAll(
          () -> assertThat(exception).isNotNull(),
          () ->
              assertThat(exception.getErrorCode())
                  .isEqualTo(CommonErrorStatus.DATA_ACCESS_EXCEPTION));
    }

    @Test
    @DisplayName("userId가 null일 때도 정상 처리")
    void mergeWhenUserIdIsNullHandlesCorrectly() {
      // given
      String anonymousId = "anonymous-1";
      Long userId = null;

      // when & then
      NullPointerException exception =
          catchThrowableOfType(
              () -> adapter.merge(anonymousId, userId), NullPointerException.class);
      assertThat(exception).isNotNull();
    }

    @Test
    @DisplayName("anonymousId가 빈 문자열일 때도 정상 처리")
    void mergeWhenAnonymousIdIsEmptyStringHandlesCorrectly() {
      // given
      String anonymousId = "";
      Long userId = PROJECT_ID;
      String expectedKey = "behaviorlog:anonymous:" + anonymousId;

      // when
      adapter.merge(anonymousId, userId);

      // then
      then(valueOperations)
          .should()
          .set(eq(expectedKey), eq(userId.toString()), eq(Duration.ofDays(7)));
    }
  }

  @Nested
  @DisplayName("findMergedUserId 메서드 테스트")
  class FindMergedUserIdTest {

    @Test
    @DisplayName("성공: 매핑된 userId 조회")
    void findMergedUserIdReturnsSuccess() {
      // given
      String anonymousId = "anonymous-1";
      String expectedKey = "behaviorlog:anonymous:" + anonymousId;
      String userIdString = "456";
      given(valueOperations.get(expectedKey)).willReturn(userIdString);

      // when
      Optional<Long> result = adapter.findMergedUserId(anonymousId);

      // then
      assertAll(
          () -> assertThat(result).isPresent(),
          () -> assertThat(result).contains(EXPECTED_USER_ID));

      then(valueOperations).should().get(expectedKey);
    }

    @Test
    @DisplayName("매핑된 userId가 없을 때 Optional.empty() 반환")
    void findMergedUserIdWhenNoMappedUserIdReturnsEmptyOptional() {
      // given
      String anonymousId = "anonymous-1";
      String expectedKey = "behaviorlog:anonymous:" + anonymousId;
      given(valueOperations.get(expectedKey)).willReturn(null);

      // when
      Optional<Long> result = adapter.findMergedUserId(anonymousId);

      // then
      assertThat(result).isEmpty();
      then(valueOperations).should().get(expectedKey);
    }

    @Test
    @DisplayName("Redis 연결 실패 시 CommonException 발생")
    void findMergedUserIdWhenRedisConnectionFailsThrowsException() {
      // given
      String anonymousId = "anonymous-1";
      willThrow(new RedisConnectionFailureException("Redis connection failed"))
          .given(valueOperations)
          .get(anyString());

      // when & then
      CommonException exception =
          catchThrowableOfType(() -> adapter.findMergedUserId(anonymousId), CommonException.class);
      assertAll(
          () -> assertThat(exception).isNotNull(),
          () ->
              assertThat(exception.getErrorCode())
                  .isEqualTo(CommonErrorStatus.REDIS_CONNECTION_FAILURE));
    }

    @Test
    @DisplayName("DataAccessException 발생 시 CommonException 발생")
    void findMergedUserIdWhenDataAccessExceptionThrowsException() {
      // given
      String anonymousId = "anonymous-1";
      willThrow(new DataAccessException("Data access failed") {})
          .given(valueOperations)
          .get(anyString());

      // when & then
      CommonException exception =
          catchThrowableOfType(() -> adapter.findMergedUserId(anonymousId), CommonException.class);
      assertAll(
          () -> assertThat(exception).isNotNull(),
          () ->
              assertThat(exception.getErrorCode())
                  .isEqualTo(CommonErrorStatus.DATA_ACCESS_EXCEPTION));
    }

    @Test
    @DisplayName("잘못된 userId 형식일 때 NumberFormatException 발생")
    void findMergedUserIdWhenInvalidUserIdFormatThrowsNumberFormatException() {
      // given
      String anonymousId = "anonymous-1";
      String expectedKey = "behaviorlog:anonymous:" + anonymousId;
      given(valueOperations.get(expectedKey)).willReturn("invalid-number");

      // when & then
      NumberFormatException exception =
          catchThrowableOfType(
              () -> adapter.findMergedUserId(anonymousId), NumberFormatException.class);
      assertThat(exception).isNotNull();
    }

    @Test
    @DisplayName("빈 문자열 userId일 때 NumberFormatException 발생")
    void findMergedUserIdWhenEmptyStringUserIdThrowsNumberFormatException() {
      // given
      String anonymousId = "anonymous-1";
      String expectedKey = "behaviorlog:anonymous:" + anonymousId;
      given(valueOperations.get(expectedKey)).willReturn("");

      // when & then
      NumberFormatException exception =
          catchThrowableOfType(
              () -> adapter.findMergedUserId(anonymousId), NumberFormatException.class);
      assertThat(exception).isNotNull();
    }
  }
}
