package com.dataracy.modules.dataset.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataMetadataTest {

    @Test
    void ofShouldBuildDataMetadataCorrectly() {
        DataMetadata metadata = DataMetadata.of(1L, 100, 10, "{\"sample\":true}");

        assertThat(metadata.getId()).isEqualTo(1L);
        assertThat(metadata.getRowCount()).isEqualTo(100);
        assertThat(metadata.getColumnCount()).isEqualTo(10);
        assertThat(metadata.getPreviewJson()).isEqualTo("{\"sample\":true}");
    }
}
