package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.reference.adapter.jpa.entity.VisitSourceEntity;
import com.dataracy.modules.reference.adapter.jpa.repository.VisitSourceJpaRepository;
import com.dataracy.modules.reference.domain.model.VisitSource;
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
class VisitSourceDbAdapterTest {

    @Mock VisitSourceJpaRepository visitsourceJpaRepository;

    @InjectMocks VisitSourceDbAdapter adapter;

    @Test
    @DisplayName("findAllVisitSources: 성공 - 엔티티를 도메인으로 변환하여 반환")
    void findAllVisitSources_success() {
        // given
        VisitSourceEntity e1 = VisitSourceEntity.builder().id(1L).value("v1").label("l1").build();
        VisitSourceEntity e2 = VisitSourceEntity.builder().id(2L).value("v2").label("l2").build();
        given(visitsourceJpaRepository.findAll()).willReturn(List.of(e1, e2));

        // when
        List<VisitSource> result = adapter.findAllVisitSources();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).value()).isEqualTo("v1");
        assertThat(result.get(0).label()).isEqualTo("l1");
        then(visitsourceJpaRepository).should().findAll();
    }

    @Test
    @DisplayName("findVisitSourceById: 성공/실패 - 존재 시 변환, 없으면 빈 Optional")
    void findVisitSourceById_success_and_empty() {
        // given
        Long id = 5L;
        VisitSourceEntity entity = VisitSourceEntity.builder().id(id).value("v").label("l").build();
        given(visitsourceJpaRepository.findById(id)).willReturn(Optional.of(entity));
        given(visitsourceJpaRepository.findById(999L)).willReturn(Optional.empty());

        // when
        Optional<VisitSource> found = adapter.findVisitSourceById(id);
        Optional<VisitSource> missing = adapter.findVisitSourceById(999L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(id);
        assertThat(missing).isEmpty();
        then(visitsourceJpaRepository).should(times(1)).findById(id);
        then(visitsourceJpaRepository).should(times(1)).findById(999L);
    }

    @Test
    @DisplayName("existsVisitSourceById: 성공 - JPA existsById 위임")
    void existsVisitSourceById_success() {
        // given
        given(visitsourceJpaRepository.existsById(1L)).willReturn(true);
        given(visitsourceJpaRepository.existsById(2L)).willReturn(false);

        // when/then
        assertThat(adapter.existsVisitSourceById(1L)).isTrue();
        assertThat(adapter.existsVisitSourceById(2L)).isFalse();
        then(visitsourceJpaRepository).should().existsById(1L);
        then(visitsourceJpaRepository).should().existsById(2L);
    }

    @Test
    @DisplayName("getLabelById: 성공/실패 - Optional 위임")
    void getLabelById_success_and_empty() {
        // given
        given(visitsourceJpaRepository.findLabelById(1L)).willReturn(Optional.of("L1"));
        given(visitsourceJpaRepository.findLabelById(9L)).willReturn(Optional.empty());

        // when/then
        assertThat(adapter.getLabelById(1L)).contains("L1");
        assertThat(adapter.getLabelById(9L)).isEmpty();
        then(visitsourceJpaRepository).should().findLabelById(1L);
        then(visitsourceJpaRepository).should().findLabelById(9L);
    }

    @Test
    @DisplayName("getLabelsByIds: 성공 - findAllById 결과를 id->label 맵으로 반환")
    void getLabelsByIds_success() {
        // given
        VisitSourceEntity e1 = VisitSourceEntity.builder().id(1L).value("v1").label("L1").build();
        VisitSourceEntity e2 = VisitSourceEntity.builder().id(2L).value("v2").label("L2").build();
        given(visitsourceJpaRepository.findAllById(List.of(1L,2L))).willReturn(List.of(e1,e2));

        // when
        Map<Long,String> result = adapter.getLabelsByIds(List.of(1L,2L));

        // then
        assertThat(result).containsEntry(1L,"L1").containsEntry(2L,"L2");
        then(visitsourceJpaRepository).should().findAllById(List.of(1L,2L));
    }
}
