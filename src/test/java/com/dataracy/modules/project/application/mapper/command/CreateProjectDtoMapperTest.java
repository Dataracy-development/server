/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.mapper.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.dataracy.modules.project.application.dto.request.command.UploadProjectRequest;
import com.dataracy.modules.project.domain.model.Project;

class CreateProjectDtoMapperTest {

  @Test
  @DisplayName("성공 → UploadProjectRequest와 userId를 Project 도메인으로 매핑")
  void toDomainSuccess() {
    // given
    CreateProjectDtoMapper mapper = new CreateProjectDtoMapper();
    ReflectionTestUtils.setField(mapper, "defaultProjectImageUrl", "default.png");

    UploadProjectRequest requestDto =
        new UploadProjectRequest("title", 1L, 2L, 3L, 4L, true, 123L, "content", List.of(10L, 20L));

    Long userId = 99L;

    // when
    Project project = mapper.toDomain(requestDto, userId, 123L);

    // then
    assertAll(
        () -> assertThat(project.getId()).isNull(),
        () -> assertThat(project.getTitle()).isEqualTo("title"),
        () -> assertThat(project.getTopicId()).isEqualTo(1L),
        () -> assertThat(project.getUserId()).isEqualTo(99L),
        () -> assertThat(project.getAnalysisPurposeId()).isEqualTo(2L),
        () -> assertThat(project.getDataSourceId()).isEqualTo(3L),
        () -> assertThat(project.getAuthorLevelId()).isEqualTo(4L),
        () -> assertThat(project.getIsContinue()).isTrue(),
        () -> assertThat(project.getParentProjectId()).isEqualTo(123L),
        () -> assertThat(project.getContent()).isEqualTo("content"),
        () -> assertThat(project.getThumbnailUrl()).isEqualTo("default.png"),
        () -> assertThat(project.getDataIds()).containsExactly(10L, 20L),
        () -> assertThat(project.getCommentCount()).isZero(),
        () -> assertThat(project.getLikeCount()).isZero(),
        () -> assertThat(project.getViewCount()).isZero(),
        () -> assertThat(project.getIsDeleted()).isFalse(),
        () -> assertThat(project.getChildProjects()).isEmpty(),
        () -> assertThat(project.getCreatedAt()).isNull());
  }
}
