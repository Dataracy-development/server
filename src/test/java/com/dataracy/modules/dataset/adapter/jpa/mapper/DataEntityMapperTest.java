package com.dataracy.modules.dataset.adapter.jpa.mapper;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.domain.model.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataEntityMapperTest {

    @Test
    @DisplayName("toDomain - entity가 null이면 null 반환")
    void toDomainShouldReturnNullWhenEntityIsNull() {
        // given
        DataEntity entity = null;

        // when
        Data result = DataEntityMapper.toDomain(entity);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toDomain - entity → domain 매핑 성공")
    void toDomainShouldMapCorrectly() {
        // given
        DataEntity entity = DataEntity.builder()
                .id(1L)
                .title("title")
                .userId(99L)
                .description("desc")
                .analysisGuide("guide")
                .downloadCount(3)
                .build();

        // when
        Data result = DataEntityMapper.toDomain(entity);

        // then
        assertThat(result)
                .extracting("id", "title", "userId", "description", "analysisGuide", "downloadCount")
                .containsExactly(1L, "title", 99L, "desc", "guide", 3);
    }

    @Test
    @DisplayName("toEntity - domain이 null이면 null 반환")
    void toEntityShouldReturnNullWhenDomainIsNull() {
        // given
        Data data = null;

        // when
        DataEntity result = DataEntityMapper.toEntity(data);

        // then
        assertThat(result).isNull();
    }
}
