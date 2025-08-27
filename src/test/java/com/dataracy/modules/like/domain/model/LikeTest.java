package com.dataracy.modules.like.domain.model;

import com.dataracy.modules.like.domain.enums.TargetType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LikeTest {

    @Test
    @DisplayName("좋아요 모델")
    void ofShouldBuildLikeWithAllFields() {
        // given
        Long id = 10L;
        Long targetId = 33L;
        TargetType targetType = TargetType.PROJECT;
        Long userId = 99L;

        // when
        Like like = Like.of(id, targetId, targetType, userId);

        // then
        assertThat(like.getId()).isEqualTo(id);
        assertThat(like.getTargetId()).isEqualTo(targetId);
        assertThat(like.getTargetType()).isEqualTo(targetType);
        assertThat(like.getUserId()).isEqualTo(userId);
    }
}
