package com.dataracy.modules.comment.application.service.validate;

import com.dataracy.modules.comment.application.port.out.validate.ValidateCommentPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ValidateCommentServiceTest {

    @Mock
    private ValidateCommentPort validateCommentPort;

    @InjectMocks
    private ValidateCommentService service;

    @Nested
    @DisplayName("댓글 존재 검증")
    class ExistsComment {

        @Test
        @DisplayName("댓글 존재 → 정상 통과")
        void validateCommentShouldPassWhenCommentExists() {
            // given
            given(validateCommentPort.existsByCommentId(1L)).willReturn(true);

            // when
            service.validateComment(1L);

            // then
            then(validateCommentPort).should().existsByCommentId(1L);
        }

        @Test
        @DisplayName("댓글 검증 실패 → 댓글이 존재하지 않으면 CommentException(NOT_FOUND_COMMENT)")
        void validateCommentShouldThrowWhenCommentNotFound() {
            // given
            Long commentId = 123L;
            given(validateCommentPort.existsByCommentId(commentId)).willReturn(false);

            // when & then
            CommentException ex = catchThrowableOfType(
                    () -> service.validateComment(commentId),
                    CommentException.class
            );
            assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
        }
    }
}
