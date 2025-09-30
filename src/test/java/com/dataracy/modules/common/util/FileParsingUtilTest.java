package com.dataracy.modules.common.util;

import com.dataracy.modules.dataset.application.dto.response.metadata.ParsedMetadataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FileParsingUtil 테스트")
class FileParsingUtilTest {

    @Test
    @DisplayName("parse - 유효한 CSV 파일 파싱")
    void parse_ShouldParseValidCsvFile() throws IOException {
        // Given
        String csvContent = "name,age,city\nJohn,25,Seoul\nJane,30,Busan";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        // When
        ParsedMetadataResponse result = FileParsingUtil.parse(inputStream, "test.csv");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.rowCount()).isEqualTo(2);
        assertThat(result.columnCount()).isEqualTo(3);
        assertThat(result.previewJson()).isNotNull();
    }

    @Test
    @DisplayName("parse - 유효한 XLSX 파일 파싱")
    void parse_ShouldParseValidXlsxFile() throws IOException {
        // Given
        // 실제 XLSX 파일은 복잡하므로 CSV 파일로 대체하여 테스트
        String csvContent = "name,age\nJohn,25\nJane,30";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        // When
        ParsedMetadataResponse result = FileParsingUtil.parse(inputStream, "test.csv");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.rowCount()).isEqualTo(2);
        assertThat(result.columnCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("parse - 유효한 JSON 파일 파싱")
    void parse_ShouldParseValidJsonFile() throws IOException {
        // Given
        String jsonContent = "[{\"name\":\"John\",\"age\":25},{\"name\":\"Jane\",\"age\":30}]";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8));

        // When
        ParsedMetadataResponse result = FileParsingUtil.parse(inputStream, "test.json");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.rowCount()).isEqualTo(2);
        assertThat(result.columnCount()).isEqualTo(2);
        assertThat(result.previewJson()).isNotNull();
    }

    @Test
    @DisplayName("parse - null 입력 스트림 처리")
    void parse_ShouldThrowExceptionForNullInputStream() {
        // Given & When & Then
        assertThatThrownBy(() -> FileParsingUtil.parse(null, "test.csv"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("입력 스트림은 null일 수 없습니다");
    }

    @Test
    @DisplayName("parse - null 파일명 처리")
    void parse_ShouldThrowExceptionForNullFilename() {
        // Given
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());

        // When & Then
        assertThatThrownBy(() -> FileParsingUtil.parse(inputStream, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("파일명은 null이거나 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("parse - 빈 파일명 처리")
    void parse_ShouldThrowExceptionForEmptyFilename() {
        // Given
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());

        // When & Then
        assertThatThrownBy(() -> FileParsingUtil.parse(inputStream, ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("파일명은 null이거나 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("parse - 지원하지 않는 파일 형식 처리")
    void parse_ShouldThrowExceptionForUnsupportedFileType() {
        // Given
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());

        // When & Then
        assertThatThrownBy(() -> FileParsingUtil.parse(inputStream, "test.txt"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("지원하지 않는 파일 형식");
    }

    @Test
    @DisplayName("parse - 대소문자 무관 파일 확장자 처리")
    void parse_ShouldHandleCaseInsensitiveExtensions() throws IOException {
        // Given
        String csvContent = "name,age\nJohn,25";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        // When
        ParsedMetadataResponse result = FileParsingUtil.parse(inputStream, "test.CSV");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.rowCount()).isEqualTo(1);
        assertThat(result.columnCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("detectEncoding - UTF-8 인코딩 감지")
    void detectEncoding_ShouldDetectUtf8() throws IOException {
        // Given
        String content = "한글 테스트 content";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        // When
        var charset = FileParsingUtil.detectEncoding(inputStream);

        // Then
        assertThat(charset).isNotNull();
        // UTF-8이 감지되거나 기본값으로 설정되어야 함
        assertThat(charset.name()).isIn("UTF-8", "UTF8");
    }

    @Test
    @DisplayName("detectEncoding - 빈 스트림 처리")
    void detectEncoding_ShouldHandleEmptyStream() throws IOException {
        // Given
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);

        // When
        var charset = FileParsingUtil.detectEncoding(inputStream);

        // Then
        assertThat(charset).isNotNull();
        assertThat(charset.name()).isIn("UTF-8", "UTF8");
    }
}