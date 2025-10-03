/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.mapper.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.project.application.dto.response.support.ParentProjectResponse;
import com.dataracy.modules.project.domain.model.Project;

class ParentProjectDtoMapperTest {

  private final ParentProjectDtoMapper mapper = new ParentProjectDtoMapper();

  @Test
  @DisplayName("성공 → Project와 username을 ParentProjectResponse로 매핑")
  void toResponseDtoSuccess() {
    // given
    LocalDateTime createdAt = LocalDateTime.of(2025, 8, 28, 10, 0);

    Project project =
        Project.of(
            1L, // id
            "parent-title", // title
            10L, // topicId
            99L, // userId
            2L, // analysisPurposeId
            3L, // dataSourceId
            4L, // authorLevelId
            true, // isContinue
            null, // parentProjectId
            "parent-content", // content
            "thumb.png", // thumbnailUrl
            List.of(100L), // dataIds
            createdAt, // createdAt
            5L, // commentCount
            6L, // likeCount
            7L, // viewCount
            false, // isDeleted
            List.of() // childProjects
            );

    String username = "tester";
    String userProfileImageUrl = "https://~~";

    // when
    ParentProjectResponse response = mapper.toResponseDto(project, username, userProfileImageUrl);

    // then
    assertAll(
        () -> assertThat(response.id()).isEqualTo(1L),
        () -> assertThat(response.title()).isEqualTo("parent-title"),
        () -> assertThat(response.content()).isEqualTo("parent-content"),
        () -> assertThat(response.creatorName()).isEqualTo("tester"),
        () -> assertThat(response.commentCount()).isEqualTo(5L),
        () -> assertThat(response.likeCount()).isEqualTo(6L),
        () -> assertThat(response.viewCount()).isEqualTo(7L),
        () -> assertThat(response.createdAt()).isEqualTo(createdAt));
  }
}
