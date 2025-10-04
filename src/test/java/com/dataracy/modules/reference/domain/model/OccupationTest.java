package com.dataracy.modules.reference.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Occupation 테스트")
class OccupationTest {

  // Test constants
  private static final Integer CURRENT_YEAR = 2024;

  @Test
  @DisplayName("Occupation record 생성 및 속성 확인")
  void occupationShouldCreateCorrectly() {
    // Given
    Long id = 1L;
    String value = "developer";
    String label = "개발자";

    // When
    Occupation occupation = new Occupation(id, value, label);

    // Then
    assertAll(
        () -> assertThat(occupation.id()).isEqualTo(id),
        () -> assertThat(occupation.value()).isEqualTo(value),
        () -> assertThat(occupation.label()).isEqualTo(label));
  }

  @Test
  @DisplayName("Occupation record equals 및 hashCode 테스트")
  void occupationShouldHaveCorrectEqualsAndHashCode() {
    // Given
    Occupation occupation1 = new Occupation(1L, "designer", "디자이너");
    Occupation occupation2 = new Occupation(1L, "designer", "디자이너");
    Occupation occupation3 = new Occupation(2L, "designer", "디자이너");

    // When & Then
    assertThat(occupation1)
        .isEqualTo(occupation2)
        .hasSameHashCodeAs(occupation2)
        .isNotEqualTo(occupation3);
  }

  @Test
  @DisplayName("Occupation record toString 테스트")
  void occupationShouldHaveCorrectToString() {
    // Given
    Occupation occupation = new Occupation(1L, "analyst", "분석가");

    // When
    String toString = occupation.toString();

    // Then
    assertThat(toString).contains("Occupation").contains("1").contains("analyst").contains("분석가");
  }

  @Test
  @DisplayName("Occupation record - null 값 처리")
  void occupationShouldHandleNullValues() {
    // Given & When
    Occupation occupation = new Occupation(null, null, null);

    // Then
    assertAll(
        () -> assertThat(occupation.id()).isNull(),
        () -> assertThat(occupation.value()).isNull(),
        () -> assertThat(occupation.label()).isNull());
  }

  @Test
  @DisplayName("Occupation record - 다양한 직업들 테스트")
  void occupationShouldHandleVariousOccupations() {
    // Given & When
    Occupation occupation1 = new Occupation(1L, "developer", "개발자");
    Occupation occupation2 = new Occupation(2L, "designer", "디자이너");
    Occupation occupation3 = new Occupation(3L, "analyst", "분석가");

    // Then
    assertAll(
        () -> assertThat(occupation1.value()).isEqualTo("developer"),
        () -> assertThat(occupation2.value()).isEqualTo("designer"),
        () -> assertThat(occupation3.value()).isEqualTo("analyst"),
        () -> assertThat(occupation1.label()).isEqualTo("개발자"),
        () -> assertThat(occupation2.label()).isEqualTo("디자이너"),
        () -> assertThat(occupation3.label()).isEqualTo("분석가"));
  }
}
