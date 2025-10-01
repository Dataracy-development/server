package com.dataracy.modules.dataset.adapter.redis;

import com.dataracy.modules.dataset.application.dto.response.read.PopularDataResponse;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
class PopularDataSetsRedisAdapterTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    
    @Mock
    private ValueOperations<String, String> valueOperations;
    
    @Mock
    private ObjectMapper objectMapper;

    private PopularDataSetsRedisAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new PopularDataSetsRedisAdapter(redisTemplate, objectMapper);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Nested
    @DisplayName("getPopularDataSets 메서드 테스트")
    class GetPopularDataSetsTest {

        @Test
        @DisplayName("성공: 캐시에서 인기 데이터셋 조회")
        void getPopularDataSets_성공() throws Exception {
            // given
            String cachedJson = "[{\"id\":1,\"title\":\"Test Data\",\"downloadCount\":100}]";
            List<PopularDataResponse> expectedData = List.of(
                new PopularDataResponse(
                    1L,
                    "Test Data",
                    1L,
                    "testUser",
                    "profile.jpg",
                    "기술",
                    "공공기관",
                    "CSV",
                    LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 12, 31),
                    "테스트 데이터",
                    "thumbnail.jpg",
                    100,
                    1024L,
                    1000,
                    10,
                    LocalDateTime.now(),
                    5L
                )
            );
            
            given(valueOperations.get("popular:datasets")).willReturn(cachedJson);
            given(objectMapper.readValue(anyString(), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .willReturn(expectedData);

            // when
            Optional<List<PopularDataResponse>> result = adapter.getPopularDataSets();

            // then
            assertAll(
                    () -> assertThat(result).isPresent(),
                    () -> assertThat(result.get()).hasSize(1),
                    () -> assertThat(result.get().get(0).id()).isEqualTo(1L),
                    () -> assertThat(result.get().get(0).title()).isEqualTo("Test Data")
            );
        }

        @Test
        @DisplayName("캐시에 데이터가 없을 때 빈 Optional 반환")
        void getPopularDataSets_캐시에데이터없음_빈Optional반환() {
            // given
            given(valueOperations.get("popular:datasets")).willReturn(null);

            // when
            Optional<List<PopularDataResponse>> result = adapter.getPopularDataSets();

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("setPopularDataSets 메서드 테스트")
    class SetPopularDataSetsTest {

        @Test
        @DisplayName("성공: 인기 데이터셋 캐시 저장")
        void setPopularDataSets_성공() throws Exception {
            // given
            List<PopularDataResponse> popularDataSets = List.of(
                new PopularDataResponse(
                    1L,
                    "Test Data",
                    1L,
                    "testUser",
                    "profile.jpg",
                    "기술",
                    "공공기관",
                    "CSV",
                    LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 12, 31),
                    "테스트 데이터",
                    "thumbnail.jpg",
                    100,
                    1024L,
                    1000,
                    10,
                    LocalDateTime.now(),
                    5L
                )
            );
            String jsonData = "[{\"id\":1,\"title\":\"Test Data\",\"downloadCount\":100}]";
            
            given(objectMapper.writeValueAsString(popularDataSets)).willReturn(jsonData);

            // when
            adapter.setPopularDataSets(popularDataSets);

            // then
            then(valueOperations).should().set(eq("popular:datasets"), eq(jsonData), eq(Duration.ofMinutes(10)));
            then(valueOperations).should().set(eq("popular:datasets:metadata"), anyString(), eq(Duration.ofMinutes(10)));
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
            given(valueOperations.get("popular:datasets:metadata")).willReturn(metadata);

            // when
            Optional<Long> result = adapter.getLastUpdateTime();

            // then
            assertAll(
                    () -> assertThat(result).isPresent(),
                    () -> assertThat(result.get()).isEqualTo(1234567890123L)
            );
        }

        @Test
        @DisplayName("메타데이터가 없을 때 빈 Optional 반환")
        void getLastUpdateTime_메타데이터없음_빈Optional반환() {
            // given
            given(valueOperations.get("popular:datasets:metadata")).willReturn(null);

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
            given(redisTemplate.hasKey("popular:datasets")).willReturn(true);
            given(redisTemplate.hasKey("popular:datasets:metadata")).willReturn(true);

            // when
            boolean result = adapter.hasValidData();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("캐시가 없을 때 false 반환")
        void hasValidData_캐시없음_false반환() {
            // given
            given(redisTemplate.hasKey("popular:datasets")).willReturn(false);
            given(redisTemplate.hasKey("popular:datasets:metadata")).willReturn(true);

            // when
            boolean result = adapter.hasValidData();

            // then
            assertThat(result).isFalse();
        }
    }
}
