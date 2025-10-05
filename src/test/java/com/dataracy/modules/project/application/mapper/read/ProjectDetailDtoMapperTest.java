package com.dataracy.modules.project.application.mapper.read;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.project.application.dto.response.read.ProjectDetailResponse;
import com.dataracy.modules.project.application.dto.response.support.ParentProjectResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectConnectedDataResponse;
import com.dataracy.modules.project.domain.model.Project;

class ProjectDetailDtoMapperTest {

  // Test constants
  private static final Integer CURRENT_HOUR = 18;

  private final ProjectDetailDtoMapper mapper = new ProjectDetailDtoMapper();

  @Test
  @DisplayName("성공 → Project와 부가 정보를 ProjectDetailResponse로 매핑")
  void toResponseDtoSuccess() {
    // given
    LocalDateTime createdAt = LocalDateTime.of(2025, 8, 27, CURRENT_HOUR, 0);

    Project project =
        Project.of(
            1L, // id
            "proj-title", // title
            10L, // topicId
            99L, // userId
            2L, // analysisPurposeId
            3L, // dataSourceId
            4L, // authorLevelId
            true, // isContinue
            55L, // parentProjectId
            "proj-content", // content
            "thumb.png", // thumbnailUrl
            List.of(100L, 200L), // dataIds
            createdAt, // createdAt
            5L, // commentCount
            6L, // likeCount
            7L, // viewCount
            false, // isDeleted
            List.of() // childProjects
            );

    List<ProjectConnectedDataResponse> connectedDataSets =
        List.of(
            new ProjectConnectedDataResponse(
                100L,
                "data-title",
                1L,
                "userA",
                "https://~~",
                "topic-label",
                "CSV",
                LocalDate.of(2025, 8, 1),
                LocalDate.of(2025, 8, 5),
                "data-thumb.png",
                3,
                55,
                100,
                LocalDateTime.of(2025, 8, 4, 10, 30),
                5L));

    ParentProjectResponse parentProjectResponse =
        new ParentProjectResponse(
            55L,
            "parent-title",
            "parent-content",
            1L,
            "parent-user",
            "https://~~",
            2L,
            3L,
            4L,
            LocalDateTime.of(2025, 8, 20, 12, 0));

    // when
    ProjectDetailResponse response =
        mapper.toResponseDto(
            project,
            "tester", // username
            "profile.png", // userProfileImageUrl
            "intro text", // userIntroductionText
            "author-level", // authorLevelLabel
            "occupation-label", // occupationLabel
            "topic-label", // topicLabel
            "analysis-purpose", // analysisPurposeLabel
            "data-source", // dataSourceLabel
            true, // isLiked
            true, // hasChild
            connectedDataSets,
            parentProjectResponse);

    // then
    assertAll(
        () -> assertThat(response.id()).isEqualTo(1L),
        () -> assertThat(response.title()).isEqualTo("proj-title"),
        () -> assertThat(response.creatorId()).isEqualTo(99L),
        () -> assertThat(response.creatorName()).isEqualTo("tester"),
        () -> assertThat(response.userIntroductionText()).isEqualTo("intro text"),
        () -> assertThat(response.userProfileImageUrl()).isEqualTo("profile.png"),
        () -> assertThat(response.authorLevelLabel()).isEqualTo("author-level"),
        () -> assertThat(response.occupationLabel()).isEqualTo("occupation-label"),
        () -> assertThat(response.topicLabel()).isEqualTo("topic-label"),
        () -> assertThat(response.analysisPurposeLabel()).isEqualTo("analysis-purpose"),
        () -> assertThat(response.dataSourceLabel()).isEqualTo("data-source"),
        () -> assertThat(response.isContinue()).isTrue(),
        () -> assertThat(response.parentProjectId()).isEqualTo(55L),
        () -> assertThat(response.content()).isEqualTo("proj-content"),
        () -> assertThat(response.projectThumbnailUrl()).isEqualTo("thumb.png"),
        () -> assertThat(response.createdAt()).isEqualTo(createdAt),
        () -> assertThat(response.commentCount()).isEqualTo(5L),
        () -> assertThat(response.likeCount()).isEqualTo(6L),
        () -> assertThat(response.viewCount()).isEqualTo(7L),
        () -> assertThat(response.isLiked()).isTrue(),
        () -> assertThat(response.hasChild()).isTrue(),
        () -> assertThat(response.connectedDataSets()).containsExactlyElementsOf(connectedDataSets),
        () -> assertThat(response.parentProject()).isEqualTo(parentProjectResponse));
  }
}
