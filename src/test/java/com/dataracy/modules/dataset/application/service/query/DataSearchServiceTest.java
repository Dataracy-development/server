package com.dataracy.modules.dataset.application.service.query;

import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;
import com.dataracy.modules.dataset.application.dto.response.search.FilteredDataResponse;
import com.dataracy.modules.dataset.application.dto.response.search.SimilarDataResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataLabelMapResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.mapper.search.FilteredDataDtoMapper;
import com.dataracy.modules.dataset.application.port.in.query.read.FindDataLabelMapUseCase;
import com.dataracy.modules.dataset.application.port.out.query.read.FindDataPort;
import com.dataracy.modules.dataset.application.port.out.query.search.SearchFilteredDataSetsPort;
import com.dataracy.modules.dataset.application.port.out.query.search.SearchRealTimeDataSetsPort;
import com.dataracy.modules.dataset.application.port.out.query.search.SearchSimilarDataSetsPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.model.DataMetadata;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DataSearchServiceTest {

    @InjectMocks
    private DataSearchService service;

    @Mock
    private FilteredDataDtoMapper mapper;

    @Mock
    private FindDataPort findDataPort;

    @Mock
    private SearchSimilarDataSetsPort similarPort;

    @Mock
    private SearchFilteredDataSetsPort filteredPort;

    @Mock
    private SearchRealTimeDataSetsPort rtPort;

    @Mock
    private FindDataLabelMapUseCase labelMapUseCase;

    private Data sample() {
        return Data.of(
                1L,
                "t",
                2L,
                3L,
                4L,
                5L,
                LocalDate.now(),
                LocalDate.now(),
                "d",
                "g",
                "f",
                "thumb",
                1,
                10L,
                DataMetadata.of(1L, 1, 1, "{}"),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("유사 데이터셋 조회")
    class SimilarDataSets {

        @Test
        @DisplayName("유사 데이터셋 검색 성공")
        void searchSimilarDataSetsSuccess() {
            // given
            given(findDataPort.findDataById(1L))
                    .willReturn(Optional.of(sample()));
            given(similarPort.searchSimilarDataSets(any(), eq(3)))
                    .willReturn(List.of(new SimilarDataResponse(
                            1L, "t", 1L, "userA", "https://~~", "topic", "ds", "dt",
                            null, null, "d", "thumb", 1, 10L, 1, 1, LocalDateTime.now()
                    )));

            // when
            List<SimilarDataResponse> res = service.searchSimilarDataSets(1L, 3);

            // then
            assertThat(res).hasSize(1);
        }

        @Test
        @DisplayName("유사 데이터셋 검색 실패 → 데이터셋 없음 예외 발생")
        void searchSimilarDataSetsThrowsWhenNotFound() {
            // given
            given(findDataPort.findDataById(1L))
                    .willReturn(Optional.empty());

            // when & then
            DataException ex = catchThrowableOfType(
                    () -> service.searchSimilarDataSets(1L, 3),
                    DataException.class
            );
            assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
        }
    }

    @Nested
    @DisplayName("필터링된 데이터셋 조회")
    class FilteredDataSets {

        @Test
        @DisplayName("필터 조건으로 데이터셋 검색 성공")
        void searchFilteredDataSetsSuccess() {
            // given
            FilteringDataRequest req = new FilteringDataRequest(
                    "k", "LATEST", 2L, 3L, 4L, 2023
            );
            DataWithProjectCountDto dto = new DataWithProjectCountDto(sample(), 2L);
            Page<DataWithProjectCountDto> page = new PageImpl<>(List.of(dto));

            given(filteredPort.searchByFilters(eq(req), any(), any()))
                    .willReturn(page);
            given(labelMapUseCase.labelMapping(any()))
                    .willReturn(new DataLabelMapResponse(Map.of(), Map.of(), Map.of(), Map.of(), Map.of()));
            given(mapper.toResponseDto(any(), any(), any(), any(), any(), any(), any()))
                    .willReturn(new FilteredDataResponse(
                            1L, "t", 1L, "userA", "https://~~", "topic", "ds", "dt",
                            null, null, "d", "thumb", 1, 10L, 1, 1,
                            LocalDateTime.now(), 2L
                    ));

            // when
            Page<FilteredDataResponse> res =
                    service.searchFilteredDataSets(req, PageRequest.of(0, 10));

            // then
            assertThat(res.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("실시간 데이터셋 조회")
    class RealTimeDataSets {

        @Test
        @DisplayName("실시간 데이터셋 검색 성공")
        void searchRealTimeDataSetsSuccess() {
            // given
            given(rtPort.searchRealTimeDataSets("k", 3))
                    .willReturn(List.of(new RecentMinimalDataResponse(
                            1L, "t", 1L, "userA", "https://~~", "thumb", LocalDateTime.now()
                    )));

            // when
            List<RecentMinimalDataResponse> res = service.searchRealTimeDataSets("k", 3);

            // then
            assertThat(res).hasSize(1);
        }

        @Test
        @DisplayName("실시간 데이터셋 검색 실패 → 키워드 공백일 경우 빈 결과 반환")
        void searchRealTimeDataSetsReturnsEmptyWhenKeywordBlank() {
            // when
            List<RecentMinimalDataResponse> res = service.searchRealTimeDataSets(" ", 3);

            // then
            assertThat(res).isEmpty();
        }
    }
}
