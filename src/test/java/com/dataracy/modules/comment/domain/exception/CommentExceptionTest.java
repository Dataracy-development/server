package com.dataracy.modules.comment.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.comment.domain.status.CommentErrorStatus;

class CommentExceptionTest {

  @Test
  @DisplayName("CommentException은 BaseErrorCode를 포함한다")
  void exceptionHasErrorCode() {
    // when
    CommentException ex = new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT);

    // then
    assertAll(
        () -> assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT),
        () -> assertThat(ex.getErrorCode().getCode()).isEqualTo("COMMENT-002"));
  }
}
