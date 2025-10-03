/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.like.adapter.jpa.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.like.adapter.jpa.entity.LikeEntity;
import com.dataracy.modules.like.adapter.jpa.repository.LikeJpaRepository;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.exception.LikeException;
import com.dataracy.modules.like.domain.model.Like;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LikeCommandAdapterTest {

  @Mock LikeJpaRepository likeJpaRepository;

  @InjectMocks LikeCommandAdapter adapter;

  @Captor ArgumentCaptor<LikeEntity> entityCaptor;

  @Test
  @DisplayName("저장 성공")
  void saveLike() {
    // given
    Like like = Like.of(null, 88L, TargetType.PROJECT, 100L);
    LikeEntity entity = LikeEntity.of(88L, TargetType.PROJECT, 100L);
    given(likeJpaRepository.save(any(LikeEntity.class))).willReturn(entity);

    // when
    adapter.save(like);

    // then
    then(likeJpaRepository).should().save(entityCaptor.capture());
    LikeEntity saved = entityCaptor.getValue();
    assertAll(
        () -> assertThat(saved.getTargetId()).isEqualTo(88L),
        () -> assertThat(saved.getTargetType()).isEqualTo(TargetType.PROJECT),
        () -> assertThat(saved.getUserId()).isEqualTo(100L));
  }

  @Test
  @DisplayName("좋아요 취소 성공")
  void cancelLikeWhenExists() {
    // given
    Long userId = 7L;
    Long targetId = 3L;
    TargetType targetType = TargetType.COMMENT;
    LikeEntity existing = LikeEntity.of(targetId, targetType, userId);
    given(likeJpaRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType))
        .willReturn(Optional.of(existing));

    // when
    adapter.cancelLike(userId, targetId, targetType);

    // then
    then(likeJpaRepository).should().delete(existing);
  }

  @Test
  @DisplayName("좋아요 취소 실패 - 엔티티 없음")
  void cancelLikeWhenNotFound() {
    // given
    Long userId = 7L;
    Long targetId = 3L;
    TargetType targetType = TargetType.PROJECT;
    given(likeJpaRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType))
        .willReturn(Optional.empty());

    // when
    LikeException ex =
        catchThrowableOfType(
            () -> adapter.cancelLike(userId, targetId, targetType), LikeException.class);

    // then
    assertAll(
        () -> assertThat(ex).isNotNull(),
        () -> then(likeJpaRepository).should(never()).delete(any()));
  }
}
