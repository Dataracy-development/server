package com.dataracy.modules.comment.application.dto.request.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UploadCommentRequestTest {

    @Test
    @DisplayName("UploadCommentRequest 레코드 생성 및 값 확인")
    void createUploadCommentRequest() {
        UploadCommentRequest dto = new UploadCommentRequest("내용", 1L);

        assertThat(dto.content()).isEqualTo("내용");
        assertThat(dto.parentCommentId()).isEqualTo(1L);
    }
}

