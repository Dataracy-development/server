/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.mapper.read;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.project.application.dto.response.read.UserProjectResponse;
import com.dataracy.modules.project.domain.model.Project;

class UserProjectDtoMapperTest {

  private final UserProjectDtoMapper mapper = new UserProjectDtoMapper();

  @Test
  @DisplayName("Project → UserProjectResponse 변환 성공")
  void toResponseDtoSuccess() {
    // given
    Project project =
        Project.builder()
            .id(1L)
            .title("프로젝트 제목")
            .content("프로젝트 내용")
            .thumbnailUrl("thumb.png")
            .commentCount(3L)
            .likeCount(5L)
            .viewCount(100L)
            .createdAt(LocalDateTime.of(2023, 8, 30, 12, 0))
            .build();

    String topicLabel = "데이터 분석";
    String authorLevelLabel = "초급";

    // when
    UserProjectResponse response = mapper.toResponseDto(project, topicLabel, authorLevelLabel);

    // then
    assertAll(
        () -> assertThat(response.id()).isEqualTo(1L),
        () -> assertThat(response.title()).isEqualTo("프로젝트 제목"),
        () -> assertThat(response.content()).isEqualTo("프로젝트 내용"),
        () -> assertThat(response.topicLabel()).isEqualTo("데이터 분석"),
        () -> assertThat(response.authorLevelLabel()).isEqualTo("초급"),
        () -> assertThat(response.commentCount()).isEqualTo(3L),
        () -> assertThat(response.likeCount()).isEqualTo(5L),
        () -> assertThat(response.viewCount()).isEqualTo(100L),
        () -> assertThat(response.createdAt()).isEqualTo(LocalDateTime.of(2023, 8, 30, 12, 0)));
  }
}
