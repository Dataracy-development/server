package com.dataracy.modules.comment.application.dto.support;

import com.dataracy.modules.comment.application.dto.response.support.FindCommentWithReplyCountResponse;
import com.dataracy.modules.comment.domain.model.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FindCommentWithReplyCountResponseTest {

    @Test
    @DisplayName("FindCommentWithReplyCountResponse 레코드 생성 및 값 확인")
    void createFindCommentWithReplyCountResponse() {
        Comment comment = Comment.of(
                1L,
                10L,
                20L,
                "내용",
                null,
                3L,
                null
        );
        FindCommentWithReplyCountResponse dto = new FindCommentWithReplyCountResponse(comment, 5L);

        assertThat(dto.comment()).isEqualTo(comment);
        assertThat(dto.replyCount()).isEqualTo(5L);
    }
}
