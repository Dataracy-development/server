package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.reference.adapter.jpa.entity.OccupationEntity;
import com.dataracy.modules.reference.adapter.jpa.repository.OccupationJpaRepository;
import com.dataracy.modules.reference.domain.model.Occupation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OccupationDbAdapterTest {

    @Mock
    private OccupationJpaRepository occupationJpaRepository;

    @InjectMocks
    private OccupationDbAdapter adapter;

    @Test
    @DisplayName("직업 전체 조회 성공")
    void findAllOccupationsSuccess() {
        // given
        OccupationEntity e1 = OccupationEntity.builder().id(1L).value("v1").label("l1").build();
        OccupationEntity e2 = OccupationEntity.builder().id(2L).value("v2").label("l2").build();
        given(occupationJpaRepository.findAll()).willReturn(List.of(e1, e2));

        // when
        List<Occupation> result = adapter.findAllOccupations();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).value()).isEqualTo("v1");
        assertThat(result.get(0).label()).isEqualTo("l1");
        then(occupationJpaRepository).should().findAll();
    }

    @Test
    @DisplayName("직업 단건 조회 성공 및 실패")
    void findOccupationByIdSuccessAndEmpty() {
        // given
        Long id = 5L;
        OccupationEntity entity = OccupationEntity.builder().id(id).value("v").label("l").build();
        given(occupationJpaRepository.findById(id)).willReturn(Optional.of(entity));
        given(occupationJpaRepository.findById(999L)).willReturn(Optional.empty());

        // when
        Optional<Occupation> found = adapter.findOccupationById(id);
        Optional<Occupation> missing = adapter.findOccupationById(999L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(id);
        assertThat(missing).isEmpty();
        then(occupationJpaRepository).should(times(1)).findById(id);
        then(occupationJpaRepository).should(times(1)).findById(999L);
    }

    @Test
    @DisplayName("직업 존재 여부 확인 성공")
    void existsOccupationByIdSuccess() {
        // given
        given(occupationJpaRepository.existsById(1L)).willReturn(true);
        given(occupationJpaRepository.existsById(2L)).willReturn(false);

        // when & then
        assertThat(adapter.existsOccupationById(1L)).isTrue();
        assertThat(adapter.existsOccupationById(2L)).isFalse();
        then(occupationJpaRepository).should().existsById(1L);
        then(occupationJpaRepository).should().existsById(2L);
    }

    @Test
    @DisplayName("직업 라벨 단건 조회 성공 및 실패")
    void getLabelByIdSuccessAndEmpty() {
        // given
        given(occupationJpaRepository.findLabelById(1L)).willReturn(Optional.of("L1"));
        given(occupationJpaRepository.findLabelById(9L)).willReturn(Optional.empty());

        // when & then
        assertThat(adapter.getLabelById(1L)).contains("L1");
        assertThat(adapter.getLabelById(9L)).isEmpty();
        then(occupationJpaRepository).should().findLabelById(1L);
        then(occupationJpaRepository).should().findLabelById(9L);
    }

    @Test
    @DisplayName("직업 라벨 다건 조회 성공")
    void getLabelsByIdsSuccess() {
        // given
        OccupationEntity e1 = OccupationEntity.builder().id(1L).value("v1").label("L1").build();
        OccupationEntity e2 = OccupationEntity.builder().id(2L).value("v2").label("L2").build();
        given(occupationJpaRepository.findAllById(List.of(1L, 2L))).willReturn(List.of(e1, e2));

        // when
        Map<Long, String> result = adapter.getLabelsByIds(List.of(1L, 2L));

        // then
        assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
        then(occupationJpaRepository).should().findAllById(List.of(1L, 2L));
    }
}
