package com.dataracy.modules.project.adapter.jpa.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectEsProjectionDlqEntityTest {

    @Test
    @DisplayName("빌더로 생성 시 기본값이 적용된다")
    void builderShouldApplyDefaults() {
        // when
        ProjectEsProjectionDlqEntity entity = ProjectEsProjectionDlqEntity.builder()
                .projectId(1L)
                .build();

        // then
        assertThat(entity.getProjectId()).isEqualTo(1L);
        assertThat(entity.getDeltaComment()).isZero();
        assertThat(entity.getDeltaLike()).isZero();
        assertThat(entity.getDeltaView()).isZero();
        assertThat(entity.getSetDeleted()).isFalse();
        assertThat(entity.getLastError()).isNull();
    }

    @Test
    @DisplayName("setter 호출 시 값이 변경된다")
    void settersShouldUpdateValues() {
        // given
        ProjectEsProjectionDlqEntity entity = new ProjectEsProjectionDlqEntity();

        // when
        entity.setProjectId(10L);
        entity.setDeltaComment(5);
        entity.setDeltaLike(7);
        entity.setDeltaView(20L);
        entity.setSetDeleted(true);
        entity.setLastError("err");

        // then
        assertThat(entity.getProjectId()).isEqualTo(10L);
        assertThat(entity.getDeltaComment()).isEqualTo(5);
        assertThat(entity.getDeltaLike()).isEqualTo(7);
        assertThat(entity.getDeltaView()).isEqualTo(20L);
        assertThat(entity.getSetDeleted()).isTrue();
        assertThat(entity.getLastError()).isEqualTo("err");
    }

    @Test
    @DisplayName("올 아규먼트 생성자로 생성된다")
    void allArgsConstructorShouldInitializeFields() {
        // when
        ProjectEsProjectionDlqEntity entity = new ProjectEsProjectionDlqEntity(
                1L,    // id
                2L,    // projectId
                3,     // deltaComment
                4,     // deltaLike
                5L,    // deltaView
                true,  // setDeleted
                "error" // lastError
        );

        // then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getProjectId()).isEqualTo(2L);
        assertThat(entity.getDeltaComment()).isEqualTo(3);
        assertThat(entity.getDeltaLike()).isEqualTo(4);
        assertThat(entity.getDeltaView()).isEqualTo(5L);
        assertThat(entity.getSetDeleted()).isTrue();
        assertThat(entity.getLastError()).isEqualTo("error");
    }
}
