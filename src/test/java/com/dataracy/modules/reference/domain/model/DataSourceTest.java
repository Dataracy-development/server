/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DataSource 테스트")
class DataSourceTest {

  @Test
  @DisplayName("DataSource record 생성 및 속성 확인")
  void dataSource_ShouldCreateCorrectly() {
    // Given
    Long id = 1L;
    String value = "government";
    String label = "정부 데이터";

    // When
    DataSource dataSource = new DataSource(id, value, label);

    // Then
    assertAll(
        () -> assertThat(dataSource.id()).isEqualTo(id),
        () -> assertThat(dataSource.value()).isEqualTo(value),
        () -> assertThat(dataSource.label()).isEqualTo(label));
  }

  @Test
  @DisplayName("DataSource record equals 및 hashCode 테스트")
  void dataSource_ShouldHaveCorrectEqualsAndHashCode() {
    // Given
    DataSource dataSource1 = new DataSource(1L, "corporate", "기업 데이터");
    DataSource dataSource2 = new DataSource(1L, "corporate", "기업 데이터");
    DataSource dataSource3 = new DataSource(2L, "corporate", "기업 데이터");

    // When & Then
    assertThat(dataSource1)
        .isEqualTo(dataSource2)
        .hasSameHashCodeAs(dataSource2)
        .isNotEqualTo(dataSource3);
  }

  @Test
  @DisplayName("DataSource record toString 테스트")
  void dataSource_ShouldHaveCorrectToString() {
    // Given
    DataSource dataSource = new DataSource(1L, "research", "연구 데이터");

    // When
    String toString = dataSource.toString();

    // Then
    assertThat(toString)
        .contains("DataSource")
        .contains("1")
        .contains("research")
        .contains("연구 데이터");
  }

  @Test
  @DisplayName("DataSource record - null 값 처리")
  void dataSource_ShouldHandleNullValues() {
    // Given & When
    DataSource dataSource = new DataSource(null, null, null);

    // Then
    assertAll(
        () -> assertThat(dataSource.id()).isNull(),
        () -> assertThat(dataSource.value()).isNull(),
        () -> assertThat(dataSource.label()).isNull());
  }

  @Test
  @DisplayName("DataSource record - 다양한 값들 테스트")
  void dataSource_ShouldHandleVariousValues() {
    // Given & When
    DataSource dataSource1 = new DataSource(1L, "government", "정부 데이터");
    DataSource dataSource2 = new DataSource(2L, "corporate", "기업 데이터");
    DataSource dataSource3 = new DataSource(3L, "research", "연구 데이터");

    // Then
    assertAll(
        () -> assertThat(dataSource1.value()).isEqualTo("government"),
        () -> assertThat(dataSource2.value()).isEqualTo("corporate"),
        () -> assertThat(dataSource3.value()).isEqualTo("research"),
        () -> assertThat(dataSource1.label()).isEqualTo("정부 데이터"),
        () -> assertThat(dataSource2.label()).isEqualTo("기업 데이터"),
        () -> assertThat(dataSource3.label()).isEqualTo("연구 데이터"));
  }
}
