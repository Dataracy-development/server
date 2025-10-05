package com.dataracy.modules.project.adapter.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProjectEsProjectionDlqEntityTest {

  @Test
  @DisplayName("성공 → 빌더로 생성 시 기본값이 적용된다")
  void builderDefaultValues() {
    // given & when
    ProjectEsProjectionDlqEntity entity =
        ProjectEsProjectionDlqEntity.builder().projectId(1L).build();

    // then
    assertAll(
        () -> assertThat(entity.getDeltaComment()).isZero(),
        () -> assertThat(entity.getDeltaLike()).isZero(),
        () -> assertThat(entity.getDeltaView()).isZero(),
        () -> assertThat(entity.getSetDeleted()).isFalse(),
        () -> assertThat(entity.getLastError()).isNull());
  }

  @Test
  @DisplayName("성공 → 모든 필드가 정상적으로 저장 및 조회된다")
  void allArgsConstructorAndGetter() {
    // given
    ProjectEsProjectionDlqEntity entity =
        new ProjectEsProjectionDlqEntity(
            10L, // id
            20L, // projectId
            2, // deltaComment
            3, // deltaLike
            5L, // deltaView
            true, // setDeleted
            "error-message" // lastError
            );

    // then
    assertAll(
        () -> assertThat(entity.getId()).isEqualTo(10L),
        () -> assertThat(entity.getProjectId()).isEqualTo(20L),
        () -> assertThat(entity.getDeltaComment()).isEqualTo(2),
        () -> assertThat(entity.getDeltaLike()).isEqualTo(3),
        () -> assertThat(entity.getDeltaView()).isEqualTo(5L),
        () -> assertThat(entity.getSetDeleted()).isTrue(),
        () -> assertThat(entity.getLastError()).isEqualTo("error-message"));
  }

  @Test
  @DisplayName("성공 → 세터로 값 변경이 가능하다")
  void setterUpdatesValues() {
    // given
    ProjectEsProjectionDlqEntity entity =
        ProjectEsProjectionDlqEntity.builder().projectId(30L).build();

    // when
    entity.setDeltaComment(7);
    entity.setDeltaLike(8);
    entity.setDeltaView(15L);
    entity.setSetDeleted(true);
    entity.setLastError("new-error");

    // then
    assertAll(
        () -> assertThat(entity.getDeltaComment()).isEqualTo(7),
        () -> assertThat(entity.getDeltaLike()).isEqualTo(8),
        () -> assertThat(entity.getDeltaView()).isEqualTo(15L),
        () -> assertThat(entity.getSetDeleted()).isTrue(),
        () -> assertThat(entity.getLastError()).isEqualTo("new-error"));
  }
}
