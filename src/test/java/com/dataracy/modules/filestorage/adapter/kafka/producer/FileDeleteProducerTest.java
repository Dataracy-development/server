package com.dataracy.modules.filestorage.adapter.kafka.producer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import com.dataracy.modules.common.logging.KafkaLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class FileDeleteProducerTest {

  @Mock private KafkaTemplate<String, String> kafkaTemplate;

  @Mock private KafkaLogger kafkaLogger;

  private FileDeleteProducer producer;

  @BeforeEach
  void setUp() {
    producer = new FileDeleteProducer(kafkaTemplate);
    ReflectionTestUtils.setField(producer, "fileDeleteTopic", "file-delete-topic");
  }

  @Test
  @DisplayName("파일 삭제 이벤트 발행 성공 시 정상 로깅")
  void sendDeleteEventSuccess() {
    // given
    String fileUrl = "http://example.com/file.jpg";
    CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
    future.complete(mock(SendResult.class));

    willReturn(future).given(kafkaTemplate).send(eq("file-delete-topic"), eq(fileUrl));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      producer.sendDeleteEvent(fileUrl);

      // then
      then(kafkaTemplate).should().send("file-delete-topic", fileUrl);
      then(kafkaLogger).should().logProduce("file-delete-topic", "파일 삭제 이벤트 발송됨");
    }
  }

  @Test
  @DisplayName("파일 삭제 이벤트 발행 실패 시 에러 로깅")
  void sendDeleteEventFailure() {
    // given
    String fileUrl = "http://example.com/file.pdf";
    CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
    RuntimeException exception = new RuntimeException("Kafka send failed");
    future.completeExceptionally(exception);

    willReturn(future).given(kafkaTemplate).send(eq("file-delete-topic"), eq(fileUrl));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      producer.sendDeleteEvent(fileUrl);

      // then
      then(kafkaTemplate).should().send("file-delete-topic", fileUrl);
      then(kafkaLogger)
          .should()
          .logError(eq("file-delete-topic"), eq("파일 삭제 이벤트 발송 처리 실패"), any(RuntimeException.class));
    }
  }

  @Test
  @DisplayName("null 파일 URL로 이벤트 발행 시 경고 로깅")
  void sendDeleteEventWithNullFileUrl() {
    // given
    String fileUrl = null;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      producer.sendDeleteEvent(fileUrl);

      // then
      then(kafkaTemplate).shouldHaveNoInteractions();
      then(kafkaLogger)
          .should()
          .logWarning("fileDeleteTopic", "파일 삭제 이벤트 발송을 위한 파일 url이 존재하지 않습니다.");
    }
  }

  @Test
  @DisplayName("빈 문자열 파일 URL로 이벤트 발행 시 경고 로깅")
  void sendDeleteEventWithEmptyFileUrl() {
    // given
    String fileUrl = "   ";

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      producer.sendDeleteEvent(fileUrl);

      // then
      then(kafkaTemplate).shouldHaveNoInteractions();
      then(kafkaLogger)
          .should()
          .logWarning("fileDeleteTopic", "파일 삭제 이벤트 발송을 위한 파일 url이 존재하지 않습니다.");
    }
  }
}
