package com.dataracy.modules.comment.application.dto.support;

import com.dataracy.modules.comment.application.dto.response.support.CommentLabelResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CommentLabelResponseTest {

    @Test
    @DisplayName("CommentLabelResponse 레코드 생성 및 값 확인")
    void createCommentLabelResponse() {
        Map<Long, String> usernames = Map.of(1L, "유저1");
        Map<Long, String> thumbnails = Map.of(1L, "profile.png");
        Map<Long, String> authorLevelIds = Map.of(1L, "10");
        Map<Long, String> authorLevelLabels = Map.of(10L, "실무자");

        CommentLabelResponse dto = new CommentLabelResponse(usernames, thumbnails, authorLevelIds, authorLevelLabels);

        assertThat(dto.usernameMap()).containsEntry(1L, "유저1");
        assertThat(dto.userThumbnailMap()).containsEntry(1L, "profile.png");
        assertThat(dto.userAuthorLevelIds()).containsEntry(1L, "10");
        assertThat(dto.userAuthorLevelLabelMap()).containsEntry(10L, "실무자");
    }
}
