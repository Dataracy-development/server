package com.dataracy.modules.project.adapter.jpa.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;

class ProjectEntityTest {

  // Test constants
  private static final Long EIGHTY_EIGHT = 88L;
  private static final Long DEFAULT_ID = 1L;
  private static final Long SECOND_ID = 2L;
  private static final Long TOPIC_ID = 99L;
  private static final Long ANALYSIS_PURPOSE_ID = EIGHTY_EIGHT;
  private static final Long DATA_SOURCE_ID = 77L;
  private static final Long AUTHOR_LEVEL_ID = 66L;
  private static final Long PARENT_PROJECT_ID = 1L;
  private static final Long THIRD_ID = 9L;
  private static final Long PROJECT_DATA_ID = 10L;
  private static final Long ADDITIONAL_DATA_ID = 20L;
  private static final Long TEST_TOPIC_ID = 10L;
  private static final Long TEST_USER_ID = 20L;
  private static final Long TEST_ANALYSIS_PURPOSE_ID = 30L;
  private static final Long TEST_DATA_SOURCE_ID = 40L;
  private static final Long TEST_AUTHOR_LEVEL_ID = 50L;

  @Test
  @DisplayName("modify 호출 시 필드가 요청 DTO와 부모 프로젝트로 갱신된다")
  void modifyShouldUpdateFields() {
    // given
    ProjectEntity oldParent =
        ProjectEntity.of(
            "parent",
            DEFAULT_ID,
            DEFAULT_ID,
            DEFAULT_ID,
            DEFAULT_ID,
            DEFAULT_ID,
            false,
            null,
            "content",
            "thumb");
    ProjectEntity child =
        ProjectEntity.of(
            "old",
            SECOND_ID,
            SECOND_ID,
            SECOND_ID,
            SECOND_ID,
            SECOND_ID,
            true,
            oldParent,
            "oldContent",
            "oldThumb");

    ModifyProjectRequest req =
        new ModifyProjectRequest(
            "newTitle",
            TOPIC_ID,
            ANALYSIS_PURPOSE_ID,
            DATA_SOURCE_ID,
            AUTHOR_LEVEL_ID,
            true,
            PARENT_PROJECT_ID,
            "newContent",
            List.of(PROJECT_DATA_ID, ADDITIONAL_DATA_ID));

    ProjectEntity newParent =
        ProjectEntity.of(
            "newParent", THIRD_ID, THIRD_ID, THIRD_ID, THIRD_ID, THIRD_ID, true, null, "c", "t");

    // when
    child.modify(req, newParent);

    // then
    assertAll(
        () -> assertThat(child.getTitle()).isEqualTo("newTitle"),
        () -> assertThat(child.getTopicId()).isEqualTo(TOPIC_ID),
        () -> assertThat(child.getAnalysisPurposeId()).isEqualTo(ANALYSIS_PURPOSE_ID),
        () -> assertThat(child.getDataSourceId()).isEqualTo(DATA_SOURCE_ID),
        () -> assertThat(child.getAuthorLevelId()).isEqualTo(AUTHOR_LEVEL_ID),
        () -> assertThat(child.getIsContinue()).isTrue(),
        () -> assertThat(child.getParentProject()).isEqualTo(newParent),
        () -> assertThat(child.getContent()).isEqualTo("newContent"));
  }

  @Test
  @DisplayName("addProjectData 호출 시 프로젝트와 데이터 엔티티의 관계가 양방향으로 연결된다")
  void addProjectDataShouldLinkBothSides() {
    // given
    ProjectEntity project =
        ProjectEntity.of(
            "p",
            DEFAULT_ID,
            DEFAULT_ID,
            DEFAULT_ID,
            DEFAULT_ID,
            DEFAULT_ID,
            true,
            null,
            "c",
            "thumb");
    ProjectDataEntity dataEntity = ProjectDataEntity.builder().dataId(PROJECT_DATA_ID).build();

    // when
    project.addProjectData(dataEntity);

    // then
    assertAll(
        () -> assertThat(project.getProjectDataEntities()).contains(dataEntity),
        () -> assertThat(dataEntity.getProject()).isEqualTo(project));
  }

  @Test
  @DisplayName("deleteParentProject 호출 시 부모 프로젝트 참조가 null이 된다")
  void deleteParentProjectShouldUnsetParent() {
    // given
    ProjectEntity parent =
        ProjectEntity.of(
            "parent",
            DEFAULT_ID,
            DEFAULT_ID,
            DEFAULT_ID,
            DEFAULT_ID,
            DEFAULT_ID,
            true,
            null,
            "c",
            "t");
    ProjectEntity child =
        ProjectEntity.of(
            "child", SECOND_ID, SECOND_ID, SECOND_ID, SECOND_ID, SECOND_ID, true, parent, "c", "t");

    // when
    child.deleteParentProject();

    // then
    assertThat(child.getParentProject()).isNull();
  }

  @Test
  @DisplayName("updateThumbnailUrl 호출 시 빈 값이면 예외 발생")
  void updateThumbnailUrlShouldThrowWhenBlank() {
    // given
    ProjectEntity project =
        ProjectEntity.of(
            "p", DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, true, null, "c", null);

    // when & then
    ProjectException ex =
        catchThrowableOfType(() -> project.updateThumbnailUrl(" "), ProjectException.class);
    assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.INVALID_THUMBNAIL_FILE_URL);
  }

  @Test
  @DisplayName("updateThumbnailUrl 호출 시 동일한 값이면 아무 일도 하지 않는다")
  void updateThumbnailUrlShouldDoNothingWhenSame() {
    // given
    ProjectEntity project =
        ProjectEntity.of(
            "p",
            DEFAULT_ID,
            DEFAULT_ID,
            DEFAULT_ID,
            DEFAULT_ID,
            DEFAULT_ID,
            true,
            null,
            "c",
            "same");

    // when
    project.updateThumbnailUrl("same");

    // then
    assertThat(project.getThumbnailUrl()).isEqualTo("same");
  }

  @Test
  @DisplayName("updateThumbnailUrl 호출 시 다른 값이면 업데이트된다")
  void updateThumbnailUrlShouldUpdateWhenDifferent() {
    // given
    ProjectEntity project =
        ProjectEntity.of(
            "p",
            DEFAULT_ID,
            DEFAULT_ID,
            DEFAULT_ID,
            DEFAULT_ID,
            DEFAULT_ID,
            true,
            null,
            "c",
            "old");

    // when
    project.updateThumbnailUrl("new");

    // then
    assertThat(project.getThumbnailUrl()).isEqualTo("new");
  }

  @Test
  @DisplayName("delete 호출 시 isDeleted=true 로 변경된다")
  void deleteShouldMarkDeleted() {
    // given
    ProjectEntity project =
        ProjectEntity.of(
            "p", DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, true, null, "c", "t");

    // when
    project.delete();

    // then
    assertThat(project.getIsDeleted()).isTrue();
  }

  @Test
  @DisplayName("restore 호출 시 isDeleted=false 로 변경된다")
  void restoreShouldUnmarkDeleted() {
    // given
    ProjectEntity project =
        ProjectEntity.of(
            "p", DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, true, null, "c", "t");
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
    ProjectEntity p =
        ProjectEntity.of(
            "t",
            TEST_TOPIC_ID,
            TEST_USER_ID,
            TEST_ANALYSIS_PURPOSE_ID,
            TEST_DATA_SOURCE_ID,
            TEST_AUTHOR_LEVEL_ID,
            true,
            null,
            "c",
            "thumb");

    // then
    assertAll(
        () -> assertThat(p.getTitle()).isEqualTo("t"),
        () -> assertThat(p.getTopicId()).isEqualTo(TEST_TOPIC_ID),
        () -> assertThat(p.getUserId()).isEqualTo(TEST_USER_ID),
        () -> assertThat(p.getThumbnailUrl()).isEqualTo("thumb"));
  }
}
