package com.dataracy.modules.project.adapter.elasticsearch.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.common.exception.EsUpdateException;
import com.dataracy.modules.common.logging.ElasticLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class dUpdateProjectViewEsAdapterTest {

    @Mock
    private ElasticsearchClient elasticsearchClient;
    
    @Mock
    private ElasticLogger elasticLogger;

    private UpdateProjectViewEsAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UpdateProjectViewEsAdapter(elasticsearchClient);
    }

    @Test
    @DisplayName("프로젝트 조회수 증가 성공 시 정상 동작")
    void increaseViewCountSuccess() throws IOException {
        // given
        Long projectId = 123L;
        Long increment = 5L;
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

            // when
            adapter.increaseViewCount(projectId, increment);

            // then
            then(elasticsearchClient).should().update(any(Function.class), eq(ProjectSearchDocument.class));
            then(elasticLogger).should().logUpdate("project_index", "123", "프로젝트 viewCount 증분 업데이트 완료 - projectId=123");
        }
    }

    @Test
    @DisplayName("조회수 증가 시 IOException 발생하면 EsUpdateException으로 변환")
    void increaseViewCountWithIOException() throws IOException {
        // given
        Long projectId = 123L;
        Long increment = 1L;
        IOException ioException = new IOException("Elasticsearch connection failed");
        willThrow(ioException).given(elasticsearchClient).update(any(Function.class), eq(ProjectSearchDocument.class));
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

            // when & then
            EsUpdateException exception = catchThrowableOfType(
                    () -> adapter.increaseViewCount(projectId, increment),
                    EsUpdateException.class
            );
            assertAll(
                    () -> org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(EsUpdateException.class),
                    () -> org.assertj.core.api.Assertions.assertThat(exception).hasMessage("ES update failed: projectId=123"),
                    () -> org.assertj.core.api.Assertions.assertThat(exception).hasCause(ioException)
            );
            
            then(elasticLogger).should().logError(eq("project_index"), eq("프로젝트 viewCount 증분 업데이트 실패 - projectId=123"), any(IOException.class));
        }
    }

    @Test
    @DisplayName("배치 조회수 증가 성공 시 정상 동작")
    void increaseViewCountBatchSuccess() throws IOException {
        // given
        Map<Long, Long> viewCountUpdates = new HashMap<>();
        viewCountUpdates.put(1L, 3L);
        viewCountUpdates.put(2L, 5L);
        viewCountUpdates.put(3L, 2L);
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

            // when
            adapter.increaseViewCountBatch(viewCountUpdates);

            // then
            then(elasticsearchClient).should(times(3)).update(any(Function.class), eq(ProjectSearchDocument.class));
            then(elasticLogger).should().logUpdate("project_index", "배치 처리", "프로젝트 viewCount 배치 업데이트 완료. 처리된 프로젝트 수: 3");
        }
    }

    @Test
    @DisplayName("빈 배치 조회수 증가 시 아무것도 하지 않음")
    void increaseViewCountBatchWithEmptyMap() {
        // given
        Map<Long, Long> emptyUpdates = new HashMap<>();
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

            // when
            adapter.increaseViewCountBatch(emptyUpdates);

            // then
            then(elasticsearchClient).shouldHaveNoInteractions();
            then(elasticLogger).shouldHaveNoInteractions();
        }
    }

    @Test
    @DisplayName("큰 증분값으로 조회수 증가 성공")
    void increaseViewCountWithLargeIncrement() throws IOException {
        // given
        Long projectId = 456L;
        Long increment = 1000L;
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

            // when
            adapter.increaseViewCount(projectId, increment);

            // then
            then(elasticsearchClient).should().update(any(Function.class), eq(ProjectSearchDocument.class));
            then(elasticLogger).should().logUpdate("project_index", "456", "프로젝트 viewCount 증분 업데이트 완료 - projectId=456");
        }
    }

    @Test
    @DisplayName("배치 처리 중 일부 실패해도 계속 진행")
    void increaseViewCountBatchWithPartialFailure() throws IOException {
        // given
        Map<Long, Long> viewCountUpdates = new HashMap<>();
        viewCountUpdates.put(1L, 3L);
        viewCountUpdates.put(2L, 5L);
        
        IOException ioException = new IOException("Partial failure");
        willThrow(ioException).given(elasticsearchClient).update(any(Function.class), eq(ProjectSearchDocument.class));
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

            // when
            adapter.increaseViewCountBatch(viewCountUpdates);

            // then
            // 개별 실패가 전체에 영향을 주지 않으므로 계속 진행되어야 함
            then(elasticsearchClient).should(times(2)).update(any(Function.class), eq(ProjectSearchDocument.class));
            // increaseViewCount에서 IOException 로깅 (2번)
            then(elasticLogger).should().logError(eq("project_index"), eq("프로젝트 viewCount 증분 업데이트 실패 - projectId=1"), any(IOException.class));
            then(elasticLogger).should().logError(eq("project_index"), eq("프로젝트 viewCount 증분 업데이트 실패 - projectId=2"), any(IOException.class));
            // increaseViewCountBatch에서 EsUpdateException 로깅 (2번)
            then(elasticLogger).should().logError(eq("project_index"), eq("배치 처리 중 개별 프로젝트 업데이트 실패 - projectId=1"), any(Exception.class));
            then(elasticLogger).should().logError(eq("project_index"), eq("배치 처리 중 개별 프로젝트 업데이트 실패 - projectId=2"), any(Exception.class));
            then(elasticLogger).should().logUpdate("project_index", "배치 처리", "프로젝트 viewCount 배치 업데이트 완료. 처리된 프로젝트 수: 2");
        }
    }
}
