package com.dataracy.modules.project.adapter.redis;

import com.dataracy.modules.common.exception.CommonException;
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

import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjectViewCountRedisAdapterTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    
    @Mock
    private ValueOperations<String, String> valueOperations;

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
        void increaseViewCount_최초조회_조회수증가() {
            // given
            Long projectId = 1L;
            String viewerId = "user123";
            String targetType = "project";
            
            given(valueOperations.setIfAbsent(
                "viewDedup:project:1:user123", 
                "1", 
                Duration.ofMinutes(5)
            )).willReturn(true);

            // when
            adapter.increaseViewCount(projectId, viewerId, targetType);

            // then
            then(valueOperations).should().setIfAbsent(
                "viewDedup:project:1:user123", 
                "1", 
                Duration.ofMinutes(5)
            );
            then(valueOperations).should().increment("viewCount:project:1");
        }

        @Test
        @DisplayName("중복 조회 시 조회수 증가하지 않음")
        void increaseViewCount_중복조회_조회수증가하지않음() {
            // given
            Long projectId = 1L;
            String viewerId = "user123";
            String targetType = "project";
            
            given(valueOperations.setIfAbsent(
                "viewDedup:project:1:user123", 
                "1", 
                Duration.ofMinutes(5)
            )).willReturn(false);

            // when
            adapter.increaseViewCount(projectId, viewerId, targetType);

            // then
            then(valueOperations).should().setIfAbsent(
                "viewDedup:project:1:user123", 
                "1", 
                Duration.ofMinutes(5)
            );
            // increment는 호출되지 않아야 함
        }

        @Test
        @DisplayName("예외 발생 시 CommonException 변환")
        void increaseViewCount_예외발생_CommonException변환() {
            // given
            Long projectId = 1L;
            String viewerId = "user123";
            String targetType = "project";
            RedisConnectionFailureException exception = new RedisConnectionFailureException("Redis connection failed");
            
            given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .willThrow(exception);

            // when & then
            assertThatThrownBy(() -> adapter.increaseViewCount(projectId, viewerId, targetType))
                .isInstanceOf(CommonException.class);
        }
    }

    @Nested
    @DisplayName("getViewCount 메서드 테스트")
    class GetViewCountTest {

        @Test
        @DisplayName("성공: 조회수 조회")
        void getViewCount_성공() {
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
        void getViewCount_키없음_0반환() {
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
        void getViewCount_예외발생_CommonException변환() {
            // given
            Long projectId = 1L;
            String targetType = "project";
            DataAccessException exception = new DataAccessException("Data access failed") {};
            
            given(valueOperations.get(anyString())).willThrow(exception);

            // when & then
            assertThatThrownBy(() -> adapter.getViewCount(projectId, targetType))
                .isInstanceOf(CommonException.class);
        }
    }

    @Nested
    @DisplayName("getAllViewCountKeys 메서드 테스트")
    class GetAllViewCountKeysTest {

        @Test
        @DisplayName("성공: 모든 조회수 키 조회")
        void getAllViewCountKeys_성공() {
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
        void clearViewCount_성공() {
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
        void popViewCount_성공() {
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
        void popViewCount_키없음_0반환() {
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
