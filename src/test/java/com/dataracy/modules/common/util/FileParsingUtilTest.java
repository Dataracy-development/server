/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.dataracy.modules.dataset.application.dto.response.metadata.ParsedMetadataResponse;

@DisplayName("FileParsingUtil 테스트")
class FileParsingUtilTest {

  @ParameterizedTest(name = "parse - {0} 파일 파싱")
  @CsvSource({
    "test.csv, 'name,age,city\nJohn,25,Seoul\nJane,30,Busan', 2, 3",
    "test.json, '[{\"name\":\"John\",\"age\":25},{\"name\":\"Jane\",\"age\":30}]', 2, 2"
  })
  @DisplayName("parse - 다양한 파일 형식 파싱")
  void parse_ShouldParseValidFiles(
      String filename, String content, int expectedRows, int expectedColumns) throws IOException {
    // Given
    ByteArrayInputStream inputStream =
        new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

    // When
    ParsedMetadataResponse result = FileParsingUtil.parse(inputStream, filename);

    // Then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.rowCount()).isEqualTo(expectedRows),
        () -> assertThat(result.columnCount()).isEqualTo(expectedColumns),
        () -> assertThat(result.previewJson()).isNotNull());
  }

  @Test
  @DisplayName("parse - null 입력 스트림 처리")
  void parse_ShouldThrowExceptionForNullInputStream() {
    // Given & When & Then
    IllegalArgumentException exception =
        catchThrowableOfType(
            () -> FileParsingUtil.parse(null, "test.csv"), IllegalArgumentException.class);
    assertAll(
        () -> assertThat(exception).isNotNull(),
        () -> assertThat(exception.getMessage()).contains("입력 스트림은 null일 수 없습니다"));
  }

  @ParameterizedTest(name = "parse - {0} 처리")
  @CsvSource({
    "null, '파일명은 null이거나 비어있을 수 없습니다'",
    "'', '파일명은 null이거나 비어있을 수 없습니다'",
    "test.txt, '지원하지 않는 파일 형식'"
  })
  @DisplayName("parse - 잘못된 입력 처리")
  void parse_ShouldThrowExceptionForInvalidInput(String filename, String expectedMessage) {
    // Given
    ByteArrayInputStream inputStream =
        new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));
    String actualFilename = "null".equals(filename) ? null : filename;

    // When & Then
    IllegalArgumentException exception =
        catchThrowableOfType(
            () -> FileParsingUtil.parse(inputStream, actualFilename),
            IllegalArgumentException.class);
    assertAll(
        () -> assertThat(exception).isNotNull(),
        () -> assertThat(exception.getMessage()).contains(expectedMessage));
  }

  @Test
  @DisplayName("parse - 대소문자 무관 파일 확장자 처리")
  void parse_ShouldHandleCaseInsensitiveExtensions() throws IOException {
    // Given
    String csvContent = "name,age\nJohn,25";
    ByteArrayInputStream inputStream =
        new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

    // When
    ParsedMetadataResponse result = FileParsingUtil.parse(inputStream, "test.CSV");

    // Then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.rowCount()).isEqualTo(1),
        () -> assertThat(result.columnCount()).isEqualTo(2));
  }

  @Test
  @DisplayName("detectEncoding - UTF-8 인코딩 감지")
  void detectEncoding_ShouldDetectUtf8() throws IOException {
    // Given
    String content = "한글 테스트 content";
    ByteArrayInputStream inputStream =
        new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

    // When
    var charset = FileParsingUtil.detectEncoding(inputStream);

    // Then
    assertAll(
        () -> assertThat(charset).isNotNull(),
        // UTF-8이 감지되거나 기본값으로 설정되어야 함
        () -> assertThat(charset.name()).isIn("UTF-8", "UTF8"));
  }

  @Test
  @DisplayName("detectEncoding - 빈 스트림 처리")
  void detectEncoding_ShouldHandleEmptyStream() throws IOException {
    // Given
    ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);

    // When
    var charset = FileParsingUtil.detectEncoding(inputStream);

    // Then
    assertAll(
        () -> assertThat(charset).isNotNull(),
        () -> assertThat(charset.name()).isIn("UTF-8", "UTF8"));
  }
}
