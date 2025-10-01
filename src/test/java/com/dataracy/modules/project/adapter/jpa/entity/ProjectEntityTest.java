package com.dataracy.modules.project.adapter.jpa.entity;

import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ProjectEntityTest {

    @Test
    @DisplayName("modify 호출 시 필드가 요청 DTO와 부모 프로젝트로 갱신된다")
    void modifyShouldUpdateFields() {
        // given
        ProjectEntity oldParent = ProjectEntity.of("parent", 1L, 1L, 1L, 1L, 1L, false, null, "content", "thumb");
        ProjectEntity child = ProjectEntity.of("old", 2L, 2L, 2L, 2L, 2L, true, oldParent, "oldContent", "oldThumb");

        ModifyProjectRequest req = new ModifyProjectRequest(
                "newTitle",
                99L,
                88L,
                77L,
                66L,
                true,
                123L,
                "newContent",
                List.of(10L, 20L)
        );

        ProjectEntity newParent = ProjectEntity.of("newParent", 9L, 9L, 9L, 9L, 9L, true, null, "c", "t");

        // when
        child.modify(req, newParent);

        // then
        assertAll(
                () -> assertThat(child.getTitle()).isEqualTo("newTitle"),
                () -> assertThat(child.getTopicId()).isEqualTo(99L),
                () -> assertThat(child.getAnalysisPurposeId()).isEqualTo(88L),
                () -> assertThat(child.getDataSourceId()).isEqualTo(77L),
                () -> assertThat(child.getAuthorLevelId()).isEqualTo(66L),
                () -> assertThat(child.getIsContinue()).isTrue(),
                () -> assertThat(child.getParentProject()).isEqualTo(newParent),
                () -> assertThat(child.getContent()).isEqualTo("newContent")
        );
    }

    @Test
    @DisplayName("addProjectData 호출 시 프로젝트와 데이터 엔티티의 관계가 양방향으로 연결된다")
    void addProjectDataShouldLinkBothSides() {
        // given
        ProjectEntity project = ProjectEntity.of("p", 1L, 1L, 1L, 1L, 1L, true, null, "c", "thumb");
        ProjectDataEntity dataEntity = ProjectDataEntity.builder().dataId(10L).build();

        // when
        project.addProjectData(dataEntity);

        // then
        assertAll(
                () -> assertThat(project.getProjectDataEntities()).contains(dataEntity),
                () -> assertThat(dataEntity.getProject()).isEqualTo(project)
        );
    }

    @Test
    @DisplayName("deleteParentProject 호출 시 부모 프로젝트 참조가 null이 된다")
    void deleteParentProjectShouldUnsetParent() {
        // given
        ProjectEntity parent = ProjectEntity.of("parent", 1L, 1L, 1L, 1L, 1L, true, null, "c", "t");
        ProjectEntity child = ProjectEntity.of("child", 2L, 2L, 2L, 2L, 2L, true, parent, "c", "t");

        // when
        child.deleteParentProject();

        // then
        assertThat(child.getParentProject()).isNull();
    }

    @Test
    @DisplayName("updateThumbnailUrl 호출 시 빈 값이면 예외 발생")
    void updateThumbnailUrlShouldThrowWhenBlank() {
        // given
        ProjectEntity project = ProjectEntity.of("p", 1L, 1L, 1L, 1L, 1L, true, null, "c", null);

        // when & then
        ProjectException ex = catchThrowableOfType(
                () -> project.updateThumbnailUrl(" "),
                ProjectException.class
        );
        assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.INVALID_THUMBNAIL_FILE_URL);
    }

    @Test
    @DisplayName("updateThumbnailUrl 호출 시 동일한 값이면 아무 일도 하지 않는다")
    void updateThumbnailUrlShouldDoNothingWhenSame() {
        // given
        ProjectEntity project = ProjectEntity.of("p", 1L, 1L, 1L, 1L, 1L, true, null, "c", "same");

        // when
        project.updateThumbnailUrl("same");

        // then
        assertThat(project.getThumbnailUrl()).isEqualTo("same");
    }

    @Test
    @DisplayName("updateThumbnailUrl 호출 시 다른 값이면 업데이트된다")
    void updateThumbnailUrlShouldUpdateWhenDifferent() {
        // given
        ProjectEntity project = ProjectEntity.of("p", 1L, 1L, 1L, 1L, 1L, true, null, "c", "old");

        // when
        project.updateThumbnailUrl("new");

        // then
        assertThat(project.getThumbnailUrl()).isEqualTo("new");
    }

    @Test
    @DisplayName("delete 호출 시 isDeleted=true 로 변경된다")
    void deleteShouldMarkDeleted() {
        // given
        ProjectEntity project = ProjectEntity.of("p", 1L, 1L, 1L, 1L, 1L, true, null, "c", "t");

        // when
        project.delete();

        // then
        assertThat(project.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("restore 호출 시 isDeleted=false 로 변경된다")
    void restoreShouldUnmarkDeleted() {
        // given
        ProjectEntity project = ProjectEntity.of("p", 1L, 1L, 1L, 1L, 1L, true, null, "c", "t");
        project.delete();

        // when
        project.restore();

        // then
        assertThat(project.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("of 팩토리 메서드로 ProjectEntity가 생성된다")
    void ofShouldCreateEntity() {
        // when
        ProjectEntity p = ProjectEntity.of("t", 10L, 20L, 30L, 40L, 50L, true, null, "c", "thumb");

        // then
        assertAll(
                () -> assertThat(p.getTitle()).isEqualTo("t"),
                () -> assertThat(p.getTopicId()).isEqualTo(10L),
                () -> assertThat(p.getUserId()).isEqualTo(20L),
                () -> assertThat(p.getThumbnailUrl()).isEqualTo("thumb")
        );
    }
}
