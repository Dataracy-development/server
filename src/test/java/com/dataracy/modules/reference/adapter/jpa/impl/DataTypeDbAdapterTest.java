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

    @Mock
    private DataTypeJpaRepository dataTypeJpaRepository;

    @InjectMocks
    private DataTypeDbAdapter adapter;

    @Test
    @DisplayName("데이터 타입 전체 조회 성공")
    void findAllDataTypesSuccess() {
        // given
        DataTypeEntity e1 = DataTypeEntity.builder().id(1L).value("v1").label("l1").build();
        DataTypeEntity e2 = DataTypeEntity.builder().id(2L).value("v2").label("l2").build();
        given(dataTypeJpaRepository.findAll()).willReturn(List.of(e1, e2));

        // when
        List<DataType> result = adapter.findAllDataTypes();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).value()).isEqualTo("v1");
        assertThat(result.get(0).label()).isEqualTo("l1");
        then(dataTypeJpaRepository).should().findAll();
    }

    @Test
    @DisplayName("데이터 타입 단건 조회 성공 및 실패")
    void findDataTypeByIdSuccessAndEmpty() {
        // given
        Long id = 5L;
        DataTypeEntity entity = DataTypeEntity.builder().id(id).value("v").label("l").build();
        given(dataTypeJpaRepository.findById(id)).willReturn(Optional.of(entity));
        given(dataTypeJpaRepository.findById(999L)).willReturn(Optional.empty());

        // when
        Optional<DataType> found = adapter.findDataTypeById(id);
        Optional<DataType> missing = adapter.findDataTypeById(999L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(id);
        assertThat(missing).isEmpty();
        then(dataTypeJpaRepository).should(times(1)).findById(id);
        then(dataTypeJpaRepository).should(times(1)).findById(999L);
    }

    @Test
    @DisplayName("데이터 타입 존재 여부 확인 성공")
    void existsDataTypeByIdSuccess() {
        // given
        given(dataTypeJpaRepository.existsById(1L)).willReturn(true);
        given(dataTypeJpaRepository.existsById(2L)).willReturn(false);

        // when & then
        assertThat(adapter.existsDataTypeById(1L)).isTrue();
        assertThat(adapter.existsDataTypeById(2L)).isFalse();
        then(dataTypeJpaRepository).should().existsById(1L);
        then(dataTypeJpaRepository).should().existsById(2L);
    }

    @Test
    @DisplayName("데이터 타입 라벨 단건 조회 성공 및 실패")
    void getLabelByIdSuccessAndEmpty() {
        // given
        given(dataTypeJpaRepository.findLabelById(1L)).willReturn(Optional.of("L1"));
        given(dataTypeJpaRepository.findLabelById(9L)).willReturn(Optional.empty());

        // when & then
        assertThat(adapter.getLabelById(1L)).contains("L1");
        assertThat(adapter.getLabelById(9L)).isEmpty();
        then(dataTypeJpaRepository).should().findLabelById(1L);
        then(dataTypeJpaRepository).should().findLabelById(9L);
    }

    @Test
    @DisplayName("데이터 타입 라벨 다건 조회 성공")
    void getLabelsByIdsSuccess() {
        // given
        DataTypeEntity e1 = DataTypeEntity.builder().id(1L).value("v1").label("L1").build();
        DataTypeEntity e2 = DataTypeEntity.builder().id(2L).value("v2").label("L2").build();
        given(dataTypeJpaRepository.findAllById(List.of(1L, 2L))).willReturn(List.of(e1, e2));

        // when
        Map<Long, String> result = adapter.getLabelsByIds(List.of(1L, 2L));

        // then
        assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
        then(dataTypeJpaRepository).should().findAllById(List.of(1L, 2L));
    }
}
