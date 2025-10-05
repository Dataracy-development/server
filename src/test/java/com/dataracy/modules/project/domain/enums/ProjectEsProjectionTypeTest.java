package com.dataracy.modules.project.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProjectEsProjectionType 테스트")
class ProjectEsProjectionTypeTest {

  @Test
  @DisplayName("ProjectEsProjectionType enum 값들 확인")
  void projectEsProjectionTypeShouldHaveCorrectValues() {
    // Given & When & Then
    assertAll(
        () -> assertThat(ProjectEsProjectionType.values()).hasSize(2),
        () -> assertThat(ProjectEsProjectionType.PENDING).isNotNull(),
        () -> assertThat(ProjectEsProjectionType.RETRYING).isNotNull());
  }

  @Test
  @DisplayName("ProjectEsProjectionType name() 메서드 테스트")
  void projectEsProjectionTypeShouldHaveCorrectNames() {
    // Given & When & Then
    assertAll(
        () -> assertThat(ProjectEsProjectionType.PENDING.name()).isEqualTo("PENDING"),
        () -> assertThat(ProjectEsProjectionType.RETRYING.name()).isEqualTo("RETRYING"));
  }

  @Test
  @DisplayName("ProjectEsProjectionType valueOf() 메서드 테스트")
  void projectEsProjectionTypeShouldParseFromString() {
    // Given & When & Then
    assertAll(
        () ->
            assertThat(ProjectEsProjectionType.valueOf("PENDING"))
                .isEqualTo(ProjectEsProjectionType.PENDING),
        () ->
            assertThat(ProjectEsProjectionType.valueOf("RETRYING"))
                .isEqualTo(ProjectEsProjectionType.RETRYING));
  }

  @Test
  @DisplayName("ProjectEsProjectionType ordinal() 메서드 테스트")
  void projectEsProjectionTypeShouldHaveCorrectOrdinals() {
    // Given & When & Then
    assertAll(
        () -> assertThat(ProjectEsProjectionType.PENDING.ordinal()).isZero(),
        () -> assertThat(ProjectEsProjectionType.RETRYING.ordinal()).isEqualTo(1));
  }

  @Test
  @DisplayName("ProjectEsProjectionType toString() 메서드 테스트")
  void projectEsProjectionTypeShouldHaveCorrectToString() {
    // Given & When & Then
    assertAll(
        () -> assertThat(ProjectEsProjectionType.PENDING).hasToString("PENDING"),
        () -> assertThat(ProjectEsProjectionType.RETRYING).hasToString("RETRYING"));
  }
}
