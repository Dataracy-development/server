/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.domain.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProjectErrorStatusTest {

  @Test
  @DisplayName("에러 코드 필드 값 검증")
  void errorStatusFields() {
    ProjectErrorStatus status = ProjectErrorStatus.NOT_FOUND_PROJECT;

    assertAll(
        () -> assertThat(status.getHttpStatus().value()).isEqualTo(404),
        () -> assertThat(status.getCode()).isEqualTo("PROJECT-002"),
        () -> assertThat(status.getMessage()).contains("존재하지 않습니다"));
  }
}
