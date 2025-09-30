package com.dataracy.modules.comment.application.service.validate;

import com.dataracy.modules.comment.application.port.out.validate.ValidateCommentPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import com.dataracy.modules.common.logging.ServiceLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ValidateCommentServiceTest {

    @InjectMocks
    private ValidateCommentService service;

    @Mock
    private ValidateCommentPort validateCommentPort;

    private MockedStatic<LoggerFactory> loggerFactoryMock;
    private ServiceLogger loggerService;

    @BeforeEach
    void setUp() {
        loggerFactoryMock = mockStatic(LoggerFactory.class);
        loggerService = mock(ServiceLogger.class);
        loggerFactoryMock.when(LoggerFactory::service).thenReturn(loggerService);
        doReturn(Instant.now()).when(loggerService).logStart(anyString(), anyString());
        doNothing().when(loggerService).logSuccess(anyString(), anyString(), any(Instant.class));
        doNothing().when(loggerService).logWarning(anyString(), anyString());
    }

    @AfterEach
    void tearDown() {
        if (loggerFactoryMock != null) {
            loggerFactoryMock.close();
        }
    }

    @Nested
    @DisplayName("validateComment 메서드 테스트")
    class ValidateCommentTest {

        @Test
        @DisplayName("댓글이 존재할 때 성공적으로 검증")
        void validateCommentSuccess() {
            // given
            Long commentId = 1L;
            given(validateCommentPort.existsByCommentId(commentId)).willReturn(true);

            // when
            service.validateComment(commentId);

            // then
            then(validateCommentPort).should().existsByCommentId(commentId);
            then(loggerService).should().logStart(eq("ValidateCommentUseCase"), 
                    contains("주어진 댓글 ID에 해당하는 댓글 존재 서비스 시작 commentId=" + commentId));
            then(loggerService).should().logSuccess(eq("ValidateCommentUseCase"), 
                    contains("주어진 댓글 ID에 해당하는 댓글 존재 서비스 종료 commentId=" + commentId), any(Instant.class));
            then(loggerService).should(never()).logWarning(anyString(), anyString());
        }

        @Test
        @DisplayName("댓글이 존재하지 않을 때 CommentException 발생")
        void validateCommentFailWhenCommentNotFound() {
            // given
            Long commentId = 999L;
            given(validateCommentPort.existsByCommentId(commentId)).willReturn(false);

            // when & then
            CommentException exception = catchThrowableOfType(() -> service.validateComment(commentId), CommentException.class);
            assertThat(exception).isNotNull();
            assertThat(exception.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);

            then(validateCommentPort).should().existsByCommentId(commentId);
            then(loggerService).should().logStart(eq("ValidateCommentUseCase"), 
                    contains("주어진 댓글 ID에 해당하는 댓글 존재 서비스 시작 commentId=" + commentId));
            then(loggerService).should().logWarning(eq("ValidateCommentUseCase"), 
                    contains("해당 댓글이 존재하지 않습니다. commentId=" + commentId));
            then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
        }

        @Test
        @DisplayName("null commentId에 대한 처리")
        void validateCommentWithNullCommentId() {
            // given
            Long commentId = null;
            given(validateCommentPort.existsByCommentId(commentId)).willReturn(false);

            // when & then
            CommentException exception = catchThrowableOfType(() -> service.validateComment(commentId), CommentException.class);
            assertThat(exception).isNotNull();
            assertThat(exception.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);

            then(validateCommentPort).should().existsByCommentId(commentId);
            then(loggerService).should().logStart(eq("ValidateCommentUseCase"), 
                    contains("주어진 댓글 ID에 해당하는 댓글 존재 서비스 시작 commentId=" + commentId));
            then(loggerService).should().logWarning(eq("ValidateCommentUseCase"), 
                    contains("해당 댓글이 존재하지 않습니다. commentId=" + commentId));
        }

        @Test
        @DisplayName("ValidateCommentPort에서 예외가 발생할 때")
        void validateCommentWhenPortThrowsException() {
            // given
            Long commentId = 1L;
            RuntimeException portException = new RuntimeException("Database connection failed");
            given(validateCommentPort.existsByCommentId(commentId)).willThrow(portException);

            // when & then
            RuntimeException exception = catchThrowableOfType(() -> service.validateComment(commentId), RuntimeException.class);
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("Database connection failed");

            then(validateCommentPort).should().existsByCommentId(commentId);
            then(loggerService).should().logStart(eq("ValidateCommentUseCase"), 
                    contains("주어진 댓글 ID에 해당하는 댓글 존재 서비스 시작 commentId=" + commentId));
            then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
            then(loggerService).should(never()).logWarning(anyString(), anyString());
        }
    }
}