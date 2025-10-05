package com.dataracy.modules.project.application.mapper.read;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;
import com.dataracy.modules.project.domain.model.Project;

class PopularProjectDtoMapperTest {

  private final PopularProjectDtoMapper mapper = new PopularProjectDtoMapper();

  @Test
  @DisplayName("성공 → Project와 라벨, 작성자명을 PopularProjectResponse로 매핑")
  void toResponseDtoSuccess() {
    // given
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
            null, // parentProjectId
            "proj-content", // content
            "thumb.png", // thumbnailUrl
            List.of(100L, 200L), // dataIds
            null, // createdAt
            5L, // commentCount
            6L, // likeCount
            7L, // viewCount
            false, // isDeleted
            List.of() // childProjects
            );

    String username = "tester";
    String userProfileImageUrl = "https://~~";
    String topicLabel = "topic-label";
    String analysisPurposeLabel = "purpose-label";
    String dataSourceLabel = "source-label";
    String authorLevelLabel = "level-label";

    // when
    PopularProjectResponse response =
        mapper.toResponseDto(
            project,
            username,
            userProfileImageUrl,
            topicLabel,
            analysisPurposeLabel,
            dataSourceLabel,
            authorLevelLabel);

    // then
    assertAll(
        () -> assertThat(response.id()).isEqualTo(1L),
        () -> assertThat(response.title()).isEqualTo("proj-title"),
        () -> assertThat(response.content()).isEqualTo("proj-content"),
        () -> assertThat(response.creatorId()).isEqualTo(99L),
        () -> assertThat(response.creatorName()).isEqualTo("tester"),
        () -> assertThat(response.projectThumbnailUrl()).isEqualTo("thumb.png"),
        () -> assertThat(response.topicLabel()).isEqualTo("topic-label"),
        () -> assertThat(response.analysisPurposeLabel()).isEqualTo("purpose-label"),
        () -> assertThat(response.dataSourceLabel()).isEqualTo("source-label"),
        () -> assertThat(response.authorLevelLabel()).isEqualTo("level-label"),
        () -> assertThat(response.commentCount()).isEqualTo(5L),
        () -> assertThat(response.likeCount()).isEqualTo(6L),
        () -> assertThat(response.viewCount()).isEqualTo(7L));
  }
}
