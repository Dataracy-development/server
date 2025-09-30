package com.dataracy.modules.filestorage.support.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("S3KeyGeneratorUtil 테스트")
class S3KeyGeneratorUtilTest {

    @Test
    @DisplayName("generateKey - 정상적인 파라미터로 S3 키 생성")
    void generateKey_ShouldGenerateCorrectKey_WhenValidParameters() {
        // Given
        String domain = "projects";
        Long entityId = 123L;
        String originalFilename = "test-image.jpg";

        // When
        String key = S3KeyGeneratorUtil.generateKey(domain, entityId, originalFilename);

        // Then
        assertThat(key).startsWith("projects/123/");
        assertThat(key).endsWith(".jpg");
        assertThat(key).matches("projects/123/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.jpg");
    }

    @Test
    @DisplayName("generateKey - null 파라미터로 예외 발생")
    void generateKey_ShouldThrowException_WhenNullParameters() {
        // Given & When & Then
        assertThatThrownBy(() -> S3KeyGeneratorUtil.generateKey(null, 123L, "test.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파라미터는 null일 수 없습니다.");

        assertThatThrownBy(() -> S3KeyGeneratorUtil.generateKey("projects", null, "test.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파라미터는 null일 수 없습니다.");

        assertThatThrownBy(() -> S3KeyGeneratorUtil.generateKey("projects", 123L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파라미터는 null일 수 없습니다.");
    }

    @Test
    @DisplayName("generateThumbnailKey - 정상적인 파라미터로 썸네일 S3 키 생성")
    void generateThumbnailKey_ShouldGenerateCorrectThumbnailKey_WhenValidParameters() {
        // Given
        String domain = "datasets";
        Long entityId = 456L;
        String originalFilename = "data-file.png";

        // When
        String key = S3KeyGeneratorUtil.generateThumbnailKey(domain, entityId, originalFilename);

        // Then
        assertThat(key).startsWith("datasets/456/thumb/");
        assertThat(key).endsWith(".png");
        assertThat(key).matches("datasets/456/thumb/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.png");
    }

    @Test
    @DisplayName("generateThumbnailKey - null 파라미터로 예외 발생")
    void generateThumbnailKey_ShouldThrowException_WhenNullParameters() {
        // Given & When & Then
        assertThatThrownBy(() -> S3KeyGeneratorUtil.generateThumbnailKey(null, 456L, "test.png"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파라미터는 null일 수 없습니다");

        assertThatThrownBy(() -> S3KeyGeneratorUtil.generateThumbnailKey("datasets", null, "test.png"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파라미터는 null일 수 없습니다");

        assertThatThrownBy(() -> S3KeyGeneratorUtil.generateThumbnailKey("datasets", 456L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파라미터는 null일 수 없습니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "document.pdf",
            "image.jpeg",
            "video.mp4",
            "archive.zip",
            "text.txt"
    })
    @DisplayName("generateKey - 다양한 파일 확장자 처리")
    void generateKey_ShouldHandleVariousExtensions(String filename) {
        // Given
        String domain = "files";
        Long entityId = 789L;

        // When
        String key = S3KeyGeneratorUtil.generateKey(domain, entityId, filename);

        // Then
        String expectedExtension = filename.substring(filename.lastIndexOf('.') + 1);
        assertThat(key).endsWith("." + expectedExtension);
        assertThat(key).startsWith("files/789/");
    }

    @Test
    @DisplayName("generateKey - 확장자가 없는 파일명 처리")
    void generateKey_ShouldUseDefaultExtension_WhenNoExtension() {
        // Given
        String domain = "uploads";
        Long entityId = 999L;
        String filename = "noextension";

        // When
        String key = S3KeyGeneratorUtil.generateKey(domain, entityId, filename);

        // Then
        assertThat(key).endsWith(".jpg");
        assertThat(key).startsWith("uploads/999/");
    }

    @Test
    @DisplayName("generateKey - 점으로 끝나는 파일명 처리")
    void generateKey_ShouldUseDefaultExtension_WhenEndsWithDot() {
        // Given
        String domain = "uploads";
        Long entityId = 888L;
        String filename = "filename.";

        // When
        String key = S3KeyGeneratorUtil.generateKey(domain, entityId, filename);

        // Then
        assertThat(key).endsWith(".jpg");
        assertThat(key).startsWith("uploads/888/");
    }

    @Test
    @DisplayName("generateThumbnailKey - 확장자가 없는 파일명 처리")
    void generateThumbnailKey_ShouldUseDefaultExtension_WhenNoExtension() {
        // Given
        String domain = "uploads";
        Long entityId = 777L;
        String filename = "noextension";

        // When
        String key = S3KeyGeneratorUtil.generateThumbnailKey(domain, entityId, filename);

        // Then
        assertThat(key).endsWith(".jpg");
        assertThat(key).contains("/thumb/");
        assertThat(key).startsWith("uploads/777/thumb/");
    }

    @Test
    @DisplayName("generateKey - 매번 다른 UUID 생성 확인")
    void generateKey_ShouldGenerateDifferentUuids_WhenCalledMultipleTimes() {
        // Given
        String domain = "test";
        Long entityId = 1L;
        String filename = "test.jpg";

        // When
        String key1 = S3KeyGeneratorUtil.generateKey(domain, entityId, filename);
        String key2 = S3KeyGeneratorUtil.generateKey(domain, entityId, filename);

        // Then
        assertThat(key1).isNotEqualTo(key2);
        assertThat(key1).startsWith("test/1/");
        assertThat(key2).startsWith("test/1/");
        assertThat(key1).endsWith(".jpg");
        assertThat(key2).endsWith(".jpg");
    }
}