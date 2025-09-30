package com.dataracy.modules.dataset.adapter.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.dataracy.modules.common.logging.ElasticLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;
import com.dataracy.modules.dataset.domain.exception.DataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class SearchRealTimeDataSetsEsAdapterTest {

    @Mock
    private ElasticsearchClient elasticsearchClient;
    
    @Mock
    private ElasticLogger elasticLogger;

    private SearchRealTimeDataSetsEsAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SearchRealTimeDataSetsEsAdapter(elasticsearchClient);
    }

    @Test
    @DisplayName("실시간 데이터셋 검색 성공 시 결과 반환")
    void searchRealTimeDataSetsSuccess() throws IOException {
        // given
        String keyword = "test data";
        int size = 10;
        Instant startTime = Instant.now();
        
        SearchResponse<DataSearchDocument> mockResponse = mock(SearchResponse.class);
        given(mockResponse.hits()).willReturn(mock());
        given(mockResponse.hits().hits()).willReturn(List.of());
        
        given(elasticsearchClient.search(any(Function.class), eq(DataSearchDocument.class)))
            .willReturn(mockResponse);
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);
            given(elasticLogger.logQueryStart(any(String.class), any(String.class))).willReturn(startTime);

            // when
            List<RecentMinimalDataResponse> result = adapter.searchRealTimeDataSets(keyword, size);

            // then
            assertThat(result).isEmpty();
            
            then(elasticsearchClient).should().search(any(Function.class), eq(DataSearchDocument.class));
            then(elasticLogger).should().logQueryStart("data_index", "데이터셋 검색어 자동완성 실시간 검색 시작: keyword=test data, size=10");
            then(elasticLogger).should().logQueryEnd(eq("data_index"), eq("데이터셋 검색어 자동완성 실시간 검색 종료: keyword=test data, size=10"), eq(startTime));
        }
    }

    @Test
    @DisplayName("빈 키워드로 검색 시 빈 결과 반환")
    void searchRealTimeDataSetsWithEmptyKeyword() throws IOException {
        // given
        String keyword = "";
        int size = 5;
        Instant startTime = Instant.now();
        
        SearchResponse<DataSearchDocument> mockResponse = mock(SearchResponse.class);
        given(mockResponse.hits()).willReturn(mock());
        given(mockResponse.hits().hits()).willReturn(List.of());
        
        given(elasticsearchClient.search(any(Function.class), eq(DataSearchDocument.class)))
            .willReturn(mockResponse);
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);
            given(elasticLogger.logQueryStart(any(String.class), any(String.class))).willReturn(startTime);

            // when
            List<RecentMinimalDataResponse> result = adapter.searchRealTimeDataSets(keyword, size);

            // then
            assertThat(result).isEmpty();
            
            then(elasticsearchClient).should().search(any(Function.class), eq(DataSearchDocument.class));
            then(elasticLogger).should().logQueryStart("data_index", "데이터셋 검색어 자동완성 실시간 검색 시작: keyword=, size=5");
            then(elasticLogger).should().logQueryEnd(eq("data_index"), eq("데이터셋 검색어 자동완성 실시간 검색 종료: keyword=, size=5"), eq(startTime));
        }
    }

    @Test
    @DisplayName("IOException 발생 시 DataException으로 변환")
    void searchRealTimeDataSetsWithIOException() throws IOException {
        // given
        String keyword = "test";
        int size = 10;
        IOException ioException = new IOException("Elasticsearch connection failed");
        
        willThrow(ioException).given(elasticsearchClient).search(any(Function.class), eq(DataSearchDocument.class));
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

            // when & then
            assertThatThrownBy(() -> adapter.searchRealTimeDataSets(keyword, size))
                .isInstanceOf(DataException.class);
            
            then(elasticLogger).should().logError(eq("data_index"), eq("데이터셋 실시간 검색 실패: keyword=test, size=10"), any(IOException.class));
        }
    }

    @Test
    @DisplayName("큰 사이즈로 검색 시 정상 동작")
    void searchRealTimeDataSetsWithLargeSize() throws IOException {
        // given
        String keyword = "big data";
        int size = 1000;
        Instant startTime = Instant.now();
        
        SearchResponse<DataSearchDocument> mockResponse = mock(SearchResponse.class);
        given(mockResponse.hits()).willReturn(mock());
        given(mockResponse.hits().hits()).willReturn(List.of());
        
        given(elasticsearchClient.search(any(Function.class), eq(DataSearchDocument.class)))
            .willReturn(mockResponse);
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);
            given(elasticLogger.logQueryStart(any(String.class), any(String.class))).willReturn(startTime);

            // when
            List<RecentMinimalDataResponse> result = adapter.searchRealTimeDataSets(keyword, size);

            // then
            assertThat(result).isEmpty();
            
            then(elasticsearchClient).should().search(any(Function.class), eq(DataSearchDocument.class));
            then(elasticLogger).should().logQueryStart("data_index", "데이터셋 검색어 자동완성 실시간 검색 시작: keyword=big data, size=1000");
            then(elasticLogger).should().logQueryEnd(eq("data_index"), eq("데이터셋 검색어 자동완성 실시간 검색 종료: keyword=big data, size=1000"), eq(startTime));
        }
    }
}
