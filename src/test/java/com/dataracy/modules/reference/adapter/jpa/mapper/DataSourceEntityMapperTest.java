package com.dataracy.modules.reference.adapter.jpa.mapper;

import com.dataracy.modules.reference.adapter.jpa.entity.DataSourceEntity;
import com.dataracy.modules.reference.domain.model.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataSourceEntityMapperTest {

    @Test
    @DisplayName("toDomain: 성공 - 엔티티에서 도메인으로 변환")
    void toDomainSuccess() {
        // given
        DataSourceEntity entity = DataSourceEntity.builder().id(1L).value("v").label("l").build();

        // when
        DataSource domain = DataSourceEntityMapper.toDomain(entity);

        // then
        assertThat(domain.id()).isEqualTo(1L);
        assertThat(domain.value()).isEqualTo("v");
        assertThat(domain.label()).isEqualTo("l");
    }

    @Test
    @DisplayName("toEntity: 성공 - 도메인에서 엔티티로 변환 (id 제외)")
    void toEntitySuccess() {
        // given
        DataSource domain = new DataSource(1L, "v", "l");

        // when
        DataSourceEntity entity = DataSourceEntityMapper.toEntity(domain);

        // then
        assertThat(entity.getId()).isNull(); // of(value,label)로 생성되어 id는 null
        assertThat(entity.getValue()).isEqualTo("v");
        assertThat(entity.getLabel()).isEqualTo("l");
    }

    @Test
    @DisplayName("null 입력 처리 - null 반환")
    void nullInputsReturnNull() {
        assertThat(DataSourceEntityMapper.toDomain(null)).isNull();
        assertThat(DataSourceEntityMapper.toEntity(null)).isNull();
    }
}
