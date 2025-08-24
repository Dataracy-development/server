package com.dataracy.modules.comment.application.dto.request.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModifyCommentRequestTest {

    @Test
    @DisplayName("ModifyCommentRequest 레코드 생성 및 값 확인")
    void createModifyCommentRequest() {
        ModifyCommentRequest dto = new ModifyCommentRequest("수정된 내용");

        assertThat(dto.content()).isEqualTo("수정된 내용");
    }
}
