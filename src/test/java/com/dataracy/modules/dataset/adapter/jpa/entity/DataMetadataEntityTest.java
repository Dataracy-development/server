package com.dataracy.modules.dataset.adapter.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.dataset.domain.model.DataMetadata;

class DataMetadataEntityTest {

  @Test
  @DisplayName("updateFrom() 호출 시 DataMetadata 값으로 필드가 갱신된다")
  void updateFromShouldUpdateFields() {
    // given
    DataMetadataEntity entity =
        DataMetadataEntity.builder().rowCount(1).columnCount(1).previewJson("old").build();

    DataMetadata metadata = DataMetadata.of(10L, 5, 6, "newPreview");

    // when
    entity.updateFrom(metadata);

    // then
    assertAll(
        () -> assertThat(entity.getRowCount()).isEqualTo(5),
        () -> assertThat(entity.getColumnCount()).isEqualTo(6),
        () -> assertThat(entity.getPreviewJson()).isEqualTo("newPreview"));
  }

  @Test
  @DisplayName("updateData() 호출 시 양방향 연관관계가 동기화된다")
  void updateDataShouldSyncBothSides() {
    // given
    DataEntity data = DataEntity.builder().build();
    DataMetadataEntity metadata = DataMetadataEntity.builder().build();

    // when
    metadata.updateData(data);

    // then
    assertAll(
        () -> assertThat(metadata.getData()).isEqualTo(data),
        () -> assertThat(data.getMetadata()).isEqualTo(metadata));
  }
}
