/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.elasticsearch.query;

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

import com.dataracy.modules.common.logging.ElasticLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.document.DataSearchDocument;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

@ExtendWith(MockitoExtension.class)
class SearchSimilarDataSetsEsAdapterTest {

  @Mock private ElasticsearchClient elasticsearchClient;

  @Mock private ElasticLogger elasticLogger;

  private SearchSimilarDataSetsEsAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new SearchSimilarDataSetsEsAdapter(elasticsearchClient);
  }

  @Test
  @DisplayName("유사 데이터셋 검색 시 IOException 발생하면 DataException으로 변환")
  void searchSimilarDataSetsWithIOException() throws IOException {
    // given
    Data data = createTestData();
    int size = 10;
    IOException ioException = new IOException("Elasticsearch connection failed");

    willThrow(ioException)
        .given(elasticsearchClient)
        .search(any(Function.class), eq(DataSearchDocument.class));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when & then
      DataException exception =
          catchThrowableOfType(
              () -> adapter.searchSimilarDataSets(data, size), DataException.class);
      assertAll(() -> org.assertj.core.api.Assertions.assertThat(exception).isNotNull());

      then(elasticLogger)
          .should()
          .logError(
              eq("data_index"), eq("유사 데이터셋 조회 실패: dataId=123, size=10"), any(IOException.class));
    }
  }

  private Data createTestData() {
    return Data.builder()
        .id(123L)
        .title("Test Dataset")
        .description("Test dataset description")
        .topicId(1L)
        .build();
  }
}
