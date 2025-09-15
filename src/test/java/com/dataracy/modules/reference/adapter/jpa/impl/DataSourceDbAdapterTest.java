package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.reference.adapter.jpa.entity.DataSourceEntity;
import com.dataracy.modules.reference.adapter.jpa.repository.DataSourceJpaRepository;
import com.dataracy.modules.reference.domain.model.DataSource;
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
class DataSourceDbAdapterTest {

    @Mock
    private DataSourceJpaRepository dataSourceJpaRepository;

    @InjectMocks
    private DataSourceDbAdapter adapter;

    @Test
    @DisplayName("데이터 소스 전체 조회 성공")
    void findAllDataSourcesSuccess() {
        // given
        DataSourceEntity e1 = DataSourceEntity.builder().id(1L).value("v1").label("l1").build();
        DataSourceEntity e2 = DataSourceEntity.builder().id(2L).value("v2").label("l2").build();
        given(dataSourceJpaRepository.findAll()).willReturn(List.of(e1, e2));

        // when
        List<DataSource> result = adapter.findAllDataSources();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).value()).isEqualTo("v1");
        assertThat(result.get(0).label()).isEqualTo("l1");
        then(dataSourceJpaRepository).should().findAll();
    }

    @Test
    @DisplayName("데이터 소스 단건 조회 성공 및 실패")
    void findDataSourceByIdSuccessAndEmpty() {
        // given
        Long id = 5L;
        DataSourceEntity entity = DataSourceEntity.builder().id(id).value("v").label("l").build();
        given(dataSourceJpaRepository.findById(id)).willReturn(Optional.of(entity));
        given(dataSourceJpaRepository.findById(999L)).willReturn(Optional.empty());

        // when
        Optional<DataSource> found = adapter.findDataSourceById(id);
        Optional<DataSource> missing = adapter.findDataSourceById(999L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(id);
        assertThat(missing).isEmpty();
        then(dataSourceJpaRepository).should(times(1)).findById(id);
        then(dataSourceJpaRepository).should(times(1)).findById(999L);
    }

    @Test
    @DisplayName("데이터 소스 존재 여부 확인 성공")
    void existsDataSourceByIdSuccess() {
        // given
        given(dataSourceJpaRepository.existsById(1L)).willReturn(true);
        given(dataSourceJpaRepository.existsById(2L)).willReturn(false);

        // when & then
        assertThat(adapter.existsDataSourceById(1L)).isTrue();
        assertThat(adapter.existsDataSourceById(2L)).isFalse();
        then(dataSourceJpaRepository).should().existsById(1L);
        then(dataSourceJpaRepository).should().existsById(2L);
    }

    @Test
    @DisplayName("데이터 소스 라벨 단건 조회 성공 및 실패")
    void getLabelByIdSuccessAndEmpty() {
        // given
        given(dataSourceJpaRepository.findLabelById(1L)).willReturn(Optional.of("L1"));
        given(dataSourceJpaRepository.findLabelById(9L)).willReturn(Optional.empty());

        // when & then
        assertThat(adapter.getLabelById(1L)).contains("L1");
        assertThat(adapter.getLabelById(9L)).isEmpty();
        then(dataSourceJpaRepository).should().findLabelById(1L);
        then(dataSourceJpaRepository).should().findLabelById(9L);
    }

    @Test
    @DisplayName("데이터 소스 라벨 다건 조회 성공")
    void getLabelsByIdsSuccess() {
        // given
        DataSourceEntity e1 = DataSourceEntity.builder().id(1L).value("v1").label("L1").build();
        DataSourceEntity e2 = DataSourceEntity.builder().id(2L).value("v2").label("L2").build();
        given(dataSourceJpaRepository.findAllById(List.of(1L, 2L))).willReturn(List.of(e1, e2));

        // when
        Map<Long, String> result = adapter.getLabelsByIds(List.of(1L, 2L));

        // then
        assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
        then(dataSourceJpaRepository).should().findAllById(List.of(1L, 2L));
    }
}
