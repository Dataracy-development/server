package com.dataracy.modules.dataset.adapter.jpa.entity;

import com.dataracy.modules.dataset.domain.model.DataMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataMetadataEntityTest {

    @Test
    void updateFromShouldUpdateFields() {
        // given
        DataMetadataEntity entity = DataMetadataEntity.builder()
                .rowCount(1)
                .columnCount(1)
                .previewJson("old")
                .build();

        DataMetadata metadata = DataMetadata.of(10L, 5, 6, "newPreview");

        // when
        entity.updateFrom(metadata);

        // then
        assertThat(entity.getRowCount()).isEqualTo(5);
        assertThat(entity.getColumnCount()).isEqualTo(6);
        assertThat(entity.getPreviewJson()).isEqualTo("newPreview");
    }

    @Test
    void updateDataShouldSyncBothSides() {
        // given
        DataEntity data = DataEntity.builder().build();
        DataMetadataEntity metadata = DataMetadataEntity.builder().build();

        // when
        metadata.updateData(data);

        // then
        assertThat(metadata.getData()).isEqualTo(data);
        assertThat(data.getMetadata()).isEqualTo(metadata);
    }
}
