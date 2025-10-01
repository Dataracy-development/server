package com.dataracy.modules.project.adapter.kafka.consumer;

import com.dataracy.modules.common.logging.KafkaLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.port.in.command.count.DecreaseCommentCountUseCase;
import com.dataracy.modules.project.application.port.in.command.count.DecreaseLikeCountUseCase;
import com.dataracy.modules.project.application.port.in.command.count.IncreaseCommentCountUseCase;
import com.dataracy.modules.project.application.port.in.command.count.IncreaseLikeCountUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class ProjectKafkaConsumerAdapterTest {

    @Mock
    private IncreaseCommentCountUseCase increaseCommentCountUseCase;
    
    @Mock
    private DecreaseCommentCountUseCase decreaseCommentCountUseCase;
    
    @Mock
    private IncreaseLikeCountUseCase increaseLikeCountUseCase;
    
    @Mock
    private DecreaseLikeCountUseCase decreaseLikeCountUseCase;
    
    @Mock
    private KafkaLogger kafkaLogger;

    private ProjectKafkaConsumerAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProjectKafkaConsumerAdapter(
            increaseCommentCountUseCase,
            decreaseCommentCountUseCase,
            increaseLikeCountUseCase,
            decreaseLikeCountUseCase
        );
        ReflectionTestUtils.setField(adapter, "commentUploadedTopic", "comment-uploaded-topic");
        ReflectionTestUtils.setField(adapter, "commentDeletedTopic", "comment-deleted-topic");
        ReflectionTestUtils.setField(adapter, "likeIncreaseTopic", "project-like-increase-topic");
        ReflectionTestUtils.setField(adapter, "likeDecreaseTopic", "project-like-decrease-topic");
    }

    @Test
    @DisplayName("댓글 작성 이벤트 수신 시 댓글 수 증가 성공")
    void consumeCommentUploadSuccess() {
        // given
        Long projectId = 123L;
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

            // when
            adapter.consumeCommentUpload(projectId);

            // then
            then(increaseCommentCountUseCase).should().increaseCommentCount(projectId);
            then(kafkaLogger).should().logConsume("comment-uploaded-topic", "댓글 작성 이벤트 수신됨: projectId=123");
            then(kafkaLogger).should().logConsume("comment-uploaded-topic", "댓글 작성 이벤트 처리 완료: projectId=123");
        }
    }

    @Test
    @DisplayName("댓글 작성 이벤트 처리 실패 시 예외 재발생")
    void consumeCommentUploadFailure() {
        // given
        Long projectId = 123L;
        RuntimeException exception = new RuntimeException("Database error");
        willThrow(exception).given(increaseCommentCountUseCase).increaseCommentCount(projectId);
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

            // when & then
            RuntimeException caughtException = catchThrowableOfType(
                    () -> adapter.consumeCommentUpload(projectId),
                    RuntimeException.class
            );
            assertAll(
                    () -> org.assertj.core.api.Assertions.assertThat(caughtException).isSameAs(exception)
            );
            
            then(kafkaLogger).should().logConsume("comment-uploaded-topic", "댓글 작성 이벤트 수신됨: projectId=123");
            then(kafkaLogger).should().logError(eq("comment-uploaded-topic"), eq("댓글 작성 이벤트 처리 실패: projectId=123"), any(RuntimeException.class));
        }
    }

    @Test
    @DisplayName("댓글 삭제 이벤트 수신 시 댓글 수 감소 성공")
    void consumeCommentDeleteSuccess() {
        // given
        Long projectId = 456L;
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

            // when
            adapter.consumeCommentDelete(projectId);

            // then
            then(decreaseCommentCountUseCase).should().decreaseCommentCount(projectId);
            then(kafkaLogger).should().logConsume("comment-deleted-topic", "댓글 삭제 이벤트 수신됨: projectId=456");
            then(kafkaLogger).should().logConsume("comment-deleted-topic", "댓글 삭제 이벤트 처리 완료: projectId=456");
        }
    }

    @Test
    @DisplayName("좋아요 증가 이벤트 수신 시 좋아요 수 증가 성공")
    void consumeLikeIncreaseSuccess() {
        // given
        Long projectId = 789L;
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

            // when
            adapter.consumeLikeIncrease(projectId);

            // then
            then(increaseLikeCountUseCase).should().increaseLikeCount(projectId);
            then(kafkaLogger).should().logConsume("project-like-increase-topic", "프로젝트 좋아요 이벤트 수신됨: projectId=789");
            then(kafkaLogger).should().logConsume("project-like-increase-topic", "프로젝트 좋아요 이벤트 처리 완료: projectId=789");
        }
    }

    @Test
    @DisplayName("좋아요 감소 이벤트 수신 시 좋아요 수 감소 성공")
    void consumeLikeDecreaseSuccess() {
        // given
        Long projectId = 999L;
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

            // when
            adapter.consumeLikeDecrease(projectId);

            // then
            then(decreaseLikeCountUseCase).should().decreaseLikeCount(projectId);
            then(kafkaLogger).should().logConsume("project-like-decrease-topic", "프로젝트 좋아요 취소 이벤트 수신됨: projectId=999");
            then(kafkaLogger).should().logConsume("project-like-decrease-topic", "프로젝트 좋아요 취소 이벤트 처리 완료: projectId=999");
        }
    }

    @Test
    @DisplayName("좋아요 증가 이벤트 처리 실패 시 예외 재발생")
    void consumeLikeIncreaseFailure() {
        // given
        Long projectId = 789L;
        RuntimeException exception = new RuntimeException("Elasticsearch error");
        willThrow(exception).given(increaseLikeCountUseCase).increaseLikeCount(projectId);
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

            // when & then
            RuntimeException caughtException = catchThrowableOfType(
                    () -> adapter.consumeLikeIncrease(projectId),
                    RuntimeException.class
            );
            assertAll(
                    () -> org.assertj.core.api.Assertions.assertThat(caughtException).isSameAs(exception)
            );
            
            then(kafkaLogger).should().logConsume("project-like-increase-topic", "프로젝트 좋아요 이벤트 수신됨: projectId=789");
            then(kafkaLogger).should().logError(eq("project-like-increase-topic"), eq("프로젝트 좋아요 이벤트 처리 실패: projectId=789"), any(RuntimeException.class));
        }
    }
}
