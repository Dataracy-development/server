/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.domain.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CommentStatusTest {

  @Test
  @DisplayName("CommentErrorStatus 값 검증")
  void errorStatusValues() {
    assertAll(
        () ->
            assertThat(CommentErrorStatus.NOT_FOUND_COMMENT.getHttpStatus())
                .isEqualTo(HttpStatus.NOT_FOUND),
        () -> assertThat(CommentErrorStatus.NOT_FOUND_COMMENT.getCode()).isEqualTo("COMMENT-002"),
        () ->
            assertThat(CommentErrorStatus.NOT_FOUND_COMMENT.getMessage())
                .isEqualTo("해당 피드백 댓글 리소스가 존재하지 않습니다."));
  }

  @Test
  @DisplayName("CommentSuccessStatus 값 검증")
  void successStatusValues() {
    assertAll(
        () ->
            assertThat(CommentSuccessStatus.CREATED_COMMENT.getHttpStatus())
                .isEqualTo(HttpStatus.CREATED),
        () -> assertThat(CommentSuccessStatus.CREATED_COMMENT.getCode()).isEqualTo("201"),
        () ->
            assertThat(CommentSuccessStatus.CREATED_COMMENT.getMessage())
                .isEqualTo("댓글 작성이 완료되었습니다"));
  }
}
