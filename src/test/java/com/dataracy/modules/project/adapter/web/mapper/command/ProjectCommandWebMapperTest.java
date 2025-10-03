/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.web.mapper.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.project.adapter.web.request.command.ModifyProjectWebRequest;
import com.dataracy.modules.project.adapter.web.request.command.UploadProjectWebRequest;
import com.dataracy.modules.project.adapter.web.response.command.UploadProjectWebResponse;
import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import com.dataracy.modules.project.application.dto.request.command.UploadProjectRequest;
import com.dataracy.modules.project.application.dto.response.command.UploadProjectResponse;

class ProjectCommandWebMapperTest {

  private final ProjectCommandWebMapper mapper = new ProjectCommandWebMapper();

  @Test
  @DisplayName("성공 → UploadProjectWebRequest를 UploadProjectRequest로 변환")
  void toApplicationDtoFromUploadRequest() {
    // given
    UploadProjectWebRequest webRequest =
        new UploadProjectWebRequest(
            "title", 1L, 2L, 3L, 4L, true, 10L, "content", List.of(100L, 200L));

    // when
    UploadProjectRequest appRequest = mapper.toApplicationDto(webRequest);

    // then
    assertAll(
        () -> assertThat(appRequest.title()).isEqualTo("title"),
        () -> assertThat(appRequest.topicId()).isEqualTo(1L),
        () -> assertThat(appRequest.analysisPurposeId()).isEqualTo(2L),
        () -> assertThat(appRequest.dataIds()).containsExactly(100L, 200L));
  }

  @Test
  @DisplayName("성공 → UploadProjectResponse를 UploadProjectWebResponse로 변환")
  void toWebDtoFromUploadResponse() {
    // given
    UploadProjectResponse response = new UploadProjectResponse(123L);

    // when
    UploadProjectWebResponse webResponse = mapper.toWebDto(response);

    // then
    assertThat(webResponse.id()).isEqualTo(123L);
  }

  @Test
  @DisplayName("성공 → ModifyProjectWebRequest를 ModifyProjectRequest로 변환")
  void toApplicationDtoFromModifyRequest() {
    // given
    ModifyProjectWebRequest webRequest =
        new ModifyProjectWebRequest(
            "modified", 11L, 22L, 33L, 44L, false, 55L, "updated content", List.of(300L, 400L));

    // when
    ModifyProjectRequest appRequest = mapper.toApplicationDto(webRequest);

    // then
    assertAll(
        () -> assertThat(appRequest.title()).isEqualTo("modified"),
        () -> assertThat(appRequest.topicId()).isEqualTo(11L),
        () -> assertThat(appRequest.dataSourceId()).isEqualTo(33L),
        () -> assertThat(appRequest.dataIds()).containsExactly(300L, 400L));
  }
}
