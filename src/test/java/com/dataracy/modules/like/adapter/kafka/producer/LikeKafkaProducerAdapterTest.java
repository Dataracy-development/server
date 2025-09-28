package com.dataracy.modules.like.adapter.kafka.producer;

import com.dataracy.modules.common.logging.KafkaLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.like.domain.enums.TargetType;
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

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class LikeKafkaProducerAdapterTest {

    @Mock
    private KafkaTemplate<String, Long> kafkaTemplate;
    
    @Mock
    private KafkaLogger kafkaLogger;

    private LikeKafkaProducerAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new LikeKafkaProducerAdapter(kafkaTemplate);
        ReflectionTestUtils.setField(adapter, "TOPIC_PROJECT_LIKE_INCREASE", "project-like-increase-topic");
        ReflectionTestUtils.setField(adapter, "TOPIC_PROJECT_LIKE_DECREASE", "project-like-decrease-topic");
        ReflectionTestUtils.setField(adapter, "TOPIC_COMMENT_LIKE_INCREASE", "comment-like-increase-topic");
        ReflectionTestUtils.setField(adapter, "TOPIC_COMMENT_LIKE_DECREASE", "comment-like-decrease-topic");
    }

    @Test
    @DisplayName("프로젝트 좋아요 이벤트 발행 성공")
    void sendProjectLikeEventSuccess() {
        // given
        Long projectId = 123L;
        CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        
        willReturn(future).given(kafkaTemplate).send(eq("project-like-increase-topic"), eq("123"), eq(123L));
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

            // when
            adapter.sendLikeEvent(TargetType.PROJECT, projectId, false);

            // then
            then(kafkaTemplate).should().send("project-like-increase-topic", "123", 123L);
            then(kafkaLogger).should().logProduce("project-like-increase-topic", "프로젝트 좋아요 이벤트 발송됨: projectId=123");
        }
    }

    @Test
    @DisplayName("프로젝트 좋아요 취소 이벤트 발행 성공")
    void sendProjectLikeDecreaseEventSuccess() {
        // given
        Long projectId = 456L;
        CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        
        willReturn(future).given(kafkaTemplate).send(eq("project-like-decrease-topic"), eq("456"), eq(456L));
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

            // when
            adapter.sendLikeEvent(TargetType.PROJECT, projectId, true);

            // then
            then(kafkaTemplate).should().send("project-like-decrease-topic", "456", 456L);
            then(kafkaLogger).should().logProduce("project-like-decrease-topic", "프로젝트 좋아요 취소 이벤트 발송됨: projectId=456");
        }
    }

    @Test
    @DisplayName("댓글 좋아요 이벤트 발행 성공")
    void sendCommentLikeEventSuccess() {
        // given
        Long commentId = 789L;
        CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        
        willReturn(future).given(kafkaTemplate).send(eq("comment-like-increase-topic"), eq("789"), eq(789L));
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

            // when
            adapter.sendLikeEvent(TargetType.COMMENT, commentId, false);

            // then
            then(kafkaTemplate).should().send("comment-like-increase-topic", "789", 789L);
            then(kafkaLogger).should().logProduce("comment-like-increase-topic", "댓글 좋아요 이벤트 발송됨: commentId=789");
        }
    }

    @Test
    @DisplayName("댓글 좋아요 취소 이벤트 발행 실패 시 에러 로깅")
    void sendCommentLikeDecreaseEventFailure() {
        // given
        Long commentId = 999L;
        CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
        RuntimeException exception = new RuntimeException("Kafka connection failed");
        future.completeExceptionally(exception);
        
        willReturn(future).given(kafkaTemplate).send(eq("comment-like-decrease-topic"), eq("999"), eq(999L));
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

            // when
            adapter.sendLikeEvent(TargetType.COMMENT, commentId, true);

            // then
            then(kafkaTemplate).should().send("comment-like-decrease-topic", "999", 999L);
            then(kafkaLogger).should().logError(eq("comment-like-decrease-topic"), eq("댓글 좋아요 취소 이벤트 발송 처리 실패: commentId=999"), any(RuntimeException.class));
        }
    }
}
