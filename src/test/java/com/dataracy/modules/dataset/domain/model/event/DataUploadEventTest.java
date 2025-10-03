/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.domain.model.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DataUploadEventTest {

  @Test
  @DisplayName("DataUploadEvent 생성 시 모든 필드가 정상적으로 초기화된다")
  void shouldCreateEventWithAllFields() {
    // when
    DataUploadEvent event = new DataUploadEvent(1L, "url", "file.csv");

    // then
    assertAll(
        () -> assertThat(event.getDataId()).isEqualTo(1L),
        () -> assertThat(event.getDataFileUrl()).isEqualTo("url"),
        () -> assertThat(event.getOriginalFilename()).isEqualTo("file.csv"));
  }
}
