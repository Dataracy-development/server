/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.mapper.read;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.project.application.dto.response.read.ConnectedProjectResponse;
import com.dataracy.modules.project.domain.model.Project;

class ConnectedProjectDtoMapperTest {

  private final ConnectedProjectDtoMapper mapper = new ConnectedProjectDtoMapper();

  @Test
  @DisplayName("성공 → Project와 username, topicLabel을 ConnectedProjectResponse로 매핑")
  void toResponseDtoSuccess() {
    // given
    LocalDateTime createdAt = LocalDateTime.of(2025, 8, 27, 12, 0);

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
            "content", // content
            "thumb.png", // thumbnailUrl
            List.of(100L, 200L), // dataIds
            createdAt, // createdAt
            5L, // commentCount
            6L, // likeCount
            7L, // viewCount
            false, // isDeleted
            List.of() // childProjects
            );

    String username = "tester";
    String userProfileImageUrl = "https://~~";
    String topicLabel = "topic-label";

    // when
    ConnectedProjectResponse response =
        mapper.toResponseDto(project, username, userProfileImageUrl, topicLabel);

    // then
    assertAll(
        () -> assertThat(response.id()).isEqualTo(1L),
        () -> assertThat(response.title()).isEqualTo("proj-title"),
        () -> assertThat(response.creatorId()).isEqualTo(99L),
        () -> assertThat(response.creatorName()).isEqualTo("tester"),
        () -> assertThat(response.topicLabel()).isEqualTo("topic-label"),
        () -> assertThat(response.commentCount()).isEqualTo(5L),
        () -> assertThat(response.likeCount()).isEqualTo(6L),
        () -> assertThat(response.viewCount()).isEqualTo(7L),
        () -> assertThat(response.createdAt()).isEqualTo(createdAt));
  }
}
