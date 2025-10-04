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

import com.dataracy.modules.reference.adapter.jpa.entity.VisitSourceEntity;
import com.dataracy.modules.reference.adapter.jpa.repository.VisitSourceJpaRepository;
import com.dataracy.modules.reference.domain.model.VisitSource;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VisitSourceDbAdapterTest {

  @Mock private VisitSourceJpaRepository visitSourceJpaRepository;

  @InjectMocks private VisitSourceDbAdapter adapter;

  @Test
  @DisplayName("방문 경로 전체 조회 성공")
  void findAllVisitSourcesSuccess() {
    // given
    VisitSourceEntity e1 = VisitSourceEntity.builder().id(1L).value("v1").label("l1").build();
    VisitSourceEntity e2 = VisitSourceEntity.builder().id(2L).value("v2").label("l2").build();
    given(visitSourceJpaRepository.findAll()).willReturn(List.of(e1, e2));

    // when
    List<VisitSource> result = adapter.findAllVisitSources();

    // then
    assertAll(
        () -> assertThat(result).hasSize(2),
        () -> assertThat(result.get(0).id()).isEqualTo(1L),
        () -> assertThat(result.get(0).value()).isEqualTo("v1"),
        () -> assertThat(result.get(0).label()).isEqualTo("l1"));
    then(visitSourceJpaRepository).should().findAll();
  }

  @Test
  @DisplayName("방문 경로 단건 조회 성공 및 실패")
  void findVisitSourceByIdSuccessAndEmpty() {
    // given
    Long id = 5L;
    VisitSourceEntity entity = VisitSourceEntity.builder().id(id).value("v").label("l").build();
    given(visitSourceJpaRepository.findById(id)).willReturn(Optional.of(entity));
    given(visitSourceJpaRepository.findById(999L)).willReturn(Optional.empty());

    // when
    Optional<VisitSource> found = adapter.findVisitSourceById(id);
    Optional<VisitSource> missing = adapter.findVisitSourceById(999L);

    // then
    assertAll(
        () -> assertThat(found).isPresent(),
        () -> assertThat(found.get().id()).isEqualTo(id),
        () -> assertThat(missing).isEmpty());
    then(visitSourceJpaRepository).should(times(1)).findById(id);
    then(visitSourceJpaRepository).should(times(1)).findById(999L);
  }

  @Test
  @DisplayName("방문 경로 존재 여부 확인 성공")
  void existsVisitSourceByIdSuccess() {
    // given
    given(visitSourceJpaRepository.existsById(1L)).willReturn(true);
    given(visitSourceJpaRepository.existsById(2L)).willReturn(false);

    // when & then
    assertAll(
        () -> assertThat(adapter.existsVisitSourceById(1L)).isTrue(),
        () -> assertThat(adapter.existsVisitSourceById(2L)).isFalse());
    then(visitSourceJpaRepository).should().existsById(1L);
    then(visitSourceJpaRepository).should().existsById(2L);
  }

  @Test
  @DisplayName("방문 경로 라벨 단건 조회 성공 및 실패")
  void getLabelByIdSuccessAndEmpty() {
    // given
    given(visitSourceJpaRepository.findLabelById(1L)).willReturn(Optional.of("L1"));
    given(visitSourceJpaRepository.findLabelById(9L)).willReturn(Optional.empty());

    // when & then
    assertAll(
        () -> assertThat(adapter.getLabelById(1L)).contains("L1"),
        () -> assertThat(adapter.getLabelById(9L)).isEmpty());
    then(visitSourceJpaRepository).should().findLabelById(1L);
    then(visitSourceJpaRepository).should().findLabelById(9L);
  }

  @Test
  @DisplayName("방문 경로 라벨 다건 조회 성공")
  void getLabelsByIdsSuccess() {
    // given
    VisitSourceEntity e1 = VisitSourceEntity.builder().id(1L).value("v1").label("L1").build();
    VisitSourceEntity e2 = VisitSourceEntity.builder().id(2L).value("v2").label("L2").build();
    given(visitSourceJpaRepository.findAllById(List.of(1L, 2L))).willReturn(List.of(e1, e2));

    // when
    Map<Long, String> result = adapter.getLabelsByIds(List.of(1L, 2L));

    // then
    assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
    then(visitSourceJpaRepository).should().findAllById(List.of(1L, 2L));
  }
}
