package com.dataracy.modules.comment.application.dto.response.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UploadCommentResponseTest {

    @Test
    @DisplayName("UploadCommentResponse 레코드 생성 및 값 확인")
    void createUploadCommentResponse() {
        UploadCommentResponse dto = new UploadCommentResponse(10L);

        assertThat(dto.id()).isEqualTo(10L);
    }
}
