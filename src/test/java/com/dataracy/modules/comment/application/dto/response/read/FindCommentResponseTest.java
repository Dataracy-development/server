package com.dataracy.modules.comment.application.dto.response.read;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FindCommentResponseTest {

    @Test
    @DisplayName("FindCommentResponse 레코드 생성 및 값 확인")
    void createFindCommentResponse() {
        LocalDateTime now = LocalDateTime.now();
        FindCommentResponse dto = new FindCommentResponse(
                1L,
                "닉네임",
                "실무자",
                "profile.png",
                "내용",
                3L,
                5L,
                now,
                true
        );

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.username()).isEqualTo("닉네임");
        assertThat(dto.authorLevelLabel()).isEqualTo("실무자");
        assertThat(dto.userProfileUrl()).isEqualTo("profile.png");
        assertThat(dto.content()).isEqualTo("내용");
        assertThat(dto.likeCount()).isEqualTo(3L);
        assertThat(dto.childCommentCount()).isEqualTo(5L);
        assertThat(dto.createdAt()).isEqualTo(now);
        assertThat(dto.isLiked()).isTrue();
    }
}
