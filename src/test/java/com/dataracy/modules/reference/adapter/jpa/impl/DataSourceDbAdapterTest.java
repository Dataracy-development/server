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

    @Mock DataSourceJpaRepository datasourceJpaRepository;

    @InjectMocks DataSourceDbAdapter adapter;

    @Test
    @DisplayName("findAllDataSources: 성공 - 엔티티를 도메인으로 변환하여 반환")
    void findAllDataSources_success() {
        // given
        DataSourceEntity e1 = DataSourceEntity.builder().id(1L).value("v1").label("l1").build();
        DataSourceEntity e2 = DataSourceEntity.builder().id(2L).value("v2").label("l2").build();
        given(datasourceJpaRepository.findAll()).willReturn(List.of(e1, e2));

        // when
        List<DataSource> result = adapter.findAllDataSources();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).value()).isEqualTo("v1");
        assertThat(result.get(0).label()).isEqualTo("l1");
        then(datasourceJpaRepository).should().findAll();
    }

    @Test
    @DisplayName("findDataSourceById: 성공/실패 - 존재 시 변환, 없으면 빈 Optional")
    void findDataSourceById_success_and_empty() {
        // given
        Long id = 5L;
        DataSourceEntity entity = DataSourceEntity.builder().id(id).value("v").label("l").build();
        given(datasourceJpaRepository.findById(id)).willReturn(Optional.of(entity));
        given(datasourceJpaRepository.findById(999L)).willReturn(Optional.empty());

        // when
        Optional<DataSource> found = adapter.findDataSourceById(id);
        Optional<DataSource> missing = adapter.findDataSourceById(999L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(id);
        assertThat(missing).isEmpty();
        then(datasourceJpaRepository).should(times(1)).findById(id);
        then(datasourceJpaRepository).should(times(1)).findById(999L);
    }

    @Test
    @DisplayName("existsDataSourceById: 성공 - JPA existsById 위임")
    void existsDataSourceById_success() {
        // given
        given(datasourceJpaRepository.existsById(1L)).willReturn(true);
        given(datasourceJpaRepository.existsById(2L)).willReturn(false);

        // when/then
        assertThat(adapter.existsDataSourceById(1L)).isTrue();
        assertThat(adapter.existsDataSourceById(2L)).isFalse();
        then(datasourceJpaRepository).should().existsById(1L);
        then(datasourceJpaRepository).should().existsById(2L);
    }

    @Test
    @DisplayName("getLabelById: 성공/실패 - Optional 위임")
    void getLabelById_success_and_empty() {
        // given
        given(datasourceJpaRepository.findLabelById(1L)).willReturn(Optional.of("L1"));
        given(datasourceJpaRepository.findLabelById(9L)).willReturn(Optional.empty());

        // when/then
        assertThat(adapter.getLabelById(1L)).contains("L1");
        assertThat(adapter.getLabelById(9L)).isEmpty();
        then(datasourceJpaRepository).should().findLabelById(1L);
        then(datasourceJpaRepository).should().findLabelById(9L);
    }

    @Test
    @DisplayName("getLabelsByIds: 성공 - findAllById 결과를 id->label 맵으로 반환")
    void getLabelsByIds_success() {
        // given
        DataSourceEntity e1 = DataSourceEntity.builder().id(1L).value("v1").label("L1").build();
        DataSourceEntity e2 = DataSourceEntity.builder().id(2L).value("v2").label("L2").build();
        given(datasourceJpaRepository.findAllById(List.of(1L,2L))).willReturn(List.of(e1,e2));

        // when
        Map<Long,String> result = adapter.getLabelsByIds(List.of(1L,2L));

        // then
        assertThat(result).containsEntry(1L,"L1").containsEntry(2L,"L2");
        then(datasourceJpaRepository).should().findAllById(List.of(1L,2L));
    }
}
