package com.dataracy.modules.dataset.adapter.kafka.consumer;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.util.ReflectionTestUtils;

import com.dataracy.modules.common.logging.KafkaLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.request.metadata.ParseMetadataRequest;
import com.dataracy.modules.dataset.application.port.in.command.metadata.ParseMetadataUseCase;
import com.dataracy.modules.dataset.domain.model.event.DataUploadEvent;

@ExtendWith(MockitoExtension.class)
class DataKafkaConsumerAdapterTest {

  // Test constants
  private static final Long PROJECT_ID = 1L;

  @Mock private StringRedisTemplate redisTemplate;

  @Mock private ValueOperations<String, String> valueOperations;

  @Mock private ParseMetadataUseCase parseMetadataUseCase;

  @Mock private KafkaLogger kafkaLogger;

  @Mock private Acknowledgment acknowledgment;

  private DataKafkaConsumerAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new DataKafkaConsumerAdapter(redisTemplate, parseMetadataUseCase);
    ReflectionTestUtils.setField(adapter, "dataUploadedTopic", "data-uploaded-topic");
  }

  @Test
  @DisplayName("데이터 업로드 이벤트 수신 시 메타데이터 파싱 성공")
  void consumeDataUploadEventSuccess() {
    // given
    DataUploadEvent event = new DataUploadEvent(1L, "http://example.com/data.csv", "dataset.csv");
    ConsumerRecord<String, DataUploadEvent> record =
        new ConsumerRecord<>("data-uploaded-topic", 0, 0L, "key", event);
    org.mockito.BDDMockito.given(redisTemplate.opsForValue()).willReturn(valueOperations);
    org.mockito.BDDMockito.given(valueOperations.setIfAbsent(any(), any(), any())).willReturn(true);

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      adapter.consume(record, acknowledgment);

      // then
      then(parseMetadataUseCase).should().parseAndSaveMetadata(any(ParseMetadataRequest.class));
      then(kafkaLogger).should().logConsume("data-uploaded-topic", "데이터셋 업로드 이벤트 수신됨: dataId=1");
      then(kafkaLogger).should().logConsume("data-uploaded-topic", "데이터셋 업로드 이벤트 처리 완료: dataId=1");
      then(acknowledgment).should().acknowledge();
    }
  }

  @Test
  @DisplayName("데이터 업로드 이벤트 처리 실패 시 예외 재발생")
  void consumeDataUploadEventFailure() {
    // given
    DataUploadEvent event =
        new DataUploadEvent(PROJECT_ID, "http://example.com/data.xlsx", "dataset.xlsx");
    ConsumerRecord<String, DataUploadEvent> record =
        new ConsumerRecord<>("data-uploaded-topic", 0, 0L, "key", event);
    RuntimeException exception = new RuntimeException("Metadata parsing failed");
    org.mockito.BDDMockito.given(redisTemplate.opsForValue()).willReturn(valueOperations);
    org.mockito.BDDMockito.given(valueOperations.setIfAbsent(any(), any(), any())).willReturn(true);
    willThrow(exception)
        .given(parseMetadataUseCase)
        .parseAndSaveMetadata(any(ParseMetadataRequest.class));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when & then
      RuntimeException caughtException =
          catchThrowableOfType(
              () -> adapter.consume(record, acknowledgment), RuntimeException.class);
      assertAll(
          () -> org.assertj.core.api.Assertions.assertThat(caughtException).isSameAs(exception));

      then(kafkaLogger).should().logConsume("data-uploaded-topic", "데이터셋 업로드 이벤트 수신됨: dataId=1");
      then(kafkaLogger)
          .should()
          .logError(
              eq("data-uploaded-topic"),
              eq("메시지 처리 실패 - topic: data-uploaded-topic, partition: 0, offset: 0"),
              any(RuntimeException.class));
    }
  }
}
