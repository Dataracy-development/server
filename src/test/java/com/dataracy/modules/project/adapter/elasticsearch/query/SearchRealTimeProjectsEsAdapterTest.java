package com.dataracy.modules.project.adapter.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.common.logging.ElasticLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;
import com.dataracy.modules.project.domain.exception.ProjectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class SearchRealTimeProjectsEsAdapterTest {

    @Mock
    private ElasticsearchClient elasticsearchClient;
    
    @Mock
    private ElasticLogger elasticLogger;

    private SearchRealTimeProjectsEsAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SearchRealTimeProjectsEsAdapter(elasticsearchClient);
    }

    @Test
    @DisplayName("실시간 프로젝트 검색 시 IOException 발생하면 ProjectException으로 변환")
    void searchByKeywordWithIOException() throws IOException {
        // given
        String keyword = "test";
        int size = 5;
        IOException ioException = new IOException("Elasticsearch connection failed");
        
        willThrow(ioException).given(elasticsearchClient).search(any(Function.class), eq(ProjectSearchDocument.class));
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

            // when & then
            ProjectException exception = catchThrowableOfType(
                    () -> adapter.searchByKeyword(keyword, size),
                    ProjectException.class
            );
            assertAll(
                    () -> org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(ProjectException.class)
            );
            
            then(elasticLogger).should().logError(eq("project_index"), eq("프로젝트 실시간 검색 실패: keyword=test, size=5"), any(IOException.class));
        }
    }
}
