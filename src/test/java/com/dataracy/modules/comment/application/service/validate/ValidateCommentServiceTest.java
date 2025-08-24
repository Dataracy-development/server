package com.dataracy.modules.comment.application.service.validate;

import com.dataracy.modules.comment.application.port.out.validate.ValidateCommentPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

class ValidateCommentServiceTest {

    private ValidateCommentPort validateCommentPort;
    private ValidateCommentService service;

    @BeforeEach
    void setup() {
        validateCommentPort = mock(ValidateCommentPort.class);
        service = new ValidateCommentService(validateCommentPort);
    }

    @Test
    @DisplayName("댓글 존재 → 정상 통과")
    void validateCommentSuccess() {
        given(validateCommentPort.existsByCommentId(1L)).willReturn(true);

        service.validateComment(1L);

        then(validateCommentPort).should().existsByCommentId(1L);
    }

    @Test
    @DisplayName("댓글 검증 실패 → 댓글이 존재하지 않으면 CommentException(NOT_FOUND_COMMENT)")
    void validateCommentFailWhenNotFound() {
        // given
        Long commentId = 123L;
        given(validateCommentPort.existsByCommentId(commentId)).willReturn(false);

        // when
        CommentException ex = catchThrowableOfType(
                () -> service.validateComment(commentId),
                CommentException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
    }
}
