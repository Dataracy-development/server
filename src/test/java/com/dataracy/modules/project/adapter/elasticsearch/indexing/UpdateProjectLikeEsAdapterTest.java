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
class UpdateProjectLikeEsAdapterTest {

  // Test constants
  private static final Long PROJECT_ID = 1L;
  private static final Long LARGE_NUMBER = 1L;
  private static final Long EIGHT_EIGHTY_EIGHT = 888L;

  @Mock private ElasticsearchClient elasticsearchClient;

  @Mock private ElasticLogger elasticLogger;

  private UpdateProjectLikeEsAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new UpdateProjectLikeEsAdapter(elasticsearchClient);
  }

  @Test
  @DisplayName("프로젝트 좋아요 증가 성공 시 정상 동작")
  void increaseLikeCountSuccess() throws IOException {
    // given
    Long projectId = 1L;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.increaseLikeCount(projectId);

      // then
      then(elasticsearchClient)
          .should()
          .update(any(Function.class), eq(ProjectSearchDocument.class));
      then(elasticLogger)
          .should()
          .logUpdate("project_index", "1", "프로젝트 likeCount 증분 업데이트 완료 - projectId=1");
    }
  }

  @Test
  @DisplayName("프로젝트 좋아요 감소 성공 시 정상 동작")
  void decreaseLikeCountSuccess() throws IOException {
    // given
    Long projectId = PROJECT_ID;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.decreaseLikeCount(projectId);

      // then
      then(elasticsearchClient)
          .should()
          .update(any(Function.class), eq(ProjectSearchDocument.class));
      then(elasticLogger)
          .should()
          .logUpdate("project_index", "1", "프로젝트 likeCount 감분 업데이트 완료 - projectId=1");
    }
  }

  @Test
  @DisplayName("좋아요 증가 시 IOException 발생하면 EsUpdateException으로 변환")
  void increaseLikeCountWithIOException() throws IOException {
    // given
    Long projectId = 1L;
    IOException ioException = new IOException("Elasticsearch connection failed");
    willThrow(ioException)
        .given(elasticsearchClient)
        .update(any(Function.class), eq(ProjectSearchDocument.class));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when & then
      EsUpdateException exception =
          catchThrowableOfType(() -> adapter.increaseLikeCount(projectId), EsUpdateException.class);
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
              eq("project_index"),
              eq("프로젝트 likeCount 증분 업데이트 실패 - projectId=1"),
              any(IOException.class));
    }
  }

  @Test
  @DisplayName("좋아요 감소 시 IOException 발생하면 EsUpdateException으로 변환")
  void decreaseLikeCountWithIOException() throws IOException {
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
          catchThrowableOfType(() -> adapter.decreaseLikeCount(projectId), EsUpdateException.class);
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
              eq("project_index"),
              eq("프로젝트 likeCount 감분 업데이트 실패 - projectId=1"),
              any(IOException.class));
    }
  }

  @Test
  @DisplayName("큰 프로젝트 ID로 좋아요 증가 성공")
  void increaseLikeCountWithLargeProjectId() throws IOException {
    // given
    Long projectId = LARGE_NUMBER;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.increaseLikeCount(projectId);

      // then
      then(elasticsearchClient)
          .should()
          .update(any(Function.class), eq(ProjectSearchDocument.class));
      then(elasticLogger)
          .should()
          .logUpdate("project_index", "1", "프로젝트 likeCount 증분 업데이트 완료 - projectId=1");
    }
  }

  @Test
  @DisplayName("큰 프로젝트 ID로 좋아요 감소 성공")
  void decreaseLikeCountWithLargeProjectId() throws IOException {
    // given
    Long projectId = EIGHT_EIGHTY_EIGHT;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.decreaseLikeCount(projectId);

      // then
      then(elasticsearchClient)
          .should()
          .update(any(Function.class), eq(ProjectSearchDocument.class));
      then(elasticLogger)
          .should()
          .logUpdate("project_index", "888", "프로젝트 likeCount 감분 업데이트 완료 - projectId=888");
    }
  }
}
