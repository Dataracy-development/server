/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.elasticsearch.indexing;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
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

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;

@ExtendWith(MockitoExtension.class)
class IndexDataAdapterTest {

  @Mock private ElasticsearchClient elasticsearchClient;

  @Mock private ElasticLogger elasticLogger;

  private IndexDataAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new IndexDataAdapter(elasticsearchClient);
  }

  @Test
  @DisplayName("데이터 인덱싱 성공 시 정상 동작")
  void indexDataSuccess() throws IOException {
    // given
    DataSearchDocument doc = createTestDocument();
    IndexResponse mockResponse = mock(IndexResponse.class);
    given(elasticsearchClient.index(any(Function.class))).willReturn(mockResponse);

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.index(doc);

      // then
      then(elasticsearchClient).should().index(any(Function.class));
      then(elasticLogger).should().logIndex("data_index", "123", "데이터셋 인덱싱 완료");
    }
  }

  @Test
  @DisplayName("IOException 발생 시 예외를 외부로 전달하지 않고 로깅만 수행")
  void indexDataFailure() throws IOException {
    // given
    DataSearchDocument doc = createTestDocument();
    given(elasticsearchClient.index(any(Function.class)))
        .willThrow(new IOException("Connection failed"));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when & then
      assertThatNoException().isThrownBy(() -> adapter.index(doc));
      then(elasticLogger)
          .should()
          .logError(eq("data_index"), eq("데이터셋 인덱싱 실패: docId=123"), any(IOException.class));
    }
  }

  private DataSearchDocument createTestDocument() {
    return DataSearchDocument.builder()
        .id(123L)
        .title("Test Dataset")
        .description("Test Description")
        .topicId(1L)
        .topicLabel("Test Topic")
        .userId(456L)
        .username("testuser")
        .userProfileImageUrl("http://example.com/profile.jpg")
        .dataSourceId(1L)
        .dataSourceLabel("Test Source")
        .dataTypeId(1L)
        .dataTypeLabel("Test Type")
        .dataFileUrl("http://example.com/data.csv")
        .dataThumbnailUrl("http://example.com/thumb.jpg")
        .downloadCount(0)
        .sizeBytes(1024L)
        .rowCount(100)
        .columnCount(10)
        .previewJson("{}")
        .createdAt(java.time.LocalDateTime.now())
        .isDeleted(false)
        .build();
  }
}
