/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
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
class UpdateProjectCommentEsAdapterTest {

  @Mock private ElasticsearchClient elasticsearchClient;

  @Mock private ElasticLogger elasticLogger;

  private UpdateProjectCommentEsAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new UpdateProjectCommentEsAdapter(elasticsearchClient);
  }

  @Test
  @DisplayName("프로젝트 댓글 수 증가 성공 시 정상 동작")
  void increaseCommentCountSuccess() throws IOException {
    // given
    Long projectId = 123L;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.increaseCommentCount(projectId);

      // then
      then(elasticsearchClient)
          .should()
          .update(any(Function.class), eq(ProjectSearchDocument.class));
      then(elasticLogger)
          .should()
          .logUpdate("project_index", "123", "프로젝트 commentCount 증분 업데이트 완료 - projectId=123");
    }
  }

  @Test
  @DisplayName("프로젝트 댓글 수 감소 성공 시 정상 동작")
  void decreaseCommentCountSuccess() throws IOException {
    // given
    Long projectId = 456L;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.decreaseCommentCount(projectId);

      // then
      then(elasticsearchClient)
          .should()
          .update(any(Function.class), eq(ProjectSearchDocument.class));
      then(elasticLogger)
          .should()
          .logUpdate("project_index", "456", "프로젝트 commentCount 감분 업데이트 완료 - projectId=456");
    }
  }

  @Test
  @DisplayName("댓글 수 증가 시 IOException 발생하면 EsUpdateException으로 변환")
  void increaseCommentCountWithIOException() throws IOException {
    // given
    Long projectId = 123L;
    IOException ioException = new IOException("Elasticsearch connection failed");
    willThrow(ioException)
        .given(elasticsearchClient)
        .update(any(Function.class), eq(ProjectSearchDocument.class));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when & then
      EsUpdateException exception =
          catchThrowableOfType(
              () -> adapter.increaseCommentCount(projectId), EsUpdateException.class);
      assertAll(
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .isInstanceOf(EsUpdateException.class),
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .hasMessage("ES update failed: projectId=123"),
          () -> org.assertj.core.api.Assertions.assertThat(exception).hasCause(ioException));

      then(elasticLogger)
          .should()
          .logError(
              eq("project_index"),
              eq("프로젝트 commentCount 증분 업데이트 실패 - projectId=123"),
              any(IOException.class));
    }
  }

  @Test
  @DisplayName("댓글 수 감소 시 IOException 발생하면 EsUpdateException으로 변환")
  void decreaseCommentCountWithIOException() throws IOException {
    // given
    Long projectId = 456L;
    IOException ioException = new IOException("Network error");
    willThrow(ioException)
        .given(elasticsearchClient)
        .update(any(Function.class), eq(ProjectSearchDocument.class));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when & then
      EsUpdateException exception =
          catchThrowableOfType(
              () -> adapter.decreaseCommentCount(projectId), EsUpdateException.class);
      assertAll(
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .isInstanceOf(EsUpdateException.class),
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .hasMessage("ES update failed: projectId=456"),
          () -> org.assertj.core.api.Assertions.assertThat(exception).hasCause(ioException));

      then(elasticLogger)
          .should()
          .logError(
              eq("project_index"),
              eq("프로젝트 commentCount 감분 업데이트 실패 - projectId=456"),
              any(IOException.class));
    }
  }
}
