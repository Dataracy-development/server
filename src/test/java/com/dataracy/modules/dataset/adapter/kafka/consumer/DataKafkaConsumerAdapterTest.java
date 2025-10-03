/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.kafka.consumer;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.dataracy.modules.common.logging.KafkaLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.request.metadata.ParseMetadataRequest;
import com.dataracy.modules.dataset.application.port.in.command.metadata.ParseMetadataUseCase;
import com.dataracy.modules.dataset.domain.model.event.DataUploadEvent;

@ExtendWith(MockitoExtension.class)
class DataKafkaConsumerAdapterTest {

  @Mock private ParseMetadataUseCase parseMetadataUseCase;

  @Mock private KafkaLogger kafkaLogger;

  private DataKafkaConsumerAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new DataKafkaConsumerAdapter(parseMetadataUseCase);
    ReflectionTestUtils.setField(adapter, "dataUploadedTopic", "data-uploaded-topic");
  }

  @Test
  @DisplayName("데이터 업로드 이벤트 수신 시 메타데이터 파싱 성공")
  void consumeDataUploadEventSuccess() {
    // given
    DataUploadEvent event = new DataUploadEvent(123L, "http://example.com/data.csv", "dataset.csv");

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      adapter.consume(event);

      // then
      then(parseMetadataUseCase).should().parseAndSaveMetadata(any(ParseMetadataRequest.class));
      then(kafkaLogger).should().logConsume("data-uploaded-topic", "데이터셋 업로드 이벤트 수신됨: dataId=123");
      then(kafkaLogger)
          .should()
          .logConsume("data-uploaded-topic", "데이터셋 업로드 이벤트 처리 완료: dataId=123");
    }
  }

  @Test
  @DisplayName("데이터 업로드 이벤트 처리 실패 시 예외 재발생")
  void consumeDataUploadEventFailure() {
    // given
    DataUploadEvent event =
        new DataUploadEvent(456L, "http://example.com/data.xlsx", "dataset.xlsx");
    RuntimeException exception = new RuntimeException("Metadata parsing failed");
    willThrow(exception)
        .given(parseMetadataUseCase)
        .parseAndSaveMetadata(any(ParseMetadataRequest.class));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when & then
      RuntimeException caughtException =
          catchThrowableOfType(() -> adapter.consume(event), RuntimeException.class);
      assertAll(
          () -> org.assertj.core.api.Assertions.assertThat(caughtException).isSameAs(exception));

      then(kafkaLogger).should().logConsume("data-uploaded-topic", "데이터셋 업로드 이벤트 수신됨: dataId=456");
      then(kafkaLogger)
          .should()
          .logError(
              eq("data-uploaded-topic"),
              eq("데이터셋 업로드 이벤트 처리 실패: dataId=456"),
              any(RuntimeException.class));
    }
  }
}
