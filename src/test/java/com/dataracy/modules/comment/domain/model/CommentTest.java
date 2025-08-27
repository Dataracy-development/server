package com.dataracy.modules.comment.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @Test
    @DisplayName("of() 정적 팩토리 메서드로 Comment 생성")
    void createComment() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // when
        Comment comment = Comment.of(
                1L,
                100L,
                200L,
                "내용입니다",
                null,
                5L,
                now
        );

        // then
        assertThat(comment.getId()).isEqualTo(1L);
        assertThat(comment.getProjectId()).isEqualTo(100L);
        assertThat(comment.getUserId()).isEqualTo(200L);
        assertThat(comment.getContent()).isEqualTo("내용입니다");
        assertThat(comment.getParentCommentId()).isNull();
        assertThat(comment.getLikeCount()).isEqualTo(5L);
        assertThat(comment.getCreatedAt()).isEqualTo(now);
    }
}
