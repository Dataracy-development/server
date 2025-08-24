package com.dataracy.modules.comment.application.dto.response.read;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FindReplyCommentResponseTest {

    @Test
    @DisplayName("FindReplyCommentResponse 레코드 생성 및 값 확인")
    void createFindReplyCommentResponse() {
        LocalDateTime now = LocalDateTime.now();
        FindReplyCommentResponse dto = new FindReplyCommentResponse(
                2L,
                "작성자",
                "전문가",
                "profile2.png",
                "답글 내용",
                7L,
                now,
                false
        );

        assertThat(dto.id()).isEqualTo(2L);
        assertThat(dto.username()).isEqualTo("작성자");
        assertThat(dto.authorLevelLabel()).isEqualTo("전문가");
        assertThat(dto.userProfileUrl()).isEqualTo("profile2.png");
        assertThat(dto.content()).isEqualTo("답글 내용");
        assertThat(dto.likeCount()).isEqualTo(7L);
        assertThat(dto.createdAt()).isEqualTo(now);
        assertThat(dto.isLiked()).isFalse();
    }
}
