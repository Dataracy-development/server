/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.elasticsearch.indexing;

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
import com.dataracy.modules.dataset.application.dto.document.DataSearchDocument;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

@ExtendWith(MockitoExtension.class)
class UpdateDataDownloadEsAdapterTest {

  @Mock private ElasticsearchClient elasticsearchClient;

  @Mock private ElasticLogger elasticLogger;

  private UpdateDataDownloadEsAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new UpdateDataDownloadEsAdapter(elasticsearchClient);
  }

  @Test
  @DisplayName("다운로드 카운트 증가 성공 시 정상 동작")
  void increaseDownloadCountSuccess() throws IOException {
    // given
    Long dataId = 123L;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.increaseDownloadCount(dataId);

      // then
      then(elasticsearchClient).should().update(any(Function.class), eq(DataSearchDocument.class));
      then(elasticLogger).should().logUpdate("data_index", "123", "dataset download++ 완료");
    }
  }

  @Test
  @DisplayName("다운로드 카운트 증가 시 IOException 발생하면 EsUpdateException으로 변환")
  void increaseDownloadCountWithIOException() throws IOException {
    // given
    Long dataId = 123L;
    IOException ioException = new IOException("Elasticsearch connection failed");
    willThrow(ioException)
        .given(elasticsearchClient)
        .update(any(Function.class), eq(DataSearchDocument.class));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when & then
      EsUpdateException exception =
          catchThrowableOfType(
              () -> adapter.increaseDownloadCount(dataId), EsUpdateException.class);
      assertAll(
          () -> org.assertj.core.api.Assertions.assertThat(exception).isNotNull(),
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .hasMessage("ES update failed: dataId=123"),
          () -> org.assertj.core.api.Assertions.assertThat(exception).hasCause(ioException));

      then(elasticLogger)
          .should()
          .logError(
              eq("data_index"), eq("dataset download++ 실패 dataId=123"), any(IOException.class));
    }
  }

  @Test
  @DisplayName("큰 데이터 ID로 다운로드 카운트 증가 성공")
  void increaseDownloadCountWithLargeId() throws IOException {
    // given
    Long dataId = 999999L;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.increaseDownloadCount(dataId);

      // then
      then(elasticsearchClient).should().update(any(Function.class), eq(DataSearchDocument.class));
      then(elasticLogger).should().logUpdate("data_index", "999999", "dataset download++ 완료");
    }
  }

  @Test
  @DisplayName("최소 데이터 ID로 다운로드 카운트 증가 성공")
  void increaseDownloadCountWithMinId() throws IOException {
    // given
    Long dataId = 1L;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.increaseDownloadCount(dataId);

      // then
      then(elasticsearchClient).should().update(any(Function.class), eq(DataSearchDocument.class));
      then(elasticLogger).should().logUpdate("data_index", "1", "dataset download++ 완료");
    }
  }
}
