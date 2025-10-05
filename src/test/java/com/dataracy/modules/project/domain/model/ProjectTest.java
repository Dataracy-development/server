package com.dataracy.modules.project.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Project 도메인 모델 테스트 */
class ProjectTest {

  @Test
  @DisplayName("기본 생성자로 Project 인스턴스 생성")
  void createProjectWithDefaultConstructor() {
    // when
    Project project = new Project();

    // then
    assertThat(project).isNotNull();
  }

  @Test
  @DisplayName("updateThumbnailUrl() 호출 시 썸네일 URL이 변경된다")
  void updateThumbnailUrlShouldUpdateUrl() {
    // given
    Project project =
        Project.of(
            1L,
            "테스트 프로젝트",
            1L,
            1L,
            1L,
            1L,
            1L,
            false,
            1L,
            "프로젝트 내용",
            "oldThumbnail.jpg",
            List.of(1L, 2L),
            LocalDateTime.now(),
            0L,
            0L,
            0L,
            false,
            List.of());

    // when
    project.updateThumbnailUrl("newThumbnail.jpg");

    // then
    assertThat(project.getThumbnailUrl()).isEqualTo("newThumbnail.jpg");
  }

  @Test
  @DisplayName("updateThumbnailUrl() 호출 시 null로 변경 가능하다")
  void updateThumbnailUrlShouldAcceptNull() {
    // given
    Project project =
        Project.of(
            1L,
            "테스트 프로젝트",
            1L,
            1L,
            1L,
            1L,
            1L,
            false,
            1L,
            "프로젝트 내용",
            "oldThumbnail.jpg",
            List.of(1L, 2L),
            LocalDateTime.now(),
            0L,
            0L,
            0L,
            false,
            List.of());

    // when
    project.updateThumbnailUrl(null);

    // then
    assertThat(project.getThumbnailUrl()).isNull();
  }

  @Test
  @DisplayName("Project.of() 정적 팩토리 메서드로 인스턴스 생성")
  void createProjectWithOfMethod() {
    // given
    LocalDateTime now = LocalDateTime.now();
    List<Long> dataIds = List.of(1L, 2L, 3L);
    List<Project> childProjects = List.of();

    // when
    Project project =
        Project.of(
            1L,
            "테스트 프로젝트",
            1L,
            1L,
            1L,
            1L,
            1L,
            false,
            1L,
            "프로젝트 내용",
            "thumbnail.jpg",
            dataIds,
            now,
            5L,
            10L,
            100L,
            false,
            childProjects);

    // then
    assertAll(
        () -> assertThat(project).isNotNull(),
        () -> assertThat(project.getId()).isEqualTo(1L),
        () -> assertThat(project.getTitle()).isEqualTo("테스트 프로젝트"),
        () -> assertThat(project.getTopicId()).isEqualTo(1L),
        () -> assertThat(project.getUserId()).isEqualTo(1L),
        () -> assertThat(project.getAnalysisPurposeId()).isEqualTo(1L),
        () -> assertThat(project.getDataSourceId()).isEqualTo(1L),
        () -> assertThat(project.getAuthorLevelId()).isEqualTo(1L),
        () -> assertThat(project.getIsContinue()).isFalse(),
        () -> assertThat(project.getParentProjectId()).isEqualTo(1L),
        () -> assertThat(project.getContent()).isEqualTo("프로젝트 내용"),
        () -> assertThat(project.getThumbnailUrl()).isEqualTo("thumbnail.jpg"),
        () -> assertThat(project.getDataIds()).isEqualTo(dataIds),
        () -> assertThat(project.getCommentCount()).isEqualTo(5L),
        () -> assertThat(project.getLikeCount()).isEqualTo(10L),
        () -> assertThat(project.getViewCount()).isEqualTo(100L),
        () -> assertThat(project.getIsDeleted()).isFalse(),
        () -> assertThat(project.getCreatedAt()).isEqualTo(now),
        () -> assertThat(project.getChildProjects()).isEqualTo(childProjects));
  }

  @Test
  @DisplayName("Project 빌더 패턴으로 인스턴스 생성")
  void createProjectWithBuilder() {
    // when
    Project project =
        Project.builder()
            .id(1L)
            .title("빌더 테스트 프로젝트")
            .topicId(2L)
            .userId(3L)
            .analysisPurposeId(4L)
            .dataSourceId(5L)
            .authorLevelId(6L)
            .isContinue(true)
            .parentProjectId(7L)
            .content("빌더로 생성한 프로젝트")
            .thumbnailUrl("builderThumbnail.jpg")
            .dataIds(List.of(10L, 20L))
            .commentCount(15L)
            .likeCount(25L)
            .viewCount(150L)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .childProjects(List.of())
            .build();

    // then
    assertAll(
        () -> assertThat(project).isNotNull(),
        () -> assertThat(project.getId()).isEqualTo(1L),
        () -> assertThat(project.getTitle()).isEqualTo("빌더 테스트 프로젝트"),
        () -> assertThat(project.getTopicId()).isEqualTo(2L),
        () -> assertThat(project.getUserId()).isEqualTo(3L),
        () -> assertThat(project.getAnalysisPurposeId()).isEqualTo(4L),
        () -> assertThat(project.getDataSourceId()).isEqualTo(5L),
        () -> assertThat(project.getAuthorLevelId()).isEqualTo(6L),
        () -> assertThat(project.getIsContinue()).isTrue(),
        () -> assertThat(project.getParentProjectId()).isEqualTo(7L),
        () -> assertThat(project.getContent()).isEqualTo("빌더로 생성한 프로젝트"),
        () -> assertThat(project.getThumbnailUrl()).isEqualTo("builderThumbnail.jpg"),
        () -> assertThat(project.getDataIds()).isEqualTo(List.of(10L, 20L)),
        () -> assertThat(project.getCommentCount()).isEqualTo(15L),
        () -> assertThat(project.getLikeCount()).isEqualTo(25L),
        () -> assertThat(project.getViewCount()).isEqualTo(150L),
        () -> assertThat(project.getIsDeleted()).isFalse(),
        () -> assertThat(project.getCreatedAt()).isNotNull(),
        () -> assertThat(project.getChildProjects()).isEmpty());
  }

  @Test
  @DisplayName("null 데이터 ID 리스트로 Project 생성")
  void createProjectWithNullDataIds() {
    // when
    Project project =
        Project.of(
            1L,
            "테스트 프로젝트",
            1L,
            1L,
            1L,
            1L,
            1L,
            false,
            1L,
            "프로젝트 내용",
            "thumbnail.jpg",
            null,
            LocalDateTime.now(),
            0L,
            0L,
            0L,
            false,
            List.of());

    // then
    assertAll(
        () -> assertThat(project).isNotNull(),
        () -> assertThat(project.getDataIds()).isEmpty()); // 방어적 복사로 인해 null 대신 빈 리스트 반환
  }

  @Test
  @DisplayName("빈 데이터 ID 리스트로 Project 생성")
  void createProjectWithEmptyDataIds() {
    // when
    Project project =
        Project.of(
            1L,
            "테스트 프로젝트",
            1L,
            1L,
            1L,
            1L,
            1L,
            false,
            1L,
            "프로젝트 내용",
            "thumbnail.jpg",
            List.of(),
            LocalDateTime.now(),
            0L,
            0L,
            0L,
            false,
            List.of());

    // then
    assertAll(
        () -> assertThat(project).isNotNull(), () -> assertThat(project.getDataIds()).isEmpty());
  }
}
