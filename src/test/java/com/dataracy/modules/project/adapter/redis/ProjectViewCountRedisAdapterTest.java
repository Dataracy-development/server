package com.dataracy.modules.project.adapter.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.time.Duration;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.dataracy.modules.common.exception.CommonException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjectViewCountRedisAdapterTest {

  @Mock private StringRedisTemplate redisTemplate;

  @Mock private ValueOperations<String, String> valueOperations;

  private ProjectViewCountRedisAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new ProjectViewCountRedisAdapter(redisTemplate);
    given(redisTemplate.opsForValue()).willReturn(valueOperations);
  }

  @Nested
  @DisplayName("increaseViewCount 메서드 테스트")
  class IncreaseViewCountTest {

    @Test
    @DisplayName("성공: 최초 조회 시 조회수 증가")
    void increaseViewCountOnFirstViewIncrementsCount() {
      // given
      Long projectId = 1L;
      String viewerId = "user1";
      String targetType = "project";

      given(valueOperations.setIfAbsent("viewDedup:project:1:user1", "1", Duration.ofMinutes(5)))
          .willReturn(true);

      // when
      adapter.increaseViewCount(projectId, viewerId, targetType);

      // then
      then(valueOperations)
          .should()
          .setIfAbsent("viewDedup:project:1:user1", "1", Duration.ofMinutes(5));
      then(valueOperations).should().increment("viewCount:project:1");
    }

    @Test
    @DisplayName("중복 조회 시 조회수 증가하지 않음")
    void increaseViewCountOnDuplicateViewDoesNotIncrement() {
      // given
      Long projectId = 1L;
      String viewerId = "user1";
      String targetType = "project";

      given(valueOperations.setIfAbsent("viewDedup:project:1:user1", "1", Duration.ofMinutes(5)))
          .willReturn(false);

      // when
      adapter.increaseViewCount(projectId, viewerId, targetType);

      // then
      then(valueOperations)
          .should()
          .setIfAbsent("viewDedup:project:1:user1", "1", Duration.ofMinutes(5));
      // increment는 호출되지 않아야 함
    }

    @Test
    @DisplayName("예외 발생 시 CommonException 변환")
    void increaseViewCountWhenExceptionOccursThrowsCommonException() {
      // given
      Long projectId = 1L;
      String viewerId = "user1";
      String targetType = "project";
      RedisConnectionFailureException redisException =
          new RedisConnectionFailureException("Redis connection failed");

      given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
          .willThrow(redisException);

      // when & then
      CommonException exception =
          catchThrowableOfType(
              () -> adapter.increaseViewCount(projectId, viewerId, targetType),
              CommonException.class);
      assertAll(() -> org.assertj.core.api.Assertions.assertThat(exception).isNotNull());
    }
  }

  @Nested
  @DisplayName("getViewCount 메서드 테스트")
  class GetViewCountTest {

    @Test
    @DisplayName("성공: 조회수 조회")
    void getViewCountReturnsSuccess() {
      // given
      Long projectId = 1L;
      String targetType = "project";
      String viewCountValue = "100";

      given(valueOperations.get("viewCount:project:1")).willReturn(viewCountValue);

      // when
      Long result = adapter.getViewCount(projectId, targetType);

      // then
      assertThat(result).isEqualTo(100L);
      then(valueOperations).should().get("viewCount:project:1");
    }

    @Test
    @DisplayName("키가 없을 때 0 반환")
    void getViewCountWhenKeyNotFoundReturnsZero() {
      // given
      Long projectId = 1L;
      String targetType = "project";

      given(valueOperations.get("viewCount:project:1")).willReturn(null);

      // when
      Long result = adapter.getViewCount(projectId, targetType);

      // then
      assertThat(result).isZero();
    }

    @Test
    @DisplayName("예외 발생 시 CommonException 변환")
    void getViewCountWhenExceptionOccursThrowsCommonException() {
      // given
      Long projectId = 1L;
      String targetType = "project";
      DataAccessException dataException = new DataAccessException("Data access failed") {};

      given(valueOperations.get(anyString())).willThrow(dataException);

      // when & then
      CommonException exception =
          catchThrowableOfType(
              () -> adapter.getViewCount(projectId, targetType), CommonException.class);
      assertAll(() -> org.assertj.core.api.Assertions.assertThat(exception).isNotNull());
    }
  }

  @Nested
  @DisplayName("getAllViewCountKeys 메서드 테스트")
  class GetAllViewCountKeysTest {

    @Test
    @DisplayName("성공: 모든 조회수 키 조회")
    void getAllViewCountKeysReturnsSuccess() {
      // given
      String targetType = "project";

      Cursor<String> cursor = mock(Cursor.class);
      given(cursor.hasNext()).willReturn(true, true, false);
      given(cursor.next()).willReturn("viewCount:project:1", "viewCount:project:2");

      given(redisTemplate.scan(any(ScanOptions.class))).willReturn(cursor);

      // when
      Set<String> result = adapter.getAllViewCountKeys(targetType);

      // then
      assertThat(result).containsExactlyInAnyOrder("viewCount:project:1", "viewCount:project:2");
    }
  }

  @Nested
  @DisplayName("clearViewCount 메서드 테스트")
  class ClearViewCountTest {

    @Test
    @DisplayName("성공: 조회수 키 삭제")
    void clearViewCountReturnsSuccess() {
      // given
      Long targetId = 1L;
      String targetType = "project";

      // when
      adapter.clearViewCount(targetId, targetType);

      // then
      then(redisTemplate).should().delete("viewCount:project:1");
    }
  }

  @Nested
  @DisplayName("popViewCount 메서드 테스트")
  class PopViewCountTest {

    @Test
    @DisplayName("성공: 조회수 Pop")
    void popViewCountReturnsSuccess() {
      // given
      Long projectId = 1L;
      String targetType = "project";

      given(redisTemplate.execute(any(), any(Boolean.class))).willReturn(150L);

      // when
      Long result = adapter.popViewCount(projectId, targetType);

      // then
      assertThat(result).isEqualTo(150L);
    }

    @Test
    @DisplayName("키가 없을 때 0 반환")
    void popViewCountWhenKeyNotFoundReturnsZero() {
      // given
      Long projectId = 1L;
      String targetType = "project";

      given(redisTemplate.execute(any(), any(Boolean.class))).willReturn(0L);

      // when
      Long result = adapter.popViewCount(projectId, targetType);

      // then
      assertThat(result).isZero();
    }
  }
}
