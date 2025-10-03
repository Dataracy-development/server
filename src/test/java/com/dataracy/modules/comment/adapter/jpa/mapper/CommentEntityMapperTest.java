/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.adapter.jpa.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
import com.dataracy.modules.comment.domain.model.Comment;

class CommentEntityMapperTest {

  @Test
  @DisplayName("Entity -> Domain 변환")
  void toDomain() {
    // given
    CommentEntity entity = CommentEntity.of(10L, 20L, "내용", null);

    // when
    Comment domain = CommentEntityMapper.toDomain(entity);

    // then
    assertAll(
        () -> assertThat(domain).isNotNull(),
        () -> assertThat(domain.getProjectId()).isEqualTo(entity.getProjectId()),
        () -> assertThat(domain.getUserId()).isEqualTo(entity.getUserId()),
        () -> assertThat(domain.getContent()).isEqualTo(entity.getContent()));
  }

  @Test
  @DisplayName("toDomain - null 입력 시 null 반환")
  void toDomainNullInput() {
    // when
    Comment domain = CommentEntityMapper.toDomain(null);

    // then
    assertThat(domain).isNull();
  }

  @Test
  @DisplayName("Domain -> Entity 변환")
  void toEntity() {
    // given
    Comment domain = Comment.of(1L, 10L, 20L, "내용", null, 0L, null);

    // when
    CommentEntity entity = CommentEntityMapper.toEntity(domain);

    // then
    assertAll(
        () -> assertThat(entity).isNotNull(),
        () -> assertThat(entity.getProjectId()).isEqualTo(domain.getProjectId()),
        () -> assertThat(entity.getUserId()).isEqualTo(domain.getUserId()),
        () -> assertThat(entity.getContent()).isEqualTo(domain.getContent()));
  }

  @Test
  @DisplayName("toEntity - null 입력 시 null 반환")
  void toEntityNullInput() {
    // when
    CommentEntity entity = CommentEntityMapper.toEntity(null);

    // then
    assertThat(entity).isNull();
  }

  @Test
  @DisplayName("toEntity - domain 값 누락 시 기본값 처리 확인")
  void toEntityMissingValues() {
    // given
    Comment domain = Comment.of(null, 1L, 2L, "내용", null, 0L, null);

    // when
    CommentEntity entity = CommentEntityMapper.toEntity(domain);

    // then
    assertAll(
        () -> assertThat(entity).isNotNull(),
        () -> assertThat(entity.getId()).isNull(),
        () -> assertThat(entity.getLikeCount()).isZero() // default
        );
  }
}
