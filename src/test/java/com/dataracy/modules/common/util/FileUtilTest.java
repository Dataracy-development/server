package com.dataracy.modules.common.util;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileUtilTest {

    @Nested
    @DisplayName("validateImageFile 메서드 테스트")
    class ValidateImageFileTest {

        @Test
        @DisplayName("유효한 이미지 파일일 때 예외 발생하지 않음")
        void validateImageFile_WithValidImageFile_DoesNotThrowException() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.jpg", "image/jpeg", "test content".getBytes()
            );

            // when & then
            FileUtil.validateImageFile(file);
        }

        @Test
        @DisplayName("파일이 null일 때 예외 발생하지 않음")
        void validateImageFile_WithNullFile_DoesNotThrowException() {
            // when & then
            FileUtil.validateImageFile(null);
        }

        @Test
        @DisplayName("파일이 비어있을 때 예외 발생하지 않음")
        void validateImageFile_WithEmptyFile_DoesNotThrowException() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.jpg", "image/jpeg", new byte[0]
            );

            // when & then
            FileUtil.validateImageFile(file);
        }

        @Test
        @DisplayName("지원하지 않는 확장자일 때 예외 발생")
        void validateImageFile_WithUnsupportedExtension_ThrowsException() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.txt", "text/plain", "test content".getBytes()
            );

            // when & then
            assertThatThrownBy(() -> FileUtil.validateImageFile(file))
                    .isInstanceOf(CommonException.class);
        }

        @Test
        @DisplayName("대문자 확장자도 유효한 것으로 처리")
        void validateImageFile_WithUppercaseExtension_DoesNotThrowException() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.JPG", "image/jpeg", "test content".getBytes()
            );

            // when & then
            FileUtil.validateImageFile(file);
        }
    }

    @Nested
    @DisplayName("validateGeneralFile 메서드 테스트")
    class ValidateGeneralFileTest {

        @Test
        @DisplayName("유효한 CSV 파일일 때 예외 발생하지 않음")
        void validateGeneralFile_WithValidCsvFile_DoesNotThrowException() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.csv", "text/csv", "test content".getBytes()
            );

            // when & then
            FileUtil.validateGeneralFile(file);
        }

        @Test
        @DisplayName("유효한 XLSX 파일일 때 예외 발생하지 않음")
        void validateGeneralFile_WithValidXlsxFile_DoesNotThrowException() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "test content".getBytes()
            );

            // when & then
            FileUtil.validateGeneralFile(file);
        }

        @Test
        @DisplayName("유효한 JSON 파일일 때 예외 발생하지 않음")
        void validateGeneralFile_WithValidJsonFile_DoesNotThrowException() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.json", "application/json", "test content".getBytes()
            );

            // when & then
            FileUtil.validateGeneralFile(file);
        }

        @Test
        @DisplayName("파일이 null일 때 예외 발생하지 않음")
        void validateGeneralFile_WithNullFile_DoesNotThrowException() {
            // when & then
            FileUtil.validateGeneralFile(null);
        }

        @Test
        @DisplayName("파일이 비어있을 때 예외 발생하지 않음")
        void validateGeneralFile_WithEmptyFile_DoesNotThrowException() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.csv", "text/csv", new byte[0]
            );

            // when & then
            FileUtil.validateGeneralFile(file);
        }

        @Test
        @DisplayName("지원하지 않는 확장자일 때 예외 발생")
        void validateGeneralFile_WithUnsupportedExtension_ThrowsException() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.txt", "text/plain", "test content".getBytes()
            );

            // when & then
            assertThatThrownBy(() -> FileUtil.validateGeneralFile(file))
                    .isInstanceOf(CommonException.class);
        }

        @Test
        @DisplayName("대문자 확장자도 유효한 것으로 처리")
        void validateGeneralFile_WithUppercaseExtension_DoesNotThrowException() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.CSV", "text/csv", "test content".getBytes()
            );

            // when & then
            FileUtil.validateGeneralFile(file);
        }
    }
}