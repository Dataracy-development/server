package com.dataracy.modules.project.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ProjectTest {

    @Test
    @DisplayName("updateThumbnailUrl - 썸네일 변경 반영")
    void updateThumbnailUrlSuccess() {
        // given
        Project project = Project.builder().id(1L).title("t").thumbnailUrl("old.png").build();

        // when
        project.updateThumbnailUrl("new.png");

        // then
        assertThat(project.getThumbnailUrl()).isEqualTo("new.png");
    }

    @Test
    @DisplayName("of() - 모든 값 반영 확인")
    void ofSuccess() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // when
        Project project = Project.of(
                1L, "title", 10L, 99L,
                20L, 30L, 40L,
                true, 2L,
                "content", "thumb.png",
                List.of(100L, 200L),
                now,
                5L, 6L, 7L,
                false,
                List.of()
        );

        // then
        assertThat(project.getId()).isEqualTo(1L);
        assertThat(project.getDataIds()).containsExactly(100L, 200L);
        assertThat(project.getLikeCount()).isEqualTo(6L);
        assertThat(project.getIsDeleted()).isFalse();
    }
}
