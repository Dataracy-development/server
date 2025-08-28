package com.dataracy.modules.reference.adapter.jpa.mapper;

import com.dataracy.modules.reference.adapter.jpa.entity.DataTypeEntity;
import com.dataracy.modules.reference.domain.model.DataType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataTypeEntityMapperTest {

    @Test
    @DisplayName("toDomain: 성공 - 엔티티에서 도메인으로 변환")
    void toDomainSuccess() {
        // given
        DataTypeEntity entity = DataTypeEntity.builder().id(1L).value("v").label("l").build();

        // when
        DataType domain = DataTypeEntityMapper.toDomain(entity);

        // then
        assertThat(domain.id()).isEqualTo(1L);
        assertThat(domain.value()).isEqualTo("v");
        assertThat(domain.label()).isEqualTo("l");
    }

    @Test
    @DisplayName("toEntity: 성공 - 도메인에서 엔티티로 변환 (id 제외)")
    void toEntitySuccess() {
        // given
        DataType domain = new DataType(1L, "v", "l");

        // when
        DataTypeEntity entity = DataTypeEntityMapper.toEntity(domain);

        // then
        assertThat(entity.getId()).isNull(); // of(value,label)로 생성되어 id는 null
        assertThat(entity.getValue()).isEqualTo("v");
        assertThat(entity.getLabel()).isEqualTo("l");
    }

    @Test
    @DisplayName("null 입력 처리 - null 반환")
    void nullInputsReturnNull() {
        assertThat(DataTypeEntityMapper.toDomain(null)).isNull();
        assertThat(DataTypeEntityMapper.toEntity(null)).isNull();
    }
}
