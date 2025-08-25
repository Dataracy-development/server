package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.reference.adapter.jpa.entity.AnalysisPurposeEntity;
import com.dataracy.modules.reference.adapter.jpa.repository.AnalysisPurposeJpaRepository;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisPurposeDbAdapterTest {

    @Mock AnalysisPurposeJpaRepository analysispurposeJpaRepository;

    @InjectMocks AnalysisPurposeDbAdapter adapter;

    @Test
    @DisplayName("findAllAnalysisPurposes: 성공 - 엔티티를 도메인으로 변환하여 반환")
    void findAllAnalysisPurposes_success() {
        // given
        AnalysisPurposeEntity e1 = AnalysisPurposeEntity.builder().id(1L).value("v1").label("l1").build();
        AnalysisPurposeEntity e2 = AnalysisPurposeEntity.builder().id(2L).value("v2").label("l2").build();
        given(analysispurposeJpaRepository.findAll()).willReturn(List.of(e1, e2));

        // when
        List<AnalysisPurpose> result = adapter.findAllAnalysisPurposes();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).value()).isEqualTo("v1");
        assertThat(result.get(0).label()).isEqualTo("l1");
        then(analysispurposeJpaRepository).should().findAll();
    }

    @Test
    @DisplayName("findAnalysisPurposeById: 성공/실패 - 존재 시 변환, 없으면 빈 Optional")
    void findAnalysisPurposeById_success_and_empty() {
        // given
        Long id = 5L;
        AnalysisPurposeEntity entity = AnalysisPurposeEntity.builder().id(id).value("v").label("l").build();
        given(analysispurposeJpaRepository.findById(id)).willReturn(Optional.of(entity));
        given(analysispurposeJpaRepository.findById(999L)).willReturn(Optional.empty());

        // when
        Optional<AnalysisPurpose> found = adapter.findAnalysisPurposeById(id);
        Optional<AnalysisPurpose> missing = adapter.findAnalysisPurposeById(999L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(id);
        assertThat(missing).isEmpty();
        then(analysispurposeJpaRepository).should(times(1)).findById(id);
        then(analysispurposeJpaRepository).should(times(1)).findById(999L);
    }

    @Test
    @DisplayName("existsAnalysisPurposeById: 성공 - JPA existsById 위임")
    void existsAnalysisPurposeById_success() {
        // given
        given(analysispurposeJpaRepository.existsById(1L)).willReturn(true);
        given(analysispurposeJpaRepository.existsById(2L)).willReturn(false);

        // when/then
        assertThat(adapter.existsAnalysisPurposeById(1L)).isTrue();
        assertThat(adapter.existsAnalysisPurposeById(2L)).isFalse();
        then(analysispurposeJpaRepository).should().existsById(1L);
        then(analysispurposeJpaRepository).should().existsById(2L);
    }

    @Test
    @DisplayName("getLabelById: 성공/실패 - Optional 위임")
    void getLabelById_success_and_empty() {
        // given
        given(analysispurposeJpaRepository.findLabelById(1L)).willReturn(Optional.of("L1"));
        given(analysispurposeJpaRepository.findLabelById(9L)).willReturn(Optional.empty());

        // when/then
        assertThat(adapter.getLabelById(1L)).contains("L1");
        assertThat(adapter.getLabelById(9L)).isEmpty();
        then(analysispurposeJpaRepository).should().findLabelById(1L);
        then(analysispurposeJpaRepository).should().findLabelById(9L);
    }

    @Test
    @DisplayName("getLabelsByIds: 성공 - findAllById 결과를 id->label 맵으로 반환")
    void getLabelsByIds_success() {
        // given
        AnalysisPurposeEntity e1 = AnalysisPurposeEntity.builder().id(1L).value("v1").label("L1").build();
        AnalysisPurposeEntity e2 = AnalysisPurposeEntity.builder().id(2L).value("v2").label("L2").build();
        given(analysispurposeJpaRepository.findAllById(List.of(1L,2L))).willReturn(List.of(e1,e2));

        // when
        Map<Long,String> result = adapter.getLabelsByIds(List.of(1L,2L));

        // then
        assertThat(result).containsEntry(1L,"L1").containsEntry(2L,"L2");
        then(analysispurposeJpaRepository).should().findAllById(List.of(1L,2L));
    }
}
