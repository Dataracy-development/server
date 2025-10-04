package com.dataracy.modules.reference.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AuthorLevel 테스트")
class AuthorLevelTest {

  // Test constants
  private static final Integer CURRENT_YEAR = 2024;

  @Test
  @DisplayName("AuthorLevel record 생성 및 속성 확인")
  void authorLevelShouldCreateCorrectly() {
    // Given
    Long id = 1L;
    String value = "beginner";
    String label = "초급자";

    // When
    AuthorLevel authorLevel = new AuthorLevel(id, value, label);

    // Then
    assertAll(
        () -> assertThat(authorLevel.id()).isEqualTo(id),
        () -> assertThat(authorLevel.value()).isEqualTo(value),
        () -> assertThat(authorLevel.label()).isEqualTo(label));
  }

  @Test
  @DisplayName("AuthorLevel record equals 및 hashCode 테스트")
  void authorLevelShouldHaveCorrectEqualsAndHashCode() {
    // Given
    AuthorLevel authorLevel1 = new AuthorLevel(1L, "expert", "전문가");
    AuthorLevel authorLevel2 = new AuthorLevel(1L, "expert", "전문가");
    AuthorLevel authorLevel3 = new AuthorLevel(2L, "expert", "전문가");

    // When & Then
    assertThat(authorLevel1)
        .isEqualTo(authorLevel2)
        .hasSameHashCodeAs(authorLevel2)
        .isNotEqualTo(authorLevel3);
  }

  @Test
  @DisplayName("AuthorLevel record toString 테스트")
  void authorLevelShouldHaveCorrectToString() {
    // Given
    AuthorLevel authorLevel = new AuthorLevel(1L, "intermediate", "중급자");

    // When
    String toString = authorLevel.toString();

    // Then
    assertThat(toString)
        .contains("AuthorLevel")
        .contains("1")
        .contains("intermediate")
        .contains("중급자");
  }

  @Test
  @DisplayName("AuthorLevel record - null 값 처리")
  void authorLevelShouldHandleNullValues() {
    // Given & When
    AuthorLevel authorLevel = new AuthorLevel(null, null, null);

    // Then
    assertAll(
        () -> assertThat(authorLevel.id()).isNull(),
        () -> assertThat(authorLevel.value()).isNull(),
        () -> assertThat(authorLevel.label()).isNull());
  }

  @Test
  @DisplayName("AuthorLevel record - 다양한 레벨들 테스트")
  void authorLevelShouldHandleVariousLevels() {
    // Given & When
    AuthorLevel authorLevel1 = new AuthorLevel(1L, "beginner", "초급자");
    AuthorLevel authorLevel2 = new AuthorLevel(2L, "intermediate", "중급자");
    AuthorLevel authorLevel3 = new AuthorLevel(3L, "expert", "전문가");

    // Then
    assertAll(
        () -> assertThat(authorLevel1.value()).isEqualTo("beginner"),
        () -> assertThat(authorLevel2.value()).isEqualTo("intermediate"),
        () -> assertThat(authorLevel3.value()).isEqualTo("expert"),
        () -> assertThat(authorLevel1.label()).isEqualTo("초급자"),
        () -> assertThat(authorLevel2.label()).isEqualTo("중급자"),
        () -> assertThat(authorLevel3.label()).isEqualTo("전문가"));
  }
}
