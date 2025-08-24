package com.dataracy.modules.comment.adapter.web.response.read;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FindReplyCommentWebResponseTest {

    @Test
    @DisplayName("FindReplyCommentWebResponse record 생성자 및 getter 확인")
    void createResponse() {
        LocalDateTime now = LocalDateTime.now();
        FindReplyCommentWebResponse res = new FindReplyCommentWebResponse(
                2L, "작성자", "전문가", "profile.png", "답글", 7L, now, false
        );

        assertThat(res.id()).isEqualTo(2L);
        assertThat(res.username()).isEqualTo("작성자");
        assertThat(res.authorLevelLabel()).isEqualTo("전문가");
        assertThat(res.userProfileUrl()).isEqualTo("profile.png");
        assertThat(res.content()).isEqualTo("답글");
        assertThat(res.likeCount()).isEqualTo(7L);
        assertThat(res.createdAt()).isEqualTo(now);
        assertThat(res.isLiked()).isFalse();
    }
}

