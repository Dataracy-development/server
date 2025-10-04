package com.dataracy.modules.comment.adapter.kafka.producer;

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
class CommentKafkaProducerAdapterTest {

  // Test constants
  private static final Long PROJECT_ID = 1L;
  private static final Long COMMENT_ID = 2L;
  private static final Long TARGET_ID = 3L;
  private static final Long USER_ID = 4L;
  private static final Long LIKE_ID = 35L;
  private static final Long SAMPLE_ID = 42L;
  private static final Long ANOTHER_LIKE_ID = 45L;
  @Mock private KafkaTemplate<String, Long> kafkaTemplate;

  @Mock private KafkaLogger kafkaLogger;

  private CommentKafkaProducerAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new CommentKafkaProducerAdapter(kafkaTemplate);
    ReflectionTestUtils.setField(adapter, "topicUpload", "comment-uploaded-topic");
    ReflectionTestUtils.setField(adapter, "topicDelete", "comment-deleted-topic");
  }

  @Test
  @DisplayName("댓글 작성 이벤트 발행 성공 시 정상 로깅")
  void sendCommentUploadedEventSuccess() {
    // given
    Long projectId = 1L;
    CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
    future.complete(mock(SendResult.class));

    willReturn(future).given(kafkaTemplate).send(eq("comment-uploaded-topic"), eq("1"), eq(1L));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      adapter.sendCommentUploadedEvent(projectId);

      // then
      then(kafkaTemplate).should().send("comment-uploaded-topic", "1", 1L);
      then(kafkaLogger).should().logProduce("comment-uploaded-topic", "댓글 작성 이벤트 발송됨: projectId=1");
    }
  }

  @Test
  @DisplayName("댓글 작성 이벤트 발행 실패 시 에러 로깅")
  void sendCommentUploadedEventFailure() {
    // given
    Long projectId = 1L;
    CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
    RuntimeException exception = new RuntimeException("Kafka send failed");
    future.completeExceptionally(exception);

    willReturn(future).given(kafkaTemplate).send(eq("comment-uploaded-topic"), eq("1"), eq(1L));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      adapter.sendCommentUploadedEvent(projectId);

      // then
      then(kafkaTemplate).should().send("comment-uploaded-topic", "1", 1L);
      then(kafkaLogger)
          .should()
          .logError(
              eq("comment-uploaded-topic"),
              eq("댓글 작성 이벤트 발송 처리 실패: projectId=1"),
              any(RuntimeException.class));
    }
  }

  @Test
  @DisplayName("댓글 삭제 이벤트 발행 성공 시 정상 로깅")
  void sendCommentDeletedEventSuccess() {
    // given
    Long projectId = PROJECT_ID;
    CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
    future.complete(mock(SendResult.class));

    willReturn(future)
        .given(kafkaTemplate)
        .send(eq("comment-deleted-topic"), eq("1"), eq(PROJECT_ID));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      adapter.sendCommentDeletedEvent(projectId);

      // then
      then(kafkaTemplate).should().send("comment-deleted-topic", "1", PROJECT_ID);
      then(kafkaLogger).should().logProduce("comment-deleted-topic", "댓글 삭제 이벤트 발송됨: projectId=1");
    }
  }

  @Test
  @DisplayName("댓글 삭제 이벤트 발행 실패 시 에러 로깅")
  void sendCommentDeletedEventFailure() {
    // given
    Long projectId = PROJECT_ID;
    CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
    RuntimeException exception = new RuntimeException("Kafka connection failed");
    future.completeExceptionally(exception);

    willReturn(future)
        .given(kafkaTemplate)
        .send(eq("comment-deleted-topic"), eq("1"), eq(PROJECT_ID));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      adapter.sendCommentDeletedEvent(projectId);

      // then
      then(kafkaTemplate).should().send("comment-deleted-topic", "1", PROJECT_ID);
      then(kafkaLogger)
          .should()
          .logError(
              eq("comment-deleted-topic"),
              eq("댓글 삭제 이벤트 발송 처리 실패: projectId=1"),
              any(RuntimeException.class));
    }
  }
}
