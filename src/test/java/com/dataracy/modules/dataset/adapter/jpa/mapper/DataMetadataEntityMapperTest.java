/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.jpa.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataMetadataEntity;
import com.dataracy.modules.dataset.domain.model.DataMetadata;

class DataMetadataEntityMapperTest {

  @Test
  @DisplayName("toDomain - entity가 null이면 null 반환")
  void toDomainShouldReturnNullWhenEntityIsNull() {
    // given
    DataMetadataEntity entity = null;

    // when
    DataMetadata result = DataMetadataEntityMapper.toDomain(entity);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("toDomain - entity → domain 매핑 성공")
  void toDomainShouldMapCorrectly() {
    // given
    DataMetadataEntity entity =
        DataMetadataEntity.builder().id(1L).rowCount(5).columnCount(6).previewJson("json").build();

    // when
    DataMetadata result = DataMetadataEntityMapper.toDomain(entity);

    // then
    assertAll(
        () -> assertThat(result.getRowCount()).isEqualTo(5),
        () -> assertThat(result.getColumnCount()).isEqualTo(6),
        () -> assertThat(result.getPreviewJson()).isEqualTo("json"));
  }

  @Test
  @DisplayName("toEntity - domain이 null이면 null 반환")
  void toEntityShouldReturnNullWhenDomainIsNull() {
    // given
    DataMetadata metadata = null;

    // when
    DataMetadataEntity result = DataMetadataEntityMapper.toEntity(metadata);

    // then
    assertThat(result).isNull();
  }
}
