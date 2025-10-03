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

import com.dataracy.modules.reference.adapter.jpa.entity.AnalysisPurposeEntity;
import com.dataracy.modules.reference.adapter.jpa.repository.AnalysisPurposeJpaRepository;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;

@ExtendWith(MockitoExtension.class)
class AnalysisPurposeDbAdapterTest {

  @Mock private AnalysisPurposeJpaRepository analysisPurposeJpaRepository;

  @InjectMocks private AnalysisPurposeDbAdapter adapter;

  @Test
  @DisplayName("분석 목적 전체 조회 성공")
  void findAllAnalysisPurposesSuccess() {
    // given
    AnalysisPurposeEntity e1 =
        AnalysisPurposeEntity.builder().id(1L).value("v1").label("l1").build();
    AnalysisPurposeEntity e2 =
        AnalysisPurposeEntity.builder().id(2L).value("v2").label("l2").build();
    given(analysisPurposeJpaRepository.findAll()).willReturn(List.of(e1, e2));

    // when
    List<AnalysisPurpose> result = adapter.findAllAnalysisPurposes();

    // then
    assertAll(
        () -> assertThat(result).hasSize(2),
        () -> assertThat(result.get(0).id()).isEqualTo(1L),
        () -> assertThat(result.get(0).value()).isEqualTo("v1"),
        () -> assertThat(result.get(0).label()).isEqualTo("l1"));
    then(analysisPurposeJpaRepository).should().findAll();
  }

  @Test
  @DisplayName("분석 목적 ID로 조회 성공 및 실패")
  void findAnalysisPurposeByIdSuccessAndEmpty() {
    // given
    Long id = 5L;
    AnalysisPurposeEntity entity =
        AnalysisPurposeEntity.builder().id(id).value("v").label("l").build();
    given(analysisPurposeJpaRepository.findById(id)).willReturn(Optional.of(entity));
    given(analysisPurposeJpaRepository.findById(999L)).willReturn(Optional.empty());

    // when
    Optional<AnalysisPurpose> found = adapter.findAnalysisPurposeById(id);
    Optional<AnalysisPurpose> missing = adapter.findAnalysisPurposeById(999L);

    // then
    assertAll(
        () -> assertThat(found).isPresent(),
        () -> assertThat(found.get().id()).isEqualTo(id),
        () -> assertThat(missing).isEmpty());
    then(analysisPurposeJpaRepository).should(times(1)).findById(id);
    then(analysisPurposeJpaRepository).should(times(1)).findById(999L);
  }

  @Test
  @DisplayName("분석 목적 존재 여부 확인 성공")
  void existsAnalysisPurposeByIdSuccess() {
    // given
    given(analysisPurposeJpaRepository.existsById(1L)).willReturn(true);
    given(analysisPurposeJpaRepository.existsById(2L)).willReturn(false);

    // when & then
    assertAll(
        () -> assertThat(adapter.existsAnalysisPurposeById(1L)).isTrue(),
        () -> assertThat(adapter.existsAnalysisPurposeById(2L)).isFalse());
    then(analysisPurposeJpaRepository).should().existsById(1L);
    then(analysisPurposeJpaRepository).should().existsById(2L);
  }

  @Test
  @DisplayName("라벨 단건 조회 성공 및 실패")
  void getLabelByIdSuccessAndEmpty() {
    // given
    given(analysisPurposeJpaRepository.findLabelById(1L)).willReturn(Optional.of("L1"));
    given(analysisPurposeJpaRepository.findLabelById(9L)).willReturn(Optional.empty());

    // when & then
    assertAll(
        () -> assertThat(adapter.getLabelById(1L)).contains("L1"),
        () -> assertThat(adapter.getLabelById(9L)).isEmpty());
    then(analysisPurposeJpaRepository).should().findLabelById(1L);
    then(analysisPurposeJpaRepository).should().findLabelById(9L);
  }

  @Test
  @DisplayName("라벨 다건 조회 성공")
  void getLabelsByIdsSuccess() {
    // given
    AnalysisPurposeEntity e1 =
        AnalysisPurposeEntity.builder().id(1L).value("v1").label("L1").build();
    AnalysisPurposeEntity e2 =
        AnalysisPurposeEntity.builder().id(2L).value("v2").label("L2").build();
    given(analysisPurposeJpaRepository.findAllById(List.of(1L, 2L))).willReturn(List.of(e1, e2));

    // when
    Map<Long, String> result = adapter.getLabelsByIds(List.of(1L, 2L));

    // then
    assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
    then(analysisPurposeJpaRepository).should().findAllById(List.of(1L, 2L));
  }
}
