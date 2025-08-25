package com.dataracy.modules.like.domain.model;

import com.dataracy.modules.like.domain.enums.TargetType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LikeTest {

    @Test
    @DisplayName("of_should_build_like_with_all_fields")
    void of_should_build_like_with_all_fields() {
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
