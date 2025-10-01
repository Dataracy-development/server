package com.dataracy.modules.behaviorlog.adapter.redis;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
/**
 * BehaviorLogRedisMergeAdapter 테스트
 */
@ExtendWith(MockitoExtension.class)
class BehaviorLogRedisMergeAdapterTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    
    @Mock
    private ValueOperations<String, String> valueOperations;
    
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
        void merge_성공() {
            // given
            String anonymousId = "anonymous-123";
            Long userId = 456L;
            String expectedKey = "behaviorlog:anonymous:" + anonymousId;

            // when
            adapter.merge(anonymousId, userId);

            // then
            then(valueOperations).should()
                    .set(eq(expectedKey), eq(userId.toString()), eq(Duration.ofDays(7)));
        }

        @Test
        @DisplayName("Redis 연결 실패 시 CommonException 발생")
        void merge_Redis연결실패_예외발생() {
            // given
            String anonymousId = "anonymous-123";
            Long userId = 456L;
            willThrow(new  RedisConnectionFailureException("Redis connection failed"))
                    .given(valueOperations).set(anyString(), anyString(), any(Duration.class));

            // when & then
            CommonException exception = catchThrowableOfType(() -> adapter.merge(anonymousId, userId), CommonException.class);
            assertAll(

                    () -> assertThat(exception).isNotNull(),

                    () -> assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.REDIS_CONNECTION_FAILURE)

            );

        }

        @Test
        @DisplayName("DataAccessException 발생 시 CommonException 발생")
        void merge_DataAccessException_예외발생() {
            // given
            String anonymousId = "anonymous-123";
            Long userId = 456L;
            willThrow(new  DataAccessException("Data access failed") {})
                    .given(valueOperations).set(anyString(), anyString(), any(Duration.class));

            // when & then
            CommonException exception = catchThrowableOfType(() -> adapter.merge(anonymousId, userId), CommonException.class);
            assertAll(

                    () -> assertThat(exception).isNotNull(),

                    () -> assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.DATA_ACCESS_EXCEPTION)

            );

        }

        @Test
        @DisplayName("userId가 null일 때도 정상 처리")
        void merge_userId가null_정상처리() {
            // given
            String anonymousId = "anonymous-123";
            Long userId = null;

            // when & then
            NullPointerException exception = catchThrowableOfType(() -> adapter.merge(anonymousId, userId), NullPointerException.class);
            assertThat(exception).isNotNull();
        }

        @Test
        @DisplayName("anonymousId가 빈 문자열일 때도 정상 처리")
        void merge_anonymousId가빈문자열_정상처리() {
            // given
            String anonymousId = "";
            Long userId = 456L;
            String expectedKey = "behaviorlog:anonymous:" + anonymousId;

            // when
            adapter.merge(anonymousId, userId);

            // then
            then(valueOperations).should()
                    .set(eq(expectedKey), eq(userId.toString()), eq(Duration.ofDays(7)));
        }
    }

    @Nested
    @DisplayName("findMergedUserId 메서드 테스트")
    class FindMergedUserIdTest {

        @Test
        @DisplayName("성공: 매핑된 userId 조회")
        void findMergedUserId_성공() {
            // given
            String anonymousId = "anonymous-123";
            String expectedKey = "behaviorlog:anonymous:" + anonymousId;
            String userIdString = "456";
            given(valueOperations.get(expectedKey)).willReturn(userIdString);

            // when
            Optional<Long> result = adapter.findMergedUserId(anonymousId);

            // then
            assertAll(

                    () -> assertThat(result).isPresent(),

src/test/java/com/dataracy/modules/comment/domain/model/CommentTest.java                    () -> assertThat(result).contains(456L)

            );

            then(valueOperations).should().get(expectedKey);
        }

        @Test
        @DisplayName("매핑된 userId가 없을 때 Optional.empty() 반환")
        void findMergedUserId_매핑된userId없음_OptionalEmpty반환() {
            // given
            String anonymousId = "anonymous-123";
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
        void findMergedUserId_Redis연결실패_예외발생() {
            // given
            String anonymousId = "anonymous-123";
            willThrow(new  RedisConnectionFailureException("Redis connection failed"))
                    .given(valueOperations).get(anyString());

            // when & then
            CommonException exception = catchThrowableOfType(() -> adapter.findMergedUserId(anonymousId), CommonException.class);
            assertAll(

                    () -> assertThat(exception).isNotNull(),

                    () -> assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.REDIS_CONNECTION_FAILURE)

            );

        }

        @Test
        @DisplayName("DataAccessException 발생 시 CommonException 발생")
        void findMergedUserId_DataAccessException_예외발생() {
            // given
            String anonymousId = "anonymous-123";
            willThrow(new  DataAccessException("Data access failed") {})
                    .given(valueOperations).get(anyString());

            // when & then
            CommonException exception = catchThrowableOfType(() -> adapter.findMergedUserId(anonymousId), CommonException.class);
            assertAll(

                    () -> assertThat(exception).isNotNull(),

                    () -> assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.DATA_ACCESS_EXCEPTION)

            );

        }

        @Test
        @DisplayName("잘못된 userId 형식일 때 NumberFormatException 발생")
        void findMergedUserId_잘못된userId형식_NumberFormatException발생() {
            // given
            String anonymousId = "anonymous-123";
            String expectedKey = "behaviorlog:anonymous:" + anonymousId;
            given(valueOperations.get(expectedKey)).willReturn("invalid-number");

            // when & then
            NumberFormatException exception = catchThrowableOfType(() -> adapter.findMergedUserId(anonymousId), NumberFormatException.class);
            assertThat(exception).isNotNull();
        }

        @Test
        @DisplayName("빈 문자열 userId일 때 NumberFormatException 발생")
        void findMergedUserId_빈문자열userId_NumberFormatException발생() {
            // given
            String anonymousId = "anonymous-123";
            String expectedKey = "behaviorlog:anonymous:" + anonymousId;
            given(valueOperations.get(expectedKey)).willReturn("");

            // when & then
            NumberFormatException exception = catchThrowableOfType(() -> adapter.findMergedUserId(anonymousId), NumberFormatException.class);
            assertThat(exception).isNotNull();
        }
    }
}
