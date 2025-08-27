package com.dataracy.modules.filestorage.support.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class S3KeyGeneratorUtilTest {

    @Nested
    @DisplayName("s3 키 생성")
    class GenerateKey {

        @Test
        @DisplayName("성공: domain/entityId/uuid.ext 형식으로 생성된다")
        void success_format() {
            // given
            String key = S3KeyGeneratorUtil.generateKey("data", 10L, "photo.png");

            // then
            assertThat(key).startsWith("data/10/").endsWith(".png");
            assertThat(key.split("/")).hasSize(3); // data / 10 / uuid.png
        }

        @Test
        @DisplayName("성공: 확장자가 없으면 기본 .jpg 확장자로 생성된다")
        void success_defaultExtension() {
            // given
            String key = S3KeyGeneratorUtil.generateKey("data", 10L, "noext");

            // then
            assertThat(key).endsWith(".jpg");
        }

        @Test
        @DisplayName("실패: domain 이 null 이면 IllegalArgumentException 발생")
        void fail_nullDomain() {
            // when
            IllegalArgumentException ex = catchThrowableOfType(
                    () -> S3KeyGeneratorUtil.generateKey(null, 1L, "a.jpg"),
                    IllegalArgumentException.class
            );

            // then
            assertThat(ex).isNotNull();
        }
    }

    @Nested
    @DisplayName("s3 썸네일 키 생성")
    class GenerateThumbnailKey {

        @Test
        @DisplayName("성공: 경로에 thumb 세그먼트를 포함한다")
        void success_thumbSegment() {
            // given
            String key = S3KeyGeneratorUtil.generateThumbnailKey("project", 77L, "cover.jpg");

            // then
            assertThat(key).startsWith("project/77/thumb/").endsWith(".jpg");
        }
    }
}
