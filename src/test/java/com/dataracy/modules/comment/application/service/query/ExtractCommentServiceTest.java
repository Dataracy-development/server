package com.dataracy.modules.comment.application.service.query;

import com.dataracy.modules.comment.application.port.out.query.extractor.ExtractCommentPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

class ExtractCommentServiceTest {

    private ExtractCommentPort extractCommentPort;
    private ExtractCommentService service;

    @BeforeEach
    void setup() {
        extractCommentPort = mock(ExtractCommentPort.class);
        service = new ExtractCommentService(extractCommentPort);
    }

    @Test
    @DisplayName("댓글 존재 → userId 반환")
    void findUserIdSuccess() {
        given(extractCommentPort.findUserIdByCommentId(1L)).willReturn(Optional.of(100L));

        Long userId = service.findUserIdByCommentId(1L);

        assertThat(userId).isEqualTo(100L);
    }

    @Test
    @DisplayName("댓글이 존재하지 않으면 실패 → NOT_FOUND_COMMENT")
    void findUserIdByCommentIdFailNotFound() {
        // given
        Long commentId = 999L;
        given(extractCommentPort.findUserIdByCommentId(commentId)).willReturn(Optional.empty());

        // when
        CommentException ex = catchThrowableOfType(
                () -> service.findUserIdByCommentId(commentId),
                CommentException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
    }
}
