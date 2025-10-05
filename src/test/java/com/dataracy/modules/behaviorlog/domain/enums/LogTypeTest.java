package com.dataracy.modules.behaviorlog.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LogType 테스트")
class LogTypeTest {

  @Test
  @DisplayName("LogType enum 값들 확인")
  void logTypeShouldHaveCorrectValues() {
    // Given & When & Then
    assertThat(LogType.values()).hasSize(2);

    assertAll(
        () -> assertThat(LogType.ACTION).isNotNull(), () -> assertThat(LogType.ERROR).isNotNull());
  }

  @Test
  @DisplayName("LogType name() 메서드 테스트")
  void logTypeShouldHaveCorrectNames() {
    // Given & When & Then
    assertAll(
        () -> assertThat(LogType.ACTION.name()).isEqualTo("ACTION"),
        () -> assertThat(LogType.ERROR.name()).isEqualTo("ERROR"));
  }

  @Test
  @DisplayName("LogType valueOf() 메서드 테스트")
  void logTypeShouldParseFromString() {
    // Given & When & Then
    assertAll(
        () -> assertThat(LogType.valueOf("ACTION")).isEqualTo(LogType.ACTION),
        () -> assertThat(LogType.valueOf("ERROR")).isEqualTo(LogType.ERROR));
  }

  @Test
  @DisplayName("LogType ordinal() 메서드 테스트")
  void logTypeShouldHaveCorrectOrdinals() {
    // Given & When & Then
    assertAll(
        () -> assertThat(LogType.ACTION.ordinal()).isZero(),
        () -> assertThat(LogType.ERROR.ordinal()).isEqualTo(1));
  }

  @Test
  @DisplayName("LogType toString() 메서드 테스트")
  void logTypeShouldHaveCorrectToString() {
    // Given & When & Then
    assertAll(
        () -> assertThat(LogType.ACTION).hasToString("ACTION"),
        () -> assertThat(LogType.ERROR).hasToString("ERROR"));
  }

  @Test
  @DisplayName("LogType description 테스트")
  void logTypeShouldHaveCorrectDescriptions() {
    // Given & When & Then
    assertAll(
        () -> assertThat(LogType.ACTION.getDescription()).isEqualTo("행동"),
        () -> assertThat(LogType.ERROR.getDescription()).isEqualTo("에러"));
  }
}
