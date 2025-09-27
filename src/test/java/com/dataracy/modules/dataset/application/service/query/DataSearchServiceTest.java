package com.dataracy.modules.dataset.application.service.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
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
import com.dataracy.modules.dataset.domain.enums.DataSortType;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSearchServiceTest {

    @Mock
    private FilteredDataDtoMapper filteredDataDtoMapper;

    @Mock
    private FindDataPort findDataPort;

    @Mock
    private FindDataLabelMapUseCase findDataLabelMapUseCase;

    @Mock
    private SearchSimilarDataSetsPort searchSimilarDataSetsPort;

    @Mock
    private SearchFilteredDataSetsPort searchFilteredDataSetsPort;

    @Mock
    private SearchRealTimeDataSetsPort searchRealTimeDataSetsPort;

    @InjectMocks
    private DataSearchService service;

    private MockedStatic<LoggerFactory> loggerFactoryMock;
    private com.dataracy.modules.common.logging.ServiceLogger loggerService;

    @BeforeEach
    void setUp() {
        loggerFactoryMock = mockStatic(LoggerFactory.class);
        loggerService = mock(com.dataracy.modules.common.logging.ServiceLogger.class);
        loggerFactoryMock.when(() -> LoggerFactory.service()).thenReturn(loggerService);
        lenient().when(loggerService.logStart(anyString(), anyString())).thenReturn(Instant.now());
        lenient().doNothing().when(loggerService).logSuccess(anyString(), anyString(), any(Instant.class));
        lenient().doNothing().when(loggerService).logWarning(anyString(), anyString());
    }

    @AfterEach
    void tearDown() {
        if (loggerFactoryMock != null) {
            loggerFactoryMock.close();
        }
    }

    @Nested
    @DisplayName("searchSimilarDataSets 메서드 테스트")
    class SearchSimilarDataSetsTest {

        @Test
        @DisplayName("유사 데이터셋 검색 성공 및 로깅 검증")
        void searchSimilarDataSetsSuccess() {
            // given
            Long dataId = 100L;
            int size = 5;
            Data data = createTestData(dataId);
            List<SimilarDataResponse> expectedResponses = List.of(
                    mock(SimilarDataResponse.class),
                    mock(SimilarDataResponse.class)
            );

            given(findDataPort.findDataById(dataId)).willReturn(Optional.of(data));
            given(searchSimilarDataSetsPort.searchSimilarDataSets(data, size)).willReturn(expectedResponses);

            // when
            List<SimilarDataResponse> result = service.searchSimilarDataSets(dataId, size);

            // then
            assertThat(result).isEqualTo(expectedResponses);

            // 포트 호출 검증
            then(findDataPort).should().findDataById(dataId);
            then(searchSimilarDataSetsPort).should().searchSimilarDataSets(data, size);

            // 로깅 검증
            then(loggerService).should().logStart(eq("SearchSimilarDataSetsUseCase"),
                    contains("지정한 데이터 ID를 기준으로 유사한 데이터셋 목록을 조회 서비스 시작 dataId=" + dataId));
            then(loggerService).should().logSuccess(eq("SearchSimilarDataSetsUseCase"),
                    contains("지정한 데이터 ID를 기준으로 유사한 데이터셋 목록을 조회 서비스 종료 dataId=" + dataId), any(Instant.class));
        }

        @Test
        @DisplayName("존재하지 않는 데이터 ID로 검색 시 예외 발생 및 로깅 검증")
        void searchSimilarDataSetsWithNonExistentData() {
            // given
            Long dataId = 999L;
            int size = 5;

            given(findDataPort.findDataById(dataId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> service.searchSimilarDataSets(dataId, size))
                    .isInstanceOf(DataException.class)
                    .satisfies(exception -> {
                        DataException dataException = (DataException) exception;
                        assertThat(dataException.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
                    });

            // 포트 호출 검증
            then(findDataPort).should().findDataById(dataId);
            then(searchSimilarDataSetsPort).should(never()).searchSimilarDataSets(any(Data.class), anyInt());

            // 로깅 검증
            then(loggerService).should().logStart(eq("SearchSimilarDataSetsUseCase"),
                    contains("지정한 데이터 ID를 기준으로 유사한 데이터셋 목록을 조회 서비스 시작 dataId=" + dataId));
            then(loggerService).should().logWarning(eq("SearchSimilarDataSetsUseCase"),
                    contains("해당 데이터셋이 존재하지 않습니다. dataId=" + dataId));
            then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
        }

        @Test
        @DisplayName("빈 유사 데이터셋 결과에 대한 처리")
        void searchSimilarDataSetsWithEmptyResult() {
            // given
            Long dataId = 100L;
            int size = 5;
            Data data = createTestData(dataId);

            given(findDataPort.findDataById(dataId)).willReturn(Optional.of(data));
            given(searchSimilarDataSetsPort.searchSimilarDataSets(data, size)).willReturn(List.of());

            // when
            List<SimilarDataResponse> result = service.searchSimilarDataSets(dataId, size);

            // then
            assertThat(result).isEmpty();

            // 포트 호출 검증
            then(findDataPort).should().findDataById(dataId);
            then(searchSimilarDataSetsPort).should().searchSimilarDataSets(data, size);

            // 로깅 검증
            then(loggerService).should().logStart(eq("SearchSimilarDataSetsUseCase"),
                    contains("지정한 데이터 ID를 기준으로 유사한 데이터셋 목록을 조회 서비스 시작 dataId=" + dataId));
            then(loggerService).should().logSuccess(eq("SearchSimilarDataSetsUseCase"),
                    contains("지정한 데이터 ID를 기준으로 유사한 데이터셋 목록을 조회 서비스 종료 dataId=" + dataId), any(Instant.class));
        }
    }

    @Nested
    @DisplayName("searchFilteredDataSets 메서드 테스트")
    class SearchFilteredDataSetsTest {

        @Test
        @DisplayName("필터링된 데이터셋 검색 성공 및 로깅 검증")
        void searchFilteredDataSetsSuccess() {
            // given
            FilteringDataRequest request = new FilteringDataRequest("AI", "LATEST", 1L, 2L, 3L, 2023);
            Pageable pageable = PageRequest.of(0, 10);

            Data data1 = createTestData(1L);
            Data data2 = createTestData(2L);
            DataWithProjectCountDto wrapper1 = new DataWithProjectCountDto(data1, 5L);
            DataWithProjectCountDto wrapper2 = new DataWithProjectCountDto(data2, 3L);
            Page<DataWithProjectCountDto> dataPage = new PageImpl<>(List.of(wrapper1, wrapper2), pageable, 2);

            DataLabelMapResponse labelResponse = mock(DataLabelMapResponse.class);
            FilteredDataResponse response1 = mock(FilteredDataResponse.class);
            FilteredDataResponse response2 = mock(FilteredDataResponse.class);

            given(searchFilteredDataSetsPort.searchByFilters(request, pageable, DataSortType.LATEST)).willReturn(dataPage);
            given(findDataLabelMapUseCase.labelMapping(List.of(wrapper1, wrapper2))).willReturn(labelResponse);
            given(labelResponse.usernameMap()).willReturn(Map.of(100L, "user1"));
            given(labelResponse.userProfileUrlMap()).willReturn(Map.of(100L, "profile1.jpg"));
            given(labelResponse.topicLabelMap()).willReturn(Map.of(1L, "AI"));
            given(labelResponse.dataSourceLabelMap()).willReturn(Map.of(2L, "Public"));
            given(labelResponse.dataTypeLabelMap()).willReturn(Map.of(3L, "CSV"));

            given(filteredDataDtoMapper.toResponseDto(eq(data1), anyString(), anyString(), anyString(), anyString(), anyString(), eq(5L)))
                    .willReturn(response1);
            given(filteredDataDtoMapper.toResponseDto(eq(data2), anyString(), anyString(), anyString(), anyString(), anyString(), eq(3L)))
                    .willReturn(response2);

            // when
            Page<FilteredDataResponse> result = service.searchFilteredDataSets(request, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).containsExactly(response1, response2);

            // 포트 호출 검증
            then(searchFilteredDataSetsPort).should().searchByFilters(request, pageable, DataSortType.LATEST);
            then(findDataLabelMapUseCase).should().labelMapping(List.of(wrapper1, wrapper2));

            // 로깅 검증
            then(loggerService).should().logStart(eq("SearchFilteredDataSetsUseCase"),
                    contains("필터링된 데이터셋 목록 조회 서비스 시작 keyword=" + request.keyword()));
            then(loggerService).should().logSuccess(eq("SearchFilteredDataSetsUseCase"),
                    contains("필터링된 데이터셋 목록 조회 서비스 종료 keyword=" + request.keyword()), any(Instant.class));
        }

        @Test
        @DisplayName("빈 결과에 대한 필터링된 데이터셋 검색")
        void searchFilteredDataSetsWithEmptyResult() {
            // given
            FilteringDataRequest request = new FilteringDataRequest("NonExistent", "LATEST", 1L, 2L, 3L, 2023);
            Pageable pageable = PageRequest.of(0, 10);
            Page<DataWithProjectCountDto> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            given(searchFilteredDataSetsPort.searchByFilters(request, pageable, DataSortType.LATEST)).willReturn(emptyPage);
            given(findDataLabelMapUseCase.labelMapping(List.of())).willReturn(mock(DataLabelMapResponse.class));

            // when
            Page<FilteredDataResponse> result = service.searchFilteredDataSets(request, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);

            // 포트 호출 검증
            then(searchFilteredDataSetsPort).should().searchByFilters(request, pageable, DataSortType.LATEST);
            then(findDataLabelMapUseCase).should().labelMapping(List.of());

            // 로깅 검증
            then(loggerService).should().logStart(eq("SearchFilteredDataSetsUseCase"),
                    contains("필터링된 데이터셋 목록 조회 서비스 시작 keyword=" + request.keyword()));
            then(loggerService).should().logSuccess(eq("SearchFilteredDataSetsUseCase"),
                    contains("필터링된 데이터셋 목록 조회 서비스 종료 keyword=" + request.keyword()), any(Instant.class));
        }
    }

    @Nested
    @DisplayName("searchRealTimeDataSets 메서드 테스트")
    class SearchRealTimeDataSetsTest {

        @Test
        @DisplayName("실시간 데이터셋 검색 성공 및 로깅 검증")
        void searchRealTimeDataSetsSuccess() {
            // given
            String keyword = "machine learning";
            int size = 5;
            List<RecentMinimalDataResponse> expectedResponses = List.of(
                    mock(RecentMinimalDataResponse.class),
                    mock(RecentMinimalDataResponse.class)
            );

            given(searchRealTimeDataSetsPort.searchRealTimeDataSets(keyword, size)).willReturn(expectedResponses);

            // when
            List<RecentMinimalDataResponse> result = service.searchRealTimeDataSets(keyword, size);

            // then
            assertThat(result).isEqualTo(expectedResponses);

            // 포트 호출 검증
            then(searchRealTimeDataSetsPort).should().searchRealTimeDataSets(keyword, size);

            // 로깅 검증
            then(loggerService).should().logStart(eq("SearchRealTimeDataSetsUseCase"),
                    contains("자동완성을 위한 실시간 데이터셋 목록 조회 서비스 시작 keyword=" + keyword));
            then(loggerService).should().logSuccess(eq("SearchRealTimeDataSetsUseCase"),
                    contains("자동완성을 위한 실시간 데이터셋 목록 조회 서비스 종료 keyword=" + keyword), any(Instant.class));
        }

        @Test
        @DisplayName("null 키워드에 대한 빈 결과 반환")
        void searchRealTimeDataSetsWithNullKeyword() {
            // given
            String keyword = null;
            int size = 5;

            // when
            List<RecentMinimalDataResponse> result = service.searchRealTimeDataSets(keyword, size);

            // then
            assertThat(result).isEmpty();

            // 포트 호출되지 않음 검증
            then(searchRealTimeDataSetsPort).should(never()).searchRealTimeDataSets(anyString(), anyInt());

            // 로깅 검증 - null 키워드는 logStart만 호출되고 logSuccess는 호출되지 않음
            then(loggerService).should().logStart(eq("SearchRealTimeDataSetsUseCase"),
                    contains("자동완성을 위한 실시간 데이터셋 목록 조회 서비스 시작 keyword=" + keyword));
            then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
        }

        @Test
        @DisplayName("빈 문자열 키워드에 대한 빈 결과 반환")
        void searchRealTimeDataSetsWithEmptyKeyword() {
            // given
            String keyword = "   ";
            int size = 5;

            // when
            List<RecentMinimalDataResponse> result = service.searchRealTimeDataSets(keyword, size);

            // then
            assertThat(result).isEmpty();

            // 포트 호출되지 않음 검증
            then(searchRealTimeDataSetsPort).should(never()).searchRealTimeDataSets(anyString(), anyInt());

            // 로깅 검증 - 빈 문자열 키워드는 logStart만 호출되고 logSuccess는 호출되지 않음
            then(loggerService).should().logStart(eq("SearchRealTimeDataSetsUseCase"),
                    contains("자동완성을 위한 실시간 데이터셋 목록 조회 서비스 시작 keyword=" + keyword));
            then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
        }

        @Test
        @DisplayName("빈 결과에 대한 처리")
        void searchRealTimeDataSetsWithEmptyResult() {
            // given
            String keyword = "NonExistent";
            int size = 5;

            given(searchRealTimeDataSetsPort.searchRealTimeDataSets(keyword, size)).willReturn(List.of());

            // when
            List<RecentMinimalDataResponse> result = service.searchRealTimeDataSets(keyword, size);

            // then
            assertThat(result).isEmpty();

            // 포트 호출 검증
            then(searchRealTimeDataSetsPort).should().searchRealTimeDataSets(keyword, size);

            // 로깅 검증
            then(loggerService).should().logStart(eq("SearchRealTimeDataSetsUseCase"),
                    contains("자동완성을 위한 실시간 데이터셋 목록 조회 서비스 시작 keyword=" + keyword));
            then(loggerService).should().logSuccess(eq("SearchRealTimeDataSetsUseCase"),
                    contains("자동완성을 위한 실시간 데이터셋 목록 조회 서비스 종료 keyword=" + keyword), any(Instant.class));
        }
    }

    private Data createTestData(Long id) {
        return Data.builder()
                .id(id)
                .title("Test Data")
                .userId(100L)
                .dataSourceId(2L)
                .dataTypeId(3L)
                .topicId(1L)
                .description("description")
                .dataFileUrl("http://example.com/file.csv")
                .downloadCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }
}