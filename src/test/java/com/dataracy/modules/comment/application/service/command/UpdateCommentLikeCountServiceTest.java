package com.dataracy.modules.comment.application.service.command;

import com.dataracy.modules.comment.application.port.out.command.update.UpdateCommentLikePort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

class UpdateCommentLikeCountServiceTest {

    private UpdateCommentLikePort updateCommentLikePort;
    private UpdateCommentLikeCountService service;

    @BeforeEach
    void setup() {
        updateCommentLikePort = mock(UpdateCommentLikePort.class);
        service = new UpdateCommentLikeCountService(updateCommentLikePort);
    }

    @Test
    @DisplayName("좋아요 증가 성공")
    void increaseLikeCount() {
        willDoNothing().given(updateCommentLikePort).increaseLikeCount(1L);

        service.increaseLikeCount(1L);

        then(updateCommentLikePort).should().increaseLikeCount(1L);
    }

    @Test
    @DisplayName("좋아요 증가 시 대상 댓글이 없으면 실패 → NOT_FOUND_COMMENT")
    void increaseLikeCountFailNotFound() {
        willThrow(new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT))
                .given(updateCommentLikePort).increaseLikeCount(999L);

        CommentException ex = catchThrowableOfType(
                () -> service.increaseLikeCount(999L),
                CommentException.class
        );

        assertThat(ex.getErrorCode())
                .isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
    }

    @Test
    @DisplayName("좋아요 증가 시 프로젝트와 댓글 불일치 → MISMATCH_PROJECT_COMMENT")
    void increaseLikeCountFailMismatchProject() {
        willThrow(new CommentException(CommentErrorStatus.MISMATCH_PROJECT_COMMENT))
                .given(updateCommentLikePort).increaseLikeCount(100L);

        CommentException ex = catchThrowableOfType(
                () -> service.increaseLikeCount(100L),
                CommentException.class
        );

        assertThat(ex.getErrorCode())
                .isEqualTo(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
    }

    @Test
    @DisplayName("좋아요 감소 성공")
    void decreaseLikeCount() {
        willDoNothing().given(updateCommentLikePort).decreaseLikeCount(1L);

        service.decreaseLikeCount(1L);

        then(updateCommentLikePort).should().decreaseLikeCount(1L);
    }

    @Test
    @DisplayName("좋아요 감소 시 대상 댓글이 없으면 실패 → NOT_FOUND_COMMENT")
    void decreaseLikeCountFailNotFound() {
        willThrow(new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT))
                .given(updateCommentLikePort).decreaseLikeCount(999L);

        CommentException ex = catchThrowableOfType(
                () -> service.decreaseLikeCount(999L),
                CommentException.class
        );

        assertThat(ex.getErrorCode())
                .isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
    }

    @Test
    @DisplayName("좋아요 감소 시 프로젝트와 댓글 불일치 → MISMATCH_PROJECT_COMMENT")
    void decreaseLikeCountFailMismatchProject() {
        willThrow(new CommentException(CommentErrorStatus.MISMATCH_PROJECT_COMMENT))
                .given(updateCommentLikePort).decreaseLikeCount(100L);

        CommentException ex = catchThrowableOfType(
                () -> service.decreaseLikeCount(100L),
                CommentException.class
        );

        assertThat(ex.getErrorCode())
                .isEqualTo(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
    }
}
