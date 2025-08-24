package com.dataracy.modules.dataset.adapter.jpa.mapper;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataMetadataEntity;
import com.dataracy.modules.dataset.domain.model.DataMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataMetadataEntityMapperTest {

    @Test
    void toDomainShouldReturnNullWhenEntityIsNull() {
        // given
        DataMetadataEntity entity = null;

        // when
        DataMetadata result = DataMetadataEntityMapper.toDomain(entity);

        // then
        assertThat(result).isNull();
    }

    @Test
    void toDomainShouldMapCorrectly() {
        // given
        DataMetadataEntity entity = DataMetadataEntity.builder()
                .id(1L)
                .rowCount(5)
                .columnCount(6)
                .previewJson("json")
                .build();

        // when
        DataMetadata result = DataMetadataEntityMapper.toDomain(entity);

        // then
        assertThat(result.getRowCount()).isEqualTo(5);
        assertThat(result.getColumnCount()).isEqualTo(6);
        assertThat(result.getPreviewJson()).isEqualTo("json");
    }

    @Test
    void toEntityShouldReturnNullWhenDomainIsNull() {
        // given
        DataMetadata metadata = null;

        // when
        DataMetadataEntity result = DataMetadataEntityMapper.toEntity(metadata);

        // then
        assertThat(result).isNull();
    }
}
