package com.dataracy.modules.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

@DisplayName("FileUtil 테스트")
class FileUtilTest {

    @Test
    @DisplayName("validateImageFile - 유효한 이미지 파일 검증")
    void validateImageFile_ShouldPassForValidImageFile() {
        // Given
        // 5MB 이하의 작은 파일로 테스트
        byte[] smallImageData = new byte[1024]; // 1KB
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", smallImageData
        );

        // When & Then
        // validateImageFile은 void 메서드이므로 예외가 발생하지 않으면 성공
        FileUtil.validateImageFile(file);
    }

    @Test
    @DisplayName("validateImageFile - null 파일 처리")
    void validateImageFile_ShouldHandleNullFile() {
        // When & Then
        // validateImageFile은 null 파일을 처리하므로 예외가 발생하지 않음
        FileUtil.validateImageFile(null);
    }

    @Test
    @DisplayName("validateImageFile - 빈 파일 처리")
    void validateImageFile_ShouldHandleEmptyFile() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file", "empty.jpg", "image/jpeg", new byte[0]
        );

        // When & Then
        FileUtil.validateImageFile(file);
    }

    @Test
    @DisplayName("validateImageFile - 허용된 확장자 검증")
    void validateImageFile_ShouldAcceptValidExtensions() {
        // Given
        byte[] smallData = new byte[1024]; // 1KB
        MockMultipartFile jpgFile = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", smallData
        );
        MockMultipartFile jpegFile = new MockMultipartFile(
            "file", "test.jpeg", "image/jpeg", smallData
        );
        MockMultipartFile pngFile = new MockMultipartFile(
            "file", "test.png", "image/png", smallData
        );

        // When & Then
        FileUtil.validateImageFile(jpgFile);
        FileUtil.validateImageFile(jpegFile);
        FileUtil.validateImageFile(pngFile);
    }

    @Test
    @DisplayName("validateImageFile - 대소문자 무관 확장자 검증")
    void validateImageFile_ShouldAcceptCaseInsensitiveExtensions() {
        // Given
        byte[] smallData = new byte[1024]; // 1KB
        MockMultipartFile jpgFile = new MockMultipartFile(
            "file", "test.JPG", "image/jpeg", smallData
        );
        MockMultipartFile pngFile = new MockMultipartFile(
            "file", "test.PNG", "image/png", smallData
        );

        // When & Then
        FileUtil.validateImageFile(jpgFile);
        FileUtil.validateImageFile(pngFile);
    }

    @Test
    @DisplayName("validateGeneralFile - 유효한 일반 파일 검증")
    void validateGeneralFile_ShouldPassForValidGeneralFile() {
        // Given
        byte[] smallData = new byte[1024]; // 1KB
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.csv", "text/csv", smallData
        );

        // When & Then
        FileUtil.validateGeneralFile(file);
    }

    @Test
    @DisplayName("validateGeneralFile - null 파일 처리")
    void validateGeneralFile_ShouldHandleNullFile() {
        // When & Then
        // validateGeneralFile은 null 파일을 처리하므로 예외가 발생하지 않음
        FileUtil.validateGeneralFile(null);
    }

    @Test
    @DisplayName("validateGeneralFile - 빈 파일 처리")
    void validateGeneralFile_ShouldHandleEmptyFile() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file", "empty.csv", "text/csv", new byte[0]
        );

        // When & Then
        FileUtil.validateGeneralFile(file);
    }

    @Test
    @DisplayName("validateGeneralFile - 허용된 확장자 검증")
    void validateGeneralFile_ShouldAcceptValidExtensions() {
        // Given
        byte[] smallData = new byte[1024]; // 1KB
        MockMultipartFile csvFile = new MockMultipartFile(
            "file", "test.csv", "text/csv", smallData
        );
        MockMultipartFile xlsxFile = new MockMultipartFile(
            "file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", smallData
        );
        MockMultipartFile jsonFile = new MockMultipartFile(
            "file", "test.json", "application/json", smallData
        );

        // When & Then
        FileUtil.validateGeneralFile(csvFile);
        FileUtil.validateGeneralFile(xlsxFile);
        FileUtil.validateGeneralFile(jsonFile);
    }

    @Test
    @DisplayName("validateGeneralFile - 대소문자 무관 확장자 검증")
    void validateGeneralFile_ShouldAcceptCaseInsensitiveExtensions() {
        // Given
        byte[] smallData = new byte[1024]; // 1KB
        MockMultipartFile csvFile = new MockMultipartFile(
            "file", "test.CSV", "text/csv", smallData
        );
        MockMultipartFile xlsxFile = new MockMultipartFile(
            "file", "test.XLSX", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", smallData
        );

        // When & Then
        FileUtil.validateGeneralFile(csvFile);
        FileUtil.validateGeneralFile(xlsxFile);
    }
}