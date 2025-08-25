package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.reference.adapter.jpa.entity.DataTypeEntity;
import com.dataracy.modules.reference.adapter.jpa.repository.DataTypeJpaRepository;
import com.dataracy.modules.reference.domain.model.DataType;
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
class DataTypeDbAdapterTest {

    @Mock DataTypeJpaRepository datatypeJpaRepository;

    @InjectMocks DataTypeDbAdapter adapter;

    @Test
    @DisplayName("findAllDataTypes: 성공 - 엔티티를 도메인으로 변환하여 반환")
    void findAllDataTypes_success() {
        // given
        DataTypeEntity e1 = DataTypeEntity.builder().id(1L).value("v1").label("l1").build();
        DataTypeEntity e2 = DataTypeEntity.builder().id(2L).value("v2").label("l2").build();
        given(datatypeJpaRepository.findAll()).willReturn(List.of(e1, e2));

        // when
        List<DataType> result = adapter.findAllDataTypes();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).value()).isEqualTo("v1");
        assertThat(result.get(0).label()).isEqualTo("l1");
        then(datatypeJpaRepository).should().findAll();
    }

    @Test
    @DisplayName("findDataTypeById: 성공/실패 - 존재 시 변환, 없으면 빈 Optional")
    void findDataTypeById_success_and_empty() {
        // given
        Long id = 5L;
        DataTypeEntity entity = DataTypeEntity.builder().id(id).value("v").label("l").build();
        given(datatypeJpaRepository.findById(id)).willReturn(Optional.of(entity));
        given(datatypeJpaRepository.findById(999L)).willReturn(Optional.empty());

        // when
        Optional<DataType> found = adapter.findDataTypeById(id);
        Optional<DataType> missing = adapter.findDataTypeById(999L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(id);
        assertThat(missing).isEmpty();
        then(datatypeJpaRepository).should(times(1)).findById(id);
        then(datatypeJpaRepository).should(times(1)).findById(999L);
    }

    @Test
    @DisplayName("existsDataTypeById: 성공 - JPA existsById 위임")
    void existsDataTypeById_success() {
        // given
        given(datatypeJpaRepository.existsById(1L)).willReturn(true);
        given(datatypeJpaRepository.existsById(2L)).willReturn(false);

        // when/then
        assertThat(adapter.existsDataTypeById(1L)).isTrue();
        assertThat(adapter.existsDataTypeById(2L)).isFalse();
        then(datatypeJpaRepository).should().existsById(1L);
        then(datatypeJpaRepository).should().existsById(2L);
    }

    @Test
    @DisplayName("getLabelById: 성공/실패 - Optional 위임")
    void getLabelById_success_and_empty() {
        // given
        given(datatypeJpaRepository.findLabelById(1L)).willReturn(Optional.of("L1"));
        given(datatypeJpaRepository.findLabelById(9L)).willReturn(Optional.empty());

        // when/then
        assertThat(adapter.getLabelById(1L)).contains("L1");
        assertThat(adapter.getLabelById(9L)).isEmpty();
        then(datatypeJpaRepository).should().findLabelById(1L);
        then(datatypeJpaRepository).should().findLabelById(9L);
    }

    @Test
    @DisplayName("getLabelsByIds: 성공 - findAllById 결과를 id->label 맵으로 반환")
    void getLabelsByIds_success() {
        // given
        DataTypeEntity e1 = DataTypeEntity.builder().id(1L).value("v1").label("L1").build();
        DataTypeEntity e2 = DataTypeEntity.builder().id(2L).value("v2").label("L2").build();
        given(datatypeJpaRepository.findAllById(List.of(1L,2L))).willReturn(List.of(e1,e2));

        // when
        Map<Long,String> result = adapter.getLabelsByIds(List.of(1L,2L));

        // then
        assertThat(result).containsEntry(1L,"L1").containsEntry(2L,"L2");
        then(datatypeJpaRepository).should().findAllById(List.of(1L,2L));
    }
}
