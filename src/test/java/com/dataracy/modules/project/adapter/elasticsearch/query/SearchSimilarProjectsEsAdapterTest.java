package com.dataracy.modules.project.adapter.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.common.logging.ElasticLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.dto.response.search.SimilarProjectResponse;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
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
class SearchSimilarProjectsEsAdapterTest {

    @Mock
    private ElasticsearchClient elasticsearchClient;
    
    @Mock
    private ElasticLogger elasticLogger;

    private SearchSimilarProjectsEsAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SearchSimilarProjectsEsAdapter(elasticsearchClient);
    }

    @Test
    @DisplayName("유사 프로젝트 검색 시 IOException 발생하면 ProjectException으로 변환")
    void searchSimilarProjectsWithIOException() throws IOException {
        // given
        Project project = createTestProject();
        int size = 10;
        IOException ioException = new IOException("Elasticsearch connection failed");
        
        willThrow(ioException).given(elasticsearchClient).search(any(Function.class), eq(ProjectSearchDocument.class));
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

            // when & then
            assertThatThrownBy(() -> adapter.searchSimilarProjects(project, size))
                .isInstanceOf(ProjectException.class);
            
            then(elasticLogger).should().logError(eq("project_index"), eq("유사 프로젝트 조회 실패: projectId=123, size=10"), any(IOException.class));
        }
    }

    private Project createTestProject() {
        return Project.builder()
            .id(123L)
            .title("Test Project")
            .content("Test project content")
            .topicId(1L)
            .analysisPurposeId(2L)
            .build();
    }
}
