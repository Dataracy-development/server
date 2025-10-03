/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.project.domain.enums.ProjectEsProjectionType;

class ProjectEsProjectionTaskEntityTest {

  @Test
  @DisplayName("빌더로 생성 시 기본값이 적용된다")
  void builderShouldApplyDefaults() {
    // when
    ProjectEsProjectionTaskEntity entity =
        ProjectEsProjectionTaskEntity.builder().projectId(1L).build();

    // then
    assertAll(
        () -> assertThat(entity.getProjectId()).isEqualTo(1L),
        () -> assertThat(entity.getDeltaComment()).isZero(),
        () -> assertThat(entity.getDeltaLike()).isZero(),
        () -> assertThat(entity.getDeltaView()).isZero(),
        () -> assertThat(entity.getSetDeleted()).isFalse(),
        () -> assertThat(entity.getStatus()).isEqualTo(ProjectEsProjectionType.PENDING),
        () -> assertThat(entity.getRetryCount()).isZero(),
        () -> assertThat(entity.getNextRunAt()).isNull(),
        () -> assertThat(entity.getLastError()).isNull());
  }

  @Test
  @DisplayName("@PrePersist 실행 시 nextRunAt 이 null이면 현재 시간으로 채워진다")
  void prePersistShouldInitializeNextRunAtWhenNull() {
    // given
    ProjectEsProjectionTaskEntity entity =
        ProjectEsProjectionTaskEntity.builder().projectId(2L).nextRunAt(null).build();

    // when
    entity.prePersist();

    // then
    assertAll(
        () -> assertThat(entity.getNextRunAt()).isNotNull(),
        () -> assertThat(entity.getNextRunAt()).isBeforeOrEqualTo(LocalDateTime.now()));
  }

  @Test
  @DisplayName("@PrePersist 실행 시 nextRunAt 이 이미 존재하면 유지된다")
  void prePersistShouldKeepNextRunAtWhenAlreadySet() {
    // given
    LocalDateTime scheduled = LocalDateTime.now().minusDays(1);
    ProjectEsProjectionTaskEntity entity =
        ProjectEsProjectionTaskEntity.builder().projectId(3L).nextRunAt(scheduled).build();

    // when
    entity.prePersist();

    // then
    assertThat(entity.getNextRunAt()).isEqualTo(scheduled);
  }

  @Test
  @DisplayName("setter 호출 시 값이 정상적으로 변경된다")
  void settersShouldUpdateValues() {
    // given
    ProjectEsProjectionTaskEntity entity = new ProjectEsProjectionTaskEntity();

    // when
    entity.setProjectId(10L);
    entity.setDeltaComment(5);
    entity.setDeltaLike(7);
    entity.setDeltaView(20L);
    entity.setSetDeleted(true);
    entity.setStatus(ProjectEsProjectionType.PENDING);
    entity.setRetryCount(3);
    entity.setNextRunAt(LocalDateTime.of(2023, 1, 1, 0, 0));
    entity.setLastError("error");

    // then
    assertAll(
        () -> assertThat(entity.getProjectId()).isEqualTo(10L),
        () -> assertThat(entity.getDeltaComment()).isEqualTo(5),
        () -> assertThat(entity.getDeltaLike()).isEqualTo(7),
        () -> assertThat(entity.getDeltaView()).isEqualTo(20L),
        () -> assertThat(entity.getSetDeleted()).isTrue(),
        () -> assertThat(entity.getStatus()).isEqualTo(ProjectEsProjectionType.PENDING),
        () -> assertThat(entity.getRetryCount()).isEqualTo(3),
        () -> assertThat(entity.getNextRunAt()).isEqualTo(LocalDateTime.of(2023, 1, 1, 0, 0)),
        () -> assertThat(entity.getLastError()).isEqualTo("error"));
  }

  @Test
  @DisplayName("올아규먼트 생성자로 모든 값이 초기화된다")
  void allArgsConstructorShouldInitializeFields() {
    // given
    LocalDateTime now = LocalDateTime.now();

    // when
    ProjectEsProjectionTaskEntity entity =
        new ProjectEsProjectionTaskEntity(
            1L, // id
            2L, // projectId
            3, // deltaComment
            4, // deltaLike
            5L, // deltaView
            true, // setDeleted
            ProjectEsProjectionType.PENDING, // status
            7, // retryCount
            now, // nextRunAt
            "error" // lastError
            );

    // then
    assertAll(
        () -> assertThat(entity.getId()).isEqualTo(1L),
        () -> assertThat(entity.getProjectId()).isEqualTo(2L),
        () -> assertThat(entity.getDeltaComment()).isEqualTo(3),
        () -> assertThat(entity.getDeltaLike()).isEqualTo(4),
        () -> assertThat(entity.getDeltaView()).isEqualTo(5L),
        () -> assertThat(entity.getSetDeleted()).isTrue(),
        () -> assertThat(entity.getStatus()).isEqualTo(ProjectEsProjectionType.PENDING),
        () -> assertThat(entity.getRetryCount()).isEqualTo(7),
        () -> assertThat(entity.getNextRunAt()).isEqualTo(now),
        () -> assertThat(entity.getLastError()).isEqualTo("error"));
  }
}
