package com.dataracy.modules.dataset.application.service.batch;

import com.dataracy.modules.dataset.application.dto.response.read.PopularDataResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataLabelMapResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.mapper.read.DataReadDtoMapper;
import com.dataracy.modules.dataset.application.port.in.query.read.FindDataLabelMapUseCase;
import com.dataracy.modules.dataset.application.port.out.query.read.GetPopularDataSetsPort;
import com.dataracy.modules.dataset.application.port.out.storage.PopularDataSetsStoragePort;
import com.dataracy.modules.dataset.domain.model.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PopularDataSetsBatchServiceTest {

    @Mock
    private DataReadDtoMapper dataReadDtoMapper;

    @Mock
    private FindDataLabelMapUseCase findDataLabelMapUseCase;

    @Mock
    private PopularDataSetsStoragePort popularDataSetsStoragePort;

    @Mock
    private GetPopularDataSetsPort getPopularDataSetsPort;

    @InjectMocks
    private PopularDataSetsBatchService popularDataSetsBatchService;

    private Data createSampleData(Long id, Long userId, Long topicId, Long dataSourceId, Long dataTypeId) {
        return Data.of(
                id, "title", userId, topicId, dataSourceId, dataTypeId,
                LocalDate.now(), LocalDate.now(), "description", "genre", "file",
                "thumbnail", 1, 10L, 
                com.dataracy.modules.dataset.domain.model.DataMetadata.of(1L, 1, 1, "{}"), 
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("인기 데이터셋 캐시 업데이트")
    class UpdatePopularDataSetsCache {

        @Test
        @DisplayName("스케줄된 캐시 업데이트 성공")
        void updatePopularDataSetsCacheSuccess() {
            // given
            Data data1 = createSampleData(1L, 100L, 10L, 20L, 30L);
            Data data2 = createSampleData(2L, 200L, 11L, 21L, 31L);
            List<DataWithProjectCountDto> savedDataSets = List.of(
                    new DataWithProjectCountDto(data1, 5L),
                    new DataWithProjectCountDto(data2, 3L)
            );

            DataLabelMapResponse labelResponse = new DataLabelMapResponse(
                    Map.of(100L, "user1", 200L, "user2"),
                    Map.of(100L, "profile1.png", 200L, "profile2.png"),
                    Map.of(10L, "topic1", 11L, "topic2"),
                    Map.of(20L, "source1", 21L, "source2"),
                    Map.of(30L, "type1", 31L, "type2")
            );

            PopularDataResponse response1 = new PopularDataResponse(
                    1L, "title", 100L, "user1", "profile1.png", "topic1", "source1", "type1",
                    null, null, "description", "thumbnail", 1, 10L, 1, 1,
                    LocalDateTime.now(), 5L
            );
            PopularDataResponse response2 = new PopularDataResponse(
                    2L, "title", 200L, "user2", "profile2.png", "topic2", "source2", "type2",
                    null, null, "description", "thumbnail", 1, 10L, 1, 1,
                    LocalDateTime.now(), 3L
            );

            given(getPopularDataSetsPort.getPopularDataSets(20)).willReturn(savedDataSets);
            given(findDataLabelMapUseCase.labelMapping(savedDataSets)).willReturn(labelResponse);
            given(dataReadDtoMapper.toResponseDto(any(), any(), any(), any(), any(), any(), any()))
                    .willReturn(response1, response2);

            // when
            popularDataSetsBatchService.updatePopularDataSetsCache();

            // then
            then(getPopularDataSetsPort).should().getPopularDataSets(20);
            then(findDataLabelMapUseCase).should().labelMapping(savedDataSets);
            then(dataReadDtoMapper).should(times(2)).toResponseDto(any(), any(), any(), any(), any(), any(), any());
            then(popularDataSetsStoragePort).should().setPopularDataSets(any());
        }

        @Test
        @DisplayName("스케줄된 캐시 업데이트 실패 - 예외 발생")
        void updatePopularDataSetsCacheFailure() {
            // given
            willThrow(new RuntimeException("Database error"))
                    .given(getPopularDataSetsPort).getPopularDataSets(20);

            // when
            popularDataSetsBatchService.updatePopularDataSetsCache();

            // then
            then(getPopularDataSetsPort).should().getPopularDataSets(20);
            then(popularDataSetsStoragePort).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("수동 캐시 업데이트")
    class ManualUpdatePopularDataSetsCache {

        @Test
        @DisplayName("수동 캐시 업데이트 성공")
        void manualUpdatePopularDataSetsCacheSuccess() {
            // given
            int size = 10;
            Data data = createSampleData(1L, 100L, 10L, 20L, 30L);
            List<DataWithProjectCountDto> savedDataSets = List.of(
                    new DataWithProjectCountDto(data, 5L)
            );

            DataLabelMapResponse labelResponse = new DataLabelMapResponse(
                    Map.of(100L, "user1"),
                    Map.of(100L, "profile1.png"),
                    Map.of(10L, "topic1"),
                    Map.of(20L, "source1"),
                    Map.of(30L, "type1")
            );

            PopularDataResponse response = new PopularDataResponse(
                    1L, "title", 100L, "user1", "profile1.png", "topic1", "source1", "type1",
                    null, null, "description", "thumbnail", 1, 10L, 1, 1,
                    LocalDateTime.now(), 5L
            );

            given(getPopularDataSetsPort.getPopularDataSets(size)).willReturn(savedDataSets);
            given(findDataLabelMapUseCase.labelMapping(savedDataSets)).willReturn(labelResponse);
            given(dataReadDtoMapper.toResponseDto(any(), any(), any(), any(), any(), any(), any()))
                    .willReturn(response);

            // when
            popularDataSetsBatchService.manualUpdatePopularDataSetsCache(size);

            // then
            then(getPopularDataSetsPort).should().getPopularDataSets(size);
            then(findDataLabelMapUseCase).should().labelMapping(savedDataSets);
            then(dataReadDtoMapper).should().toResponseDto(any(), any(), any(), any(), any(), any(), any());
            then(popularDataSetsStoragePort).should().setPopularDataSets(any());
        }

        @Test
        @DisplayName("수동 캐시 업데이트 실패 - 예외 발생")
        void manualUpdatePopularDataSetsCacheFailure() {
            // given
            int size = 5;
            willThrow(new RuntimeException("Database connection failed"))
                    .given(getPopularDataSetsPort).getPopularDataSets(size);

            // when
            popularDataSetsBatchService.manualUpdatePopularDataSetsCache(size);

            // then
            then(getPopularDataSetsPort).should().getPopularDataSets(size);
            then(popularDataSetsStoragePort).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("캐시 워밍업")
    class WarmUpCacheIfNeeded {

        @Test
        @DisplayName("캐시가 유효하지 않을 때 워밍업 실행")
        void warmUpCacheIfNeededWhenCacheInvalid() {
            // given
            int size = 15;
            given(popularDataSetsStoragePort.hasValidData()).willReturn(false);
            
            Data data = createSampleData(1L, 100L, 10L, 20L, 30L);
            List<DataWithProjectCountDto> savedDataSets = List.of(
                    new DataWithProjectCountDto(data, 5L)
            );

            DataLabelMapResponse labelResponse = new DataLabelMapResponse(
                    Map.of(100L, "user1"),
                    Map.of(100L, "profile1.png"),
                    Map.of(10L, "topic1"),
                    Map.of(20L, "source1"),
                    Map.of(30L, "type1")
            );

            PopularDataResponse response = new PopularDataResponse(
                    1L, "title", 100L, "user1", "profile1.png", "topic1", "source1", "type1",
                    null, null, "description", "thumbnail", 1, 10L, 1, 1,
                    LocalDateTime.now(), 5L
            );

            given(getPopularDataSetsPort.getPopularDataSets(size)).willReturn(savedDataSets);
            given(findDataLabelMapUseCase.labelMapping(savedDataSets)).willReturn(labelResponse);
            given(dataReadDtoMapper.toResponseDto(any(), any(), any(), any(), any(), any(), any()))
                    .willReturn(response);

            // when
            popularDataSetsBatchService.warmUpCacheIfNeeded(size);

            // then
            then(popularDataSetsStoragePort).should().hasValidData();
            then(getPopularDataSetsPort).should().getPopularDataSets(size);
            then(findDataLabelMapUseCase).should().labelMapping(savedDataSets);
            then(dataReadDtoMapper).should().toResponseDto(any(), any(), any(), any(), any(), any(), any());
            then(popularDataSetsStoragePort).should().setPopularDataSets(any());
        }

        @Test
        @DisplayName("캐시가 유효할 때 워밍업 실행하지 않음")
        void warmUpCacheIfNeededWhenCacheValid() {
            // given
            int size = 15;
            given(popularDataSetsStoragePort.hasValidData()).willReturn(true);

            // when
            popularDataSetsBatchService.warmUpCacheIfNeeded(size);

            // then
            then(popularDataSetsStoragePort).should().hasValidData();
            then(getPopularDataSetsPort).shouldHaveNoInteractions();
            then(findDataLabelMapUseCase).shouldHaveNoInteractions();
            then(dataReadDtoMapper).shouldHaveNoInteractions();
            then(popularDataSetsStoragePort).shouldHaveNoMoreInteractions();
        }
    }
}
