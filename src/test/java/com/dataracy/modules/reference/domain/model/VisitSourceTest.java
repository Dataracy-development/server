/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("VisitSource 테스트")
class VisitSourceTest {

  @Test
  @DisplayName("VisitSource record 생성 및 속성 확인")
  void visitSource_ShouldCreateCorrectly() {
    // Given
    Long id = 1L;
    String value = "google";
    String label = "구글";

    // When
    VisitSource visitSource = new VisitSource(id, value, label);

    // Then
    assertAll(
        () -> assertThat(visitSource.id()).isEqualTo(id),
        () -> assertThat(visitSource.value()).isEqualTo(value),
        () -> assertThat(visitSource.label()).isEqualTo(label));
  }

  @Test
  @DisplayName("VisitSource record equals 및 hashCode 테스트")
  void visitSource_ShouldHaveCorrectEqualsAndHashCode() {
    // Given
    VisitSource visitSource1 = new VisitSource(1L, "naver", "네이버");
    VisitSource visitSource2 = new VisitSource(1L, "naver", "네이버");
    VisitSource visitSource3 = new VisitSource(2L, "naver", "네이버");

    // When & Then
    assertThat(visitSource1)
        .isEqualTo(visitSource2)
        .hasSameHashCodeAs(visitSource2)
        .isNotEqualTo(visitSource3);
  }

  @Test
  @DisplayName("VisitSource record toString 테스트")
  void visitSource_ShouldHaveCorrectToString() {
    // Given
    VisitSource visitSource = new VisitSource(1L, "direct", "직접 접속");

    // When
    String toString = visitSource.toString();

    // Then
    assertThat(toString).contains("VisitSource").contains("1").contains("direct").contains("직접 접속");
  }

  @Test
  @DisplayName("VisitSource record - null 값 처리")
  void visitSource_ShouldHandleNullValues() {
    // Given & When
    VisitSource visitSource = new VisitSource(null, null, null);

    // Then
    assertAll(
        () -> assertThat(visitSource.id()).isNull(),
        () -> assertThat(visitSource.value()).isNull(),
        () -> assertThat(visitSource.label()).isNull());
  }

  @Test
  @DisplayName("VisitSource record - 다양한 방문 소스들 테스트")
  void visitSource_ShouldHandleVariousSources() {
    // Given & When
    VisitSource visitSource1 = new VisitSource(1L, "google", "구글");
    VisitSource visitSource2 = new VisitSource(2L, "naver", "네이버");
    VisitSource visitSource3 = new VisitSource(3L, "direct", "직접 접속");

    // Then
    assertAll(
        () -> assertThat(visitSource1.value()).isEqualTo("google"),
        () -> assertThat(visitSource2.value()).isEqualTo("naver"),
        () -> assertThat(visitSource3.value()).isEqualTo("direct"),
        () -> assertThat(visitSource1.label()).isEqualTo("구글"),
        () -> assertThat(visitSource2.label()).isEqualTo("네이버"),
        () -> assertThat(visitSource3.label()).isEqualTo("직접 접속"));
  }
}
