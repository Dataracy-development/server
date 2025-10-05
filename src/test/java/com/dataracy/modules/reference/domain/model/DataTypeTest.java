package com.dataracy.modules.reference.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DataType 테스트")
class DataTypeTest {

  // Test constants
  private static final Integer CURRENT_YEAR = 2024;

  @Test
  @DisplayName("DataType record 생성 및 속성 확인")
  void dataTypeShouldCreateCorrectly() {
    // Given
    Long id = 1L;
    String value = "csv";
    String label = "CSV 파일";

    // When
    DataType dataType = new DataType(id, value, label);

    // Then
    assertAll(
        () -> assertThat(dataType.id()).isEqualTo(id),
        () -> assertThat(dataType.value()).isEqualTo(value),
        () -> assertThat(dataType.label()).isEqualTo(label));
  }

  @Test
  @DisplayName("DataType record equals 및 hashCode 테스트")
  void dataTypeShouldHaveCorrectEqualsAndHashCode() {
    // Given
    DataType dataType1 = new DataType(1L, "json", "JSON 파일");
    DataType dataType2 = new DataType(1L, "json", "JSON 파일");
    DataType dataType3 = new DataType(2L, "json", "JSON 파일");

    // When & Then
    assertThat(dataType1).isEqualTo(dataType2).hasSameHashCodeAs(dataType2).isNotEqualTo(dataType3);
  }

  @Test
  @DisplayName("DataType record toString 테스트")
  void dataTypeShouldHaveCorrectToString() {
    // Given
    DataType dataType = new DataType(1L, "xml", "XML 파일");

    // When
    String toString = dataType.toString();

    // Then
    assertThat(toString).contains("DataType").contains("1").contains("xml").contains("XML 파일");
  }

  @Test
  @DisplayName("DataType record - null 값 처리")
  void dataTypeShouldHandleNullValues() {
    // Given & When
    DataType dataType = new DataType(null, null, null);

    // Then
    assertAll(
        () -> assertThat(dataType.id()).isNull(),
        () -> assertThat(dataType.value()).isNull(),
        () -> assertThat(dataType.label()).isNull());
  }

  @Test
  @DisplayName("DataType record - 다양한 값들 테스트")
  void dataTypeShouldHandleVariousValues() {
    // Given & When
    DataType dataType1 = new DataType(1L, "csv", "CSV 파일");
    DataType dataType2 = new DataType(2L, "json", "JSON 파일");
    DataType dataType3 = new DataType(3L, "xlsx", "엑셀 파일");

    // Then
    assertAll(
        () -> assertThat(dataType1.value()).isEqualTo("csv"),
        () -> assertThat(dataType2.value()).isEqualTo("json"),
        () -> assertThat(dataType3.value()).isEqualTo("xlsx"),
        () -> assertThat(dataType1.label()).isEqualTo("CSV 파일"),
        () -> assertThat(dataType2.label()).isEqualTo("JSON 파일"),
        () -> assertThat(dataType3.label()).isEqualTo("엑셀 파일"));
  }
}
