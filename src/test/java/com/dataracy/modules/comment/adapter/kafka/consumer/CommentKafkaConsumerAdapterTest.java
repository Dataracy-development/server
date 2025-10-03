/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.adapter.kafka.consumer;

import static org.assertj.core.api.Assertions.assertThat;
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

import com.dataracy.modules.comment.application.port.in.command.count.DecreaseLikeCountUseCase;
import com.dataracy.modules.comment.application.port.in.command.count.IncreaseLikeCountUseCase;
import com.dataracy.modules.common.logging.KafkaLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class CommentKafkaConsumerAdapterTest {

  @Mock private IncreaseLikeCountUseCase increaseLikeCountUseCase;

  @Mock private DecreaseLikeCountUseCase decreaseLikeCountUseCase;

  @Mock private KafkaLogger kafkaLogger;

  private CommentKafkaConsumerAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new CommentKafkaConsumerAdapter(increaseLikeCountUseCase, decreaseLikeCountUseCase);
    ReflectionTestUtils.setField(
        adapter, "commentLikeIncreaseTopic", "comment-like-increase-topic");
    ReflectionTestUtils.setField(
        adapter, "commentLikeDecreaseTopic", "comment-like-decrease-topic");
  }

  @Test
  @DisplayName("댓글 좋아요 증가 이벤트 수신 시 좋아요 수 증가 성공")
  void consumeLikeIncreaseSuccess() {
    // given
    Long commentId = 123L;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      adapter.consumeLikeIncrease(commentId);

      // then
      then(increaseLikeCountUseCase).should().increaseLikeCount(commentId);
      then(kafkaLogger)
          .should()
          .logConsume("comment-like-increase-topic", "댓글 좋아요 이벤트 수신됨: commentId=123");
      then(kafkaLogger)
          .should()
          .logConsume("comment-like-increase-topic", "댓글 좋아요 이벤트 처리 완료: commentId=123");
    }
  }

  @Test
  @DisplayName("댓글 좋아요 증가 이벤트 처리 실패 시 예외 재발생")
  void consumeLikeIncreaseFailure() {
    // given
    Long commentId = 456L;
    RuntimeException exception = new RuntimeException("Database error");
    willThrow(exception).given(increaseLikeCountUseCase).increaseLikeCount(commentId);

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when & then
      RuntimeException caughtException =
          catchThrowableOfType(
              () -> adapter.consumeLikeIncrease(commentId), RuntimeException.class);
      assertAll(() -> assertThat(caughtException).isSameAs(exception));

      then(kafkaLogger)
          .should()
          .logConsume("comment-like-increase-topic", "댓글 좋아요 이벤트 수신됨: commentId=456");
      then(kafkaLogger)
          .should()
          .logError(
              eq("comment-like-increase-topic"),
              eq("댓글 좋아요 이벤트 처리 실패: commentId=456"),
              any(RuntimeException.class));
    }
  }

  @Test
  @DisplayName("댓글 좋아요 감소 이벤트 수신 시 좋아요 수 감소 성공")
  void consumeLikeDecreaseSuccess() {
    // given
    Long commentId = 789L;

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when
      adapter.consumeLikeDecrease(commentId);

      // then
      then(decreaseLikeCountUseCase).should().decreaseLikeCount(commentId);
      then(kafkaLogger)
          .should()
          .logConsume("comment-like-decrease-topic", "댓글 좋아요 취소 이벤트 수신됨: commentId=789");
      then(kafkaLogger)
          .should()
          .logConsume("comment-like-decrease-topic", "댓글 좋아요 취소 이벤트 처리 완료: commentId=789");
    }
  }

  @Test
  @DisplayName("댓글 좋아요 감소 이벤트 처리 실패 시 예외 재발생")
  void consumeLikeDecreaseFailure() {
    // given
    Long commentId = 999L;
    RuntimeException exception = new RuntimeException("Elasticsearch error");
    willThrow(exception).given(decreaseLikeCountUseCase).decreaseLikeCount(commentId);

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

      // when & then
      RuntimeException caughtException =
          catchThrowableOfType(
              () -> adapter.consumeLikeDecrease(commentId), RuntimeException.class);
      assertAll(() -> assertThat(caughtException).isSameAs(exception));

      then(kafkaLogger)
          .should()
          .logConsume("comment-like-decrease-topic", "댓글 좋아요 취소 이벤트 수신됨: commentId=999");
      then(kafkaLogger)
          .should()
          .logError(
              eq("comment-like-decrease-topic"),
              eq("댓글 좋아요 취소 이벤트 처리 실패: commentId=999"),
              any(RuntimeException.class));
    }
  }
}
