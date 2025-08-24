package com.dataracy.modules.comment.adapter.web.response.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UploadCommentWebResponseTest {

    @Test
    @DisplayName("UploadCommentWebResponse record 생성자 및 getter 확인")
    void createResponse() {
        UploadCommentWebResponse res = new UploadCommentWebResponse(100L);

        assertThat(res.id()).isEqualTo(100L);
    }
}

