/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.behaviorlog.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ActionType 테스트")
class ActionTypeTest {

  @Test
  @DisplayName("ActionType enum 값들 확인")
  void actionType_ShouldHaveCorrectValues() {
    // Given & When & Then
    assertThat(ActionType.values()).hasSize(3);

    assertAll(
        () -> assertThat(ActionType.CLICK).isNotNull(),
        () -> assertThat(ActionType.NAVIGATION).isNotNull(),
        () -> assertThat(ActionType.OTHER).isNotNull());
  }

  @Test
  @DisplayName("ActionType name() 메서드 테스트")
  void actionType_ShouldHaveCorrectNames() {
    // Given & When & Then
    assertAll(
        () -> assertThat(ActionType.CLICK.name()).isEqualTo("CLICK"),
        () -> assertThat(ActionType.NAVIGATION.name()).isEqualTo("NAVIGATION"),
        () -> assertThat(ActionType.OTHER.name()).isEqualTo("OTHER"));
  }

  @Test
  @DisplayName("ActionType valueOf() 메서드 테스트")
  void actionType_ShouldParseFromString() {
    // Given & When & Then
    assertAll(
        () -> assertThat(ActionType.valueOf("CLICK")).isEqualTo(ActionType.CLICK),
        () -> assertThat(ActionType.valueOf("NAVIGATION")).isEqualTo(ActionType.NAVIGATION),
        () -> assertThat(ActionType.valueOf("OTHER")).isEqualTo(ActionType.OTHER));
  }

  @Test
  @DisplayName("ActionType ordinal() 메서드 테스트")
  void actionType_ShouldHaveCorrectOrdinals() {
    // Given & When & Then
    assertAll(
        () -> assertThat(ActionType.CLICK.ordinal()).isZero(),
        () -> assertThat(ActionType.NAVIGATION.ordinal()).isEqualTo(1),
        () -> assertThat(ActionType.OTHER.ordinal()).isEqualTo(2));
  }

  @Test
  @DisplayName("ActionType toString() 메서드 테스트")
  void actionType_ShouldHaveCorrectToString() {
    // Given & When & Then
    assertAll(
        () -> assertThat(ActionType.CLICK).hasToString("CLICK"),
        () -> assertThat(ActionType.NAVIGATION).hasToString("NAVIGATION"),
        () -> assertThat(ActionType.OTHER).hasToString("OTHER"));
  }

  @Test
  @DisplayName("ActionType description 테스트")
  void actionType_ShouldHaveCorrectDescriptions() {
    // Given & When & Then
    assertAll(
        () -> assertThat(ActionType.CLICK.getDescription()).isEqualTo("클릭"),
        () -> assertThat(ActionType.NAVIGATION.getDescription()).isEqualTo("이동"),
        () -> assertThat(ActionType.OTHER.getDescription()).isEqualTo("기타"));
  }

  @Test
  @DisplayName("ActionType fromNullableString() 메서드 테스트")
  void actionType_ShouldParseFromNullableString() {
    // Given & When & Then
    assertAll(
        () -> assertThat(ActionType.fromNullableString("CLICK")).isEqualTo(ActionType.CLICK),
        () ->
            assertThat(ActionType.fromNullableString("NAVIGATION"))
                .isEqualTo(ActionType.NAVIGATION),
        () -> assertThat(ActionType.fromNullableString("OTHER")).isEqualTo(ActionType.OTHER),
        () -> assertThat(ActionType.fromNullableString("UNKNOWN")).isEqualTo(ActionType.OTHER),
        () -> assertThat(ActionType.fromNullableString(null)).isEqualTo(ActionType.OTHER),
        () -> assertThat(ActionType.fromNullableString("")).isEqualTo(ActionType.OTHER),
        () -> assertThat(ActionType.fromNullableString("   ")).isEqualTo(ActionType.OTHER));
  }
}
