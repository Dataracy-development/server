/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DataEsProjectionTaskEntityTest {

  @Test
  @DisplayName("prePersist() 호출 시 nextRunAt이 null이면 현재 시간으로 초기화된다")
  void prePersistShouldSetNextRunAtWhenNull() {
    // given
    DataEsProjectionTaskEntity entity =
        DataEsProjectionTaskEntity.builder().nextRunAt(null).build();

    // when
    entity.prePersist();

    // then
    assertAll(
        () -> assertThat(entity.getNextRunAt()).isNotNull(),
        () -> assertThat(entity.getNextRunAt()).isBeforeOrEqualTo(LocalDateTime.now()));
  }

  @Test
  @DisplayName("prePersist() 호출 시 nextRunAt이 이미 설정되어 있으면 덮어쓰지 않는다")
  void prePersistShouldNotOverrideNextRunAtWhenAlreadySet() {
    // given
    LocalDateTime existing = LocalDateTime.now().minusDays(1);
    DataEsProjectionTaskEntity entity =
        DataEsProjectionTaskEntity.builder().nextRunAt(existing).build();

    // when
    entity.prePersist();

    // then
    assertThat(entity.getNextRunAt()).isEqualTo(existing);
  }
}
