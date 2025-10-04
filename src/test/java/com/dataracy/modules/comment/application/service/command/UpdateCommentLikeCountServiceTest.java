package com.dataracy.modules.comment.application.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.comment.application.port.out.command.update.UpdateCommentLikePort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateCommentLikeCountServiceTest {

  @Mock private UpdateCommentLikePort updateCommentLikePort;

  @InjectMocks private UpdateCommentLikeCountService service;

  @Nested
  @DisplayName("좋아요 증가")
  class IncreaseLike {

    @Test
    @DisplayName("좋아요 증가 성공")
    void increaseLikeCountSuccess() {
      // given
      willDoNothing().given(updateCommentLikePort).increaseLikeCount(1L);

      // when
      service.increaseLikeCount(1L);

      // then
      then(updateCommentLikePort).should().increaseLikeCount(1L);
    }

    @Test
    @DisplayName("좋아요 증가 시 대상 댓글이 없으면 실패 → NOT_FOUND_COMMENT")
    void increaseLikeCountShouldThrowWhenNotFound() {
      // given
      willThrow(new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT))
          .given(updateCommentLikePort)
          .increaseLikeCount(999L);

      // when & then
      CommentException ex =
          catchThrowableOfType(() -> service.increaseLikeCount(999L), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
    }

    @Test
    @DisplayName("좋아요 증가 시 프로젝트와 댓글 불일치 → MISMATCH_PROJECT_COMMENT")
    void increaseLikeCountShouldThrowWhenProjectMismatch() {
      // given
      willThrow(new CommentException(CommentErrorStatus.MISMATCH_PROJECT_COMMENT))
          .given(updateCommentLikePort)
          .increaseLikeCount(100L);

      // when & then
      CommentException ex =
          catchThrowableOfType(() -> service.increaseLikeCount(100L), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
    }
  }

  @Nested
  @DisplayName("좋아요 감소")
  class DecreaseLike {

    @Test
    @DisplayName("좋아요 감소 성공")
    void decreaseLikeCountSuccess() {
      // given
      willDoNothing().given(updateCommentLikePort).decreaseLikeCount(1L);

      // when
      service.decreaseLikeCount(1L);

      // then
      then(updateCommentLikePort).should().decreaseLikeCount(1L);
    }

    @Test
    @DisplayName("좋아요 감소 시 대상 댓글이 없으면 실패 → NOT_FOUND_COMMENT")
    void decreaseLikeCountShouldThrowWhenNotFound() {
      // given
      willThrow(new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT))
          .given(updateCommentLikePort)
          .decreaseLikeCount(999L);

      // when & then
      CommentException ex =
          catchThrowableOfType(() -> service.decreaseLikeCount(999L), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
    }

    @Test
    @DisplayName("좋아요 감소 시 프로젝트와 댓글 불일치 → MISMATCH_PROJECT_COMMENT")
    void decreaseLikeCountShouldThrowWhenProjectMismatch() {
      // given
      willThrow(new CommentException(CommentErrorStatus.MISMATCH_PROJECT_COMMENT))
          .given(updateCommentLikePort)
          .decreaseLikeCount(100L);

      // when & then
      CommentException ex =
          catchThrowableOfType(() -> service.decreaseLikeCount(100L), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
    }
  }
}
