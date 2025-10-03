/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.adapter.jpa.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.reference.adapter.jpa.entity.AuthorLevelEntity;
import com.dataracy.modules.reference.adapter.jpa.repository.AuthorLevelJpaRepository;
import com.dataracy.modules.reference.domain.model.AuthorLevel;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthorLevelDbAdapterTest {

  @Mock private AuthorLevelJpaRepository authorLevelJpaRepository;

  @InjectMocks private AuthorLevelDbAdapter adapter;

  @Test
  @DisplayName("작성자 레벨 전체 조회 성공")
  void findAllAuthorLevelsSuccess() {
    // given
    AuthorLevelEntity e1 = AuthorLevelEntity.builder().id(1L).value("v1").label("l1").build();
    AuthorLevelEntity e2 = AuthorLevelEntity.builder().id(2L).value("v2").label("l2").build();
    given(authorLevelJpaRepository.findAll()).willReturn(List.of(e1, e2));

    // when
    List<AuthorLevel> result = adapter.findAllAuthorLevels();

    // then
    assertAll(
        () -> assertThat(result).hasSize(2),
        () -> assertThat(result.get(0).id()).isEqualTo(1L),
        () -> assertThat(result.get(0).value()).isEqualTo("v1"),
        () -> assertThat(result.get(0).label()).isEqualTo("l1"));
    then(authorLevelJpaRepository).should().findAll();
  }

  @Test
  @DisplayName("작성자 레벨 단건 조회 성공 및 실패")
  void findAuthorLevelByIdSuccessAndEmpty() {
    // given
    Long id = 5L;
    AuthorLevelEntity entity = AuthorLevelEntity.builder().id(id).value("v").label("l").build();
    given(authorLevelJpaRepository.findById(id)).willReturn(Optional.of(entity));
    given(authorLevelJpaRepository.findById(999L)).willReturn(Optional.empty());

    // when
    Optional<AuthorLevel> found = adapter.findAuthorLevelById(id);
    Optional<AuthorLevel> missing = adapter.findAuthorLevelById(999L);

    // then
    assertAll(
        () -> assertThat(found).isPresent(),
        () -> assertThat(found.get().id()).isEqualTo(id),
        () -> assertThat(missing).isEmpty());
    then(authorLevelJpaRepository).should(times(1)).findById(id);
    then(authorLevelJpaRepository).should(times(1)).findById(999L);
  }

  @Test
  @DisplayName("작성자 레벨 존재 여부 확인 성공")
  void existsAuthorLevelByIdSuccess() {
    // given
    given(authorLevelJpaRepository.existsById(1L)).willReturn(true);
    given(authorLevelJpaRepository.existsById(2L)).willReturn(false);

    // when & then
    assertAll(
        () -> assertThat(adapter.existsAuthorLevelById(1L)).isTrue(),
        () -> assertThat(adapter.existsAuthorLevelById(2L)).isFalse());
    then(authorLevelJpaRepository).should().existsById(1L);
    then(authorLevelJpaRepository).should().existsById(2L);
  }

  @Test
  @DisplayName("작성자 레벨 라벨 단건 조회 성공 및 실패")
  void getLabelByIdSuccessAndEmpty() {
    // given
    given(authorLevelJpaRepository.findLabelById(1L)).willReturn(Optional.of("L1"));
    given(authorLevelJpaRepository.findLabelById(9L)).willReturn(Optional.empty());

    // when & then
    assertAll(
        () -> assertThat(adapter.getLabelById(1L)).contains("L1"),
        () -> assertThat(adapter.getLabelById(9L)).isEmpty());
    then(authorLevelJpaRepository).should().findLabelById(1L);
    then(authorLevelJpaRepository).should().findLabelById(9L);
  }

  @Test
  @DisplayName("작성자 레벨 라벨 다건 조회 성공")
  void getLabelsByIdsSuccess() {
    // given
    AuthorLevelEntity e1 = AuthorLevelEntity.builder().id(1L).value("v1").label("L1").build();
    AuthorLevelEntity e2 = AuthorLevelEntity.builder().id(2L).value("v2").label("L2").build();
    given(authorLevelJpaRepository.findAllById(List.of(1L, 2L))).willReturn(List.of(e1, e2));

    // when
    Map<Long, String> result = adapter.getLabelsByIds(List.of(1L, 2L));

    // then
    assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
    then(authorLevelJpaRepository).should().findAllById(List.of(1L, 2L));
  }
}
