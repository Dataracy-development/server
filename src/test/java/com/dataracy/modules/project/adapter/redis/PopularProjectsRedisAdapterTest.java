package com.dataracy.modules.project.adapter.redis;

import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PopularProjectsRedisAdapterTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    
    @Mock
    private ValueOperations<String, String> valueOperations;
    
    @Mock
    private ObjectMapper objectMapper;

    private PopularProjectsRedisAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new PopularProjectsRedisAdapter(redisTemplate, objectMapper);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Nested
    @DisplayName("getPopularProjects 메서드 테스트")
    class GetPopularProjectsTest {

        @Test
        @DisplayName("성공: 캐시에서 인기 프로젝트 조회")
        void getPopularProjects_성공() throws Exception {
            // given
            String cachedJson = "[{\"id\":1,\"title\":\"Test Project\",\"viewCount\":100}]";
            List<PopularProjectResponse> expectedData = List.of(
                new PopularProjectResponse(
                    1L,
                    "Test Project",
                    "프로젝트 내용",
                    1L,
                    "testUser",
                    "profile.jpg",
                    "thumbnail.jpg",
                    "기술",
                    "분석",
                    "공공기관",
                    "초급",
                    10L,
                    50L,
                    100L
                )
            );
            
            given(valueOperations.get("popular:projects")).willReturn(cachedJson);
            given(objectMapper.readValue(anyString(), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .willReturn(expectedData);

            // when
            Optional<List<PopularProjectResponse>> result = adapter.getPopularProjects();

            // then
            assertAll(
                    () -> assertThat(result).isPresent(),
                    () -> assertThat(result.get()).hasSize(1),
                    () -> assertThat(result.get().get(0).id()).isEqualTo(1L),
                    () -> assertThat(result.get().get(0).title()).isEqualTo("Test Project")
            );
        }

        @Test
        @DisplayName("캐시에 데이터가 없을 때 빈 Optional 반환")
        void getPopularProjects_캐시에데이터없음_빈Optional반환() {
            // given
            given(valueOperations.get("popular:projects")).willReturn(null);

            // when
            Optional<List<PopularProjectResponse>> result = adapter.getPopularProjects();

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("setPopularProjects 메서드 테스트")
    class SetPopularProjectsTest {

        @Test
        @DisplayName("성공: 인기 프로젝트 캐시 저장")
        void setPopularProjects_성공() throws Exception {
            // given
            List<PopularProjectResponse> popularProjects = List.of(
                new PopularProjectResponse(
                    1L,
                    "Test Project",
                    "프로젝트 내용",
                    1L,
                    "testUser",
                    "profile.jpg",
                    "thumbnail.jpg",
                    "기술",
                    "분석",
                    "공공기관",
                    "초급",
                    10L,
                    50L,
                    100L
                )
            );
            String jsonData = "[{\"id\":1,\"title\":\"Test Project\",\"viewCount\":100}]";
            
            given(objectMapper.writeValueAsString(popularProjects)).willReturn(jsonData);

            // when
            adapter.setPopularProjects(popularProjects);

            // then
            then(valueOperations).should().set(eq("popular:projects"), eq(jsonData), eq(Duration.ofMinutes(10)));
            then(valueOperations).should().set(eq("popular:projects:metadata"), anyString(), eq(Duration.ofMinutes(10)));
        }
    }

    @Nested
    @DisplayName("getLastUpdateTime 메서드 테스트")
    class GetLastUpdateTimeTest {

        @Test
        @DisplayName("성공: 마지막 업데이트 시간 조회")
        void getLastUpdateTime_성공() {
            // given
            String metadata = "1234567890123";
            given(valueOperations.get("popular:projects:metadata")).willReturn(metadata);

            // when
            Optional<Long> result = adapter.getLastUpdateTime();

            // then
            assertAll(
                    () -> assertThat(result).isPresent(),
                    () -> assertThat(result).contains(1234567890123L)
            );
        }

        @Test
        @DisplayName("메타데이터가 없을 때 빈 Optional 반환")
        void getLastUpdateTime_메타데이터없음_빈Optional반환() {
            // given
            given(valueOperations.get("popular:projects:metadata")).willReturn(null);

            // when
            Optional<Long> result = adapter.getLastUpdateTime();

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("hasValidData 메서드 테스트")
    class HasValidDataTest {

        @Test
        @DisplayName("캐시가 존재할 때 true 반환")
        void hasValidData_캐시존재_true반환() {
            // given
            given(redisTemplate.hasKey("popular:projects")).willReturn(true);
            given(redisTemplate.hasKey("popular:projects:metadata")).willReturn(true);

            // when
            boolean result = adapter.hasValidData();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("캐시가 없을 때 false 반환")
        void hasValidData_캐시없음_false반환() {
            // given
            given(redisTemplate.hasKey("popular:projects")).willReturn(false);
            given(redisTemplate.hasKey("popular:projects:metadata")).willReturn(true);

            // when
            boolean result = adapter.hasValidData();

            // then
            assertThat(result).isFalse();
        }
    }
}
