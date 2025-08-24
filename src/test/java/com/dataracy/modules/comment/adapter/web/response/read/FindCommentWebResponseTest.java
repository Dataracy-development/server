package com.dataracy.modules.comment.adapter.web.response.read;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FindCommentWebResponseTest {

    @Test
    @DisplayName("FindCommentWebResponse record 생성자 및 getter 확인")
    void createResponse() {
        LocalDateTime now = LocalDateTime.now();
        FindCommentWebResponse res = new FindCommentWebResponse(
                1L, "닉네임", "실무자", "profile.png", "내용", 3L, 5L, now, true
        );

        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.username()).isEqualTo("닉네임");
        assertThat(res.authorLevelLabel()).isEqualTo("실무자");
        assertThat(res.userProfileUrl()).isEqualTo("profile.png");
        assertThat(res.content()).isEqualTo("내용");
        assertThat(res.likeCount()).isEqualTo(3L);
        assertThat(res.childCommentCount()).isEqualTo(5L);
        assertThat(res.createdAt()).isEqualTo(now);
        assertThat(res.isLiked()).isTrue();
    }
}

