package com.dataracy.modules.like.adapter.kafka.producer;

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
import com.dataracy.modules.like.domain.enums.TargetType;

@ExtendWith(MockitoExtension.class)
class LikeKafkaProducerAdapterTest {

  // Test constants
  private static final Integer CURRENT_YEAR = 2024;

  // Test constants
  private static final Long TEST_ID_1 = 1L;
  private static final Long TEST_ID_2 = 2L;

  @Mock private KafkaTemplate<String, Long> kafkaTemplate;

  @Mock private KafkaLogger kafkaLogger;

  private LikeKafkaProducerAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new LikeKafkaProducerAdapter(kafkaTemplate);
    ReflectionTestUtils.setField(
        adapter, "topicProjectLikeIncrease", "project-like-increase-topic");
    ReflectionTestUtils.setField(
        adapter, "topicProjectLikeDecrease", "project-like-decrease-topic");
    ReflectionTestUtils.setField(
        adapter, "topicCommentLikeIncrease", "comment-like-increase-topic");
    ReflectionTestUtils.setField(
        adapter, "topicCommentLikeDecrease", "comment-like-decrease-topic");
  }

  @Test
  @DisplayName("프로젝트 좋아요 이벤트 발행 성공")
  void sendProjectLikeEventSuccess() {
    // given
    Long projectId = 1L;
    @SuppressWarnings("unchecked")
    CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
    future.complete(mock(SendResult.class));

    willReturn(future)
        .given(kafkaTemplate)
        .send(eq("project-like-increase-topic"), eq("1"), eq(1L));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      adapter.sendLikeEvent(TargetType.PROJECT, projectId, false);

      // then
      then(kafkaTemplate).should().send("project-like-increase-topic", "1", 1L);
      then(kafkaLogger)
          .should()
          .logProduce("project-like-increase-topic", "프로젝트 좋아요 이벤트 발송됨: projectId=1");
    }
  }

  @Test
  @DisplayName("프로젝트 좋아요 취소 이벤트 발행 성공")
  void sendProjectLikeDecreaseEventSuccess() {
    // given
    Long projectId = TEST_ID_1;
    @SuppressWarnings("unchecked")
    CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
    future.complete(mock(SendResult.class));

    willReturn(future)
        .given(kafkaTemplate)
        .send(eq("project-like-decrease-topic"), eq("1"), eq(TEST_ID_1));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      adapter.sendLikeEvent(TargetType.PROJECT, projectId, true);

      // then
      then(kafkaTemplate).should().send("project-like-decrease-topic", "1", TEST_ID_1);
      then(kafkaLogger)
          .should()
          .logProduce("project-like-decrease-topic", "프로젝트 좋아요 취소 이벤트 발송됨: projectId=1");
    }
  }

  @Test
  @DisplayName("댓글 좋아요 이벤트 발행 성공")
  void sendCommentLikeEventSuccess() {
    // given
    Long commentId = TEST_ID_2;
    @SuppressWarnings("unchecked")
    CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
    future.complete(mock(SendResult.class));

    willReturn(future)
        .given(kafkaTemplate)
        .send(eq("comment-like-increase-topic"), eq("2"), eq(TEST_ID_2));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      adapter.sendLikeEvent(TargetType.COMMENT, commentId, false);

      // then
      then(kafkaTemplate).should().send("comment-like-increase-topic", "2", TEST_ID_2);
      then(kafkaLogger)
          .should()
          .logProduce("comment-like-increase-topic", "댓글 좋아요 이벤트 발송됨: commentId=2");
    }
  }

  @Test
  @DisplayName("댓글 좋아요 취소 이벤트 발행 실패 시 에러 로깅")
  void sendCommentLikeDecreaseEventFailure() {
    // given
    Long commentId = 999L;
    @SuppressWarnings("unchecked")
    CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
    RuntimeException exception = new RuntimeException("Kafka connection failed");
    future.completeExceptionally(exception);

    willReturn(future)
        .given(kafkaTemplate)
        .send(eq("comment-like-decrease-topic"), eq("999"), eq(999L));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      adapter.sendLikeEvent(TargetType.COMMENT, commentId, true);

      // then
      then(kafkaTemplate).should().send("comment-like-decrease-topic", "999", 999L);
      then(kafkaLogger)
          .should()
          .logError(
              eq("comment-like-decrease-topic"),
              eq("댓글 좋아요 취소 이벤트 발송 처리 실패: commentId=999"),
              any(RuntimeException.class));
    }
  }
}
