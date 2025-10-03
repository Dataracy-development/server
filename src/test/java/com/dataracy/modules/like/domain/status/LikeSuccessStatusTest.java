/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.like.domain.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("LikeSuccessStatus 테스트")
class LikeSuccessStatusTest {

  @Test
  @DisplayName("모든 LikeSuccessStatus 값 확인")
  void allLikeSuccessStatuses_ShouldBeDefined() {
    // Then
    assertAll(
        () -> assertThat(LikeSuccessStatus.values()).hasSize(4),
        () -> assertThat(LikeSuccessStatus.LIKE_PROJECT).isNotNull(),
        () -> assertThat(LikeSuccessStatus.UNLIKE_PROJECT).isNotNull(),
        () -> assertThat(LikeSuccessStatus.LIKE_COMMENT).isNotNull(),
        () -> assertThat(LikeSuccessStatus.UNLIKE_COMMENT).isNotNull());
  }

  @Test
  @DisplayName("LikeSuccessStatus HTTP 상태 코드 확인")
  void likeSuccessStatuses_ShouldHaveCorrectHttpStatus() {
    // Then
    assertAll(
        () -> assertThat(LikeSuccessStatus.LIKE_PROJECT.getHttpStatus()).isEqualTo(HttpStatus.OK),
        () -> assertThat(LikeSuccessStatus.UNLIKE_PROJECT.getHttpStatus()).isEqualTo(HttpStatus.OK),
        () -> assertThat(LikeSuccessStatus.LIKE_COMMENT.getHttpStatus()).isEqualTo(HttpStatus.OK),
        () ->
            assertThat(LikeSuccessStatus.UNLIKE_COMMENT.getHttpStatus()).isEqualTo(HttpStatus.OK));
  }

  @Test
  @DisplayName("LikeSuccessStatus 코드 확인")
  void likeSuccessStatuses_ShouldHaveCorrectCode() {
    // Then
    assertAll(
        () -> assertThat(LikeSuccessStatus.LIKE_PROJECT.getCode()).isEqualTo("LIKE-001"),
        () -> assertThat(LikeSuccessStatus.UNLIKE_PROJECT.getCode()).isEqualTo("LIKE-002"),
        () -> assertThat(LikeSuccessStatus.LIKE_COMMENT.getCode()).isEqualTo("LIKE-003"),
        () -> assertThat(LikeSuccessStatus.UNLIKE_COMMENT.getCode()).isEqualTo("LIKE-004"));
  }

  @Test
  @DisplayName("LikeSuccessStatus 메시지 확인")
  void likeSuccessStatuses_ShouldHaveCorrectMessage() {
    // Then
    assertAll(
        () ->
            assertThat(LikeSuccessStatus.LIKE_PROJECT.getMessage())
                .contains("프로젝트에 대한 좋아요 처리에 성공했습니다"),
        () ->
            assertThat(LikeSuccessStatus.UNLIKE_PROJECT.getMessage())
                .contains("프로젝트에 대한 좋아요 취소 처리에 성공했습니다"),
        () ->
            assertThat(LikeSuccessStatus.LIKE_COMMENT.getMessage())
                .contains("댓글에 대한 좋아요 처리에 성공했습니다"),
        () ->
            assertThat(LikeSuccessStatus.UNLIKE_COMMENT.getMessage())
                .contains("댓글에 대한 좋아요 취소 처리에 성공했습니다"));
  }
}
