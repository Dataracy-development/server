package com.dataracy.modules.project.adapter.elasticsearch.indexing;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;

import java.io.IOException;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dataracy.modules.common.exception.EsUpdateException;
import com.dataracy.modules.common.logging.ElasticLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

@ExtendWith(MockitoExtension.class)
class SoftDeleteProjectEsAdapterTest {

  // Test constants
  private static final Long PROJECT_ID = 1L;

  @Mock private ElasticsearchClient elasticsearchClient;

  @Mock private ElasticLogger elasticLogger;

  private SoftDeleteProjectEsAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new SoftDeleteProjectEsAdapter(elasticsearchClient);
  }

  @Test
  @DisplayName("프로젝트 소프트 삭제 성공 시 정상 동작")
  void deleteProjectSuccess() throws IOException {
    // given
    Long projectId = 1L;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.deleteProject(projectId);

      // then
      then(elasticsearchClient)
          .should()
          .update(any(Function.class), eq(ProjectSearchDocument.class));
      then(elasticLogger)
          .should()
          .logUpdate("project_index", "1", "프로젝트 Soft Delete완료: projectId=1");
    }
  }

  @Test
  @DisplayName("프로젝트 복원 성공 시 정상 동작")
  void restoreProjectSuccess() throws IOException {
    // given
    Long projectId = PROJECT_ID;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.restoreProject(projectId);

      // then
      then(elasticsearchClient)
          .should()
          .update(any(Function.class), eq(ProjectSearchDocument.class));
      then(elasticLogger).should().logUpdate("project_index", "1", "프로젝트 삭제 복원완료: projectId=1");
    }
  }

  @Test
  @DisplayName("소프트 삭제 시 IOException 발생하면 EsUpdateException으로 변환")
  void deleteProjectWithIOException() throws IOException {
    // given
    Long projectId = 1L;
    IOException ioException = new IOException("Connection failed");
    willThrow(ioException)
        .given(elasticsearchClient)
        .update(any(Function.class), eq(ProjectSearchDocument.class));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when & then
      EsUpdateException exception =
          catchThrowableOfType(() -> adapter.deleteProject(projectId), EsUpdateException.class);
      assertAll(
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .isInstanceOf(EsUpdateException.class),
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .hasMessage("ES update failed: projectId=1"),
          () -> org.assertj.core.api.Assertions.assertThat(exception).hasCause(ioException));

      then(elasticLogger)
          .should()
          .logError(
              eq("project_index"), eq("프로젝트 Soft Delete실패: projectId=1"), any(IOException.class));
    }
  }

  @Test
  @DisplayName("복원 시 IOException 발생하면 EsUpdateException으로 변환")
  void restoreProjectWithIOException() throws IOException {
    // given
    Long projectId = PROJECT_ID;
    IOException ioException = new IOException("Network error");
    willThrow(ioException)
        .given(elasticsearchClient)
        .update(any(Function.class), eq(ProjectSearchDocument.class));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when & then
      EsUpdateException exception =
          catchThrowableOfType(() -> adapter.restoreProject(projectId), EsUpdateException.class);
      assertAll(
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .isInstanceOf(EsUpdateException.class),
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .hasMessage("ES update failed: projectId=1"),
          () -> org.assertj.core.api.Assertions.assertThat(exception).hasCause(ioException));

      then(elasticLogger)
          .should()
          .logError(eq("project_index"), eq("프로젝트 삭제 복원실패: projectId=1"), any(IOException.class));
    }
  }
}
