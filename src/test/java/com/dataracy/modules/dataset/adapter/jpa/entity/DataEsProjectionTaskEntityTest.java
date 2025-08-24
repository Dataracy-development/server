package com.dataracy.modules.dataset.adapter.jpa.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DataEsProjectionTaskEntityTest {

    @Test
    void prePersistShouldSetNextRunAtWhenNull() {
        // given
        DataEsProjectionTaskEntity entity = DataEsProjectionTaskEntity.builder().nextRunAt(null).build();

        // when
        entity.prePersist();

        // then
        assertThat(entity.getNextRunAt()).isNotNull();
        assertThat(entity.getNextRunAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void prePersistShouldNotOverrideNextRunAtWhenAlreadySet() {
        // given
        LocalDateTime existing = LocalDateTime.now().minusDays(1);
        DataEsProjectionTaskEntity entity = DataEsProjectionTaskEntity.builder().nextRunAt(existing).build();

        // when
        entity.prePersist();

        // then
        assertThat(entity.getNextRunAt()).isEqualTo(existing);
    }
}
