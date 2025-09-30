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
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class UpdateProjectLikeEsAdapterTest {

    @Mock
    private ElasticsearchClient elasticsearchClient;
    
    @Mock
    private ElasticLogger elasticLogger;

    private UpdateProjectLikeEsAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UpdateProjectLikeEsAdapter(elasticsearchClient);
    }

    @Test
    @DisplayName("프로젝트 좋아요 증가 성공 시 정상 동작")
    void increaseLikeCountSuccess() throws IOException {
        // given
        Long projectId = 123L;
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

            // when
            adapter.increaseLikeCount(projectId);

            // then
            then(elasticsearchClient).should().update(any(Function.class), eq(ProjectSearchDocument.class));
            then(elasticLogger).should().logUpdate("project_index", "123", "프로젝트 likeCount 증분 업데이트 완료 - projectId=123");
        }
    }

    @Test
    @DisplayName("프로젝트 좋아요 감소 성공 시 정상 동작")
    void decreaseLikeCountSuccess() throws IOException {
        // given
        Long projectId = 456L;
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

            // when
            adapter.decreaseLikeCount(projectId);

            // then
            then(elasticsearchClient).should().update(any(Function.class), eq(ProjectSearchDocument.class));
            then(elasticLogger).should().logUpdate("project_index", "456", "프로젝트 likeCount 감분 업데이트 완료 - projectId=456");
        }
    }

    @Test
    @DisplayName("좋아요 증가 시 IOException 발생하면 EsUpdateException으로 변환")
    void increaseLikeCountWithIOException() throws IOException {
        // given
        Long projectId = 123L;
        IOException ioException = new IOException("Elasticsearch connection failed");
        willThrow(ioException).given(elasticsearchClient).update(any(Function.class), eq(ProjectSearchDocument.class));
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

            // when & then
            assertThatThrownBy(() -> adapter.increaseLikeCount(projectId))
                .isInstanceOf(EsUpdateException.class)
                .hasMessage("ES update failed: projectId=123")
                .hasCause(ioException);
            
            then(elasticLogger).should().logError(eq("project_index"), eq("프로젝트 likeCount 증분 업데이트 실패 - projectId=123"), any(IOException.class));
        }
    }

    @Test
    @DisplayName("좋아요 감소 시 IOException 발생하면 EsUpdateException으로 변환")
    void decreaseLikeCountWithIOException() throws IOException {
        // given
        Long projectId = 456L;
        IOException ioException = new IOException("Network error");
        willThrow(ioException).given(elasticsearchClient).update(any(Function.class), eq(ProjectSearchDocument.class));
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

            // when & then
            assertThatThrownBy(() -> adapter.decreaseLikeCount(projectId))
                .isInstanceOf(EsUpdateException.class)
                .hasMessage("ES update failed: projectId=456")
                .hasCause(ioException);
            
            then(elasticLogger).should().logError(eq("project_index"), eq("프로젝트 likeCount 감분 업데이트 실패 - projectId=456"), any(IOException.class));
        }
    }

    @Test
    @DisplayName("큰 프로젝트 ID로 좋아요 증가 성공")
    void increaseLikeCountWithLargeProjectId() throws IOException {
        // given
        Long projectId = 999999L;
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

            // when
            adapter.increaseLikeCount(projectId);

            // then
            then(elasticsearchClient).should().update(any(Function.class), eq(ProjectSearchDocument.class));
            then(elasticLogger).should().logUpdate("project_index", "999999", "프로젝트 likeCount 증분 업데이트 완료 - projectId=999999");
        }
    }

    @Test
    @DisplayName("큰 프로젝트 ID로 좋아요 감소 성공")
    void decreaseLikeCountWithLargeProjectId() throws IOException {
        // given
        Long projectId = 888888L;
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

            // when
            adapter.decreaseLikeCount(projectId);

            // then
            then(elasticsearchClient).should().update(any(Function.class), eq(ProjectSearchDocument.class));
            then(elasticLogger).should().logUpdate("project_index", "888888", "프로젝트 likeCount 감분 업데이트 완료 - projectId=888888");
        }
    }
}
