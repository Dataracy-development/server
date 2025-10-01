package com.dataracy.modules.dataset.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DataMetadataTest {

    @Test
    @DisplayName("of() 정적 팩토리 메서드로 DataMetadata 객체가 올바르게 생성된다")
    void ofShouldBuildDataMetadataCorrectly() {
        // given & when
        DataMetadata metadata = DataMetadata.of(
                1L,
                100,
                10,
                "{\"sample\":true}"
        );

        // then
        assertAll(
                () -> assertThat(metadata.getId()).isEqualTo(1L),
                () -> assertThat(metadata.getRowCount()).isEqualTo(100),
                () -> assertThat(metadata.getColumnCount()).isEqualTo(10),
                () -> assertThat(metadata.getPreviewJson()).isEqualTo("{\"sample\":true}")
        );
    }
}
