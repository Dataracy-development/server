/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.like.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.like.domain.enums.TargetType;

/** Like 도메인 모델 테스트 */
class LikeTest {

  @Test
  @DisplayName("Like.of() 정적 팩토리 메서드로 인스턴스 생성")
  void createLikeWithOfMethod() {
    // given
    Long id = 1L;
    Long targetId = 10L;
    TargetType targetType = TargetType.PROJECT;
    Long userId = 20L;

    // when
    Like like = Like.of(id, targetId, targetType, userId);

    // then
    assertAll(
        () -> assertThat(like).isNotNull(),
        () -> assertThat(like.getId()).isEqualTo(id),
        () -> assertThat(like.getTargetId()).isEqualTo(targetId),
        () -> assertThat(like.getTargetType()).isEqualTo(targetType),
        () -> assertThat(like.getUserId()).isEqualTo(userId));
  }

  @Test
  @DisplayName("Like.builder()로 인스턴스 생성")
  void createLikeWithBuilder() {
    // given
    Long id = 2L;
    Long targetId = 15L;
    TargetType targetType = TargetType.COMMENT;
    Long userId = 25L;

    // when
    Like like =
        Like.builder().id(id).targetId(targetId).targetType(targetType).userId(userId).build();

    // then
    assertAll(
        () -> assertThat(like).isNotNull(),
        () -> assertThat(like.getId()).isEqualTo(id),
        () -> assertThat(like.getTargetId()).isEqualTo(targetId),
        () -> assertThat(like.getTargetType()).isEqualTo(targetType),
        () -> assertThat(like.getUserId()).isEqualTo(userId));
  }

  @Test
  @DisplayName("null 값들로 Like 생성")
  void createLikeWithNullValues() {
    // given
    Long id = null;
    Long targetId = null;
    TargetType targetType = null;
    Long userId = null;

    // when
    Like like = Like.of(id, targetId, targetType, userId);

    // then
    assertAll(
        () -> assertThat(like).isNotNull(),
        () -> assertThat(like.getId()).isNull(),
        () -> assertThat(like.getTargetId()).isNull(),
        () -> assertThat(like.getTargetType()).isNull(),
        () -> assertThat(like.getUserId()).isNull());
  }

  @Test
  @DisplayName("PROJECT 타겟으로 Like 생성")
  void createLikeWithProjectTarget() {
    // given
    Long id = 3L;
    Long targetId = 30L;
    TargetType targetType = TargetType.PROJECT;
    Long userId = 35L;

    // when
    Like like = Like.of(id, targetId, targetType, userId);

    // then
    assertAll(
        () -> assertThat(like).isNotNull(),
        () -> assertThat(like.getTargetType()).isEqualTo(TargetType.PROJECT));
  }

  @Test
  @DisplayName("COMMENT 타겟으로 Like 생성")
  void createLikeWithCommentTarget() {
    // given
    Long id = 4L;
    Long targetId = 40L;
    TargetType targetType = TargetType.COMMENT;
    Long userId = 45L;

    // when
    Like like = Like.of(id, targetId, targetType, userId);

    // then
    assertAll(
        () -> assertThat(like).isNotNull(),
        () -> assertThat(like.getTargetType()).isEqualTo(TargetType.COMMENT));
  }

  @Test
  @DisplayName("같은 사용자가 여러 타겟에 Like 생성")
  void createMultipleLikesBySameUser() {
    // given
    Long userId = 50L;

    // when
    Like projectLike = Like.of(1L, 100L, TargetType.PROJECT, userId);
    Like commentLike = Like.of(2L, 200L, TargetType.COMMENT, userId);

    // then
    assertAll(
        () -> assertThat(projectLike.getUserId()).isEqualTo(userId),
        () -> assertThat(commentLike.getUserId()).isEqualTo(userId),
        () -> assertThat(projectLike.getTargetType()).isEqualTo(TargetType.PROJECT),
        () -> assertThat(commentLike.getTargetType()).isEqualTo(TargetType.COMMENT));
  }
}
