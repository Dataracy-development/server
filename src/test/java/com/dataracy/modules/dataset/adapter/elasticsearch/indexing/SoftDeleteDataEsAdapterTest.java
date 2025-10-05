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
class SoftDeleteDataEsAdapterTest {

  // Test constants
  private static final Long PROJECT_ID = 1L;

  @Mock private ElasticsearchClient elasticsearchClient;

  @Mock private ElasticLogger elasticLogger;

  private SoftDeleteDataEsAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new SoftDeleteDataEsAdapter(elasticsearchClient);
  }

  @Test
  @DisplayName("데이터 소프트 삭제 성공 시 정상 동작")
  void deleteDataSuccess() throws IOException {
    // given
    Long dataId = 1L;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.deleteData(dataId);

      // then
      then(elasticsearchClient).should().update(any(Function.class), eq(DataSearchDocument.class));
      then(elasticLogger).should().logUpdate("data_index", "1", "데이터셋 soft delete 완료: dataId=1");
    }
  }

  @Test
  @DisplayName("데이터 복원 성공 시 정상 동작")
  void restoreDataSuccess() throws IOException {
    // given
    Long dataId = PROJECT_ID;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.restoreData(dataId);

      // then
      then(elasticsearchClient).should().update(any(Function.class), eq(DataSearchDocument.class));
      then(elasticLogger).should().logUpdate("data_index", "1", "데이터셋 복원 완료: dataId=1");
    }
  }

  @Test
  @DisplayName("소프트 삭제 시 IOException 발생하면 EsUpdateException으로 변환")
  void deleteDataWithIOException() throws IOException {
    // given
    Long dataId = 1L;
    IOException ioException = new IOException("Connection failed");
    willThrow(ioException)
        .given(elasticsearchClient)
        .update(any(Function.class), eq(DataSearchDocument.class));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when & then
      EsUpdateException exception =
          catchThrowableOfType(() -> adapter.deleteData(dataId), EsUpdateException.class);
      assertAll(
          () -> org.assertj.core.api.Assertions.assertThat(exception).isNotNull(),
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .hasMessage("ES update failed: dataId=1"),
          () -> org.assertj.core.api.Assertions.assertThat(exception).hasCause(ioException));

      then(elasticLogger)
          .should()
          .logError(eq("data_index"), eq("데이터셋 soft delete 실패: dataId=1"), any(IOException.class));
    }
  }

  @Test
  @DisplayName("복원 시 IOException 발생하면 EsUpdateException으로 변환")
  void restoreDataWithIOException() throws IOException {
    // given
    Long dataId = PROJECT_ID;
    IOException ioException = new IOException("Network error");
    willThrow(ioException)
        .given(elasticsearchClient)
        .update(any(Function.class), eq(DataSearchDocument.class));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when & then
      EsUpdateException exception =
          catchThrowableOfType(() -> adapter.restoreData(dataId), EsUpdateException.class);
      assertAll(
          () -> org.assertj.core.api.Assertions.assertThat(exception).isNotNull(),
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .hasMessage("ES update failed: dataId=1"),
          () -> org.assertj.core.api.Assertions.assertThat(exception).hasCause(ioException));

      then(elasticLogger)
          .should()
          .logError(eq("data_index"), eq("데이터셋 복원 실패: dataId=1"), any(IOException.class));
    }
  }
}
