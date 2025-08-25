package com.dataracy.modules.filestorage.support.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class S3KeyGeneratorUtilTest {

    @Test
    @DisplayName("generateKey_should_format_domain_entity_uuid_ext")
    void generateKey_should_format_domain_entity_uuid_ext() {
        // given
        String key = S3KeyGeneratorUtil.generateKey("data", 10L, "photo.png");

        // then
        assertThat(key).startsWith("data/10/").endsWith(".png");
        assertThat(key.split("/")).hasSize(3);
    }

    @Test
    @DisplayName("generateThumbnailKey_should_include_thumb_segment")
    void generateThumbnailKey_should_include_thumb_segment() {
        // given
        String key = S3KeyGeneratorUtil.generateThumbnailKey("project", 77L, "cover.jpg");

        // then
        assertThat(key).startsWith("project/77/thumb/").endsWith(".jpg");
    }

    @Test
    @DisplayName("generateKey_should_default_ext_when_missing_dot")
    void generateKey_should_default_ext_when_missing_dot() {
        // given
        String key = S3KeyGeneratorUtil.generateKey("data", 10L, "noext");

        // then
        assertThat(key).endsWith(".jpg");
    }

    @Test
    @DisplayName("generateKey_should_throw_when_params_null")
    void generateKey_should_throw_when_params_null() {
        // when
        IllegalArgumentException ex = catchThrowableOfType(
            () -> S3KeyGeneratorUtil.generateKey(null, 1L, "a.jpg"),
            IllegalArgumentException.class
        );

        // then
        assertThat(ex).isNotNull();
    }
}
