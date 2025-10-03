/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.web.mapper.read;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dataracy.modules.project.adapter.web.mapper.support.ParentProjectWebMapper;
import com.dataracy.modules.project.adapter.web.mapper.support.ProjectConnectedDataWebMapper;
import com.dataracy.modules.project.adapter.web.response.read.*;
import com.dataracy.modules.project.adapter.web.response.support.ChildProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.support.ParentProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.support.ProjectConnectedDataWebResponse;
import com.dataracy.modules.project.application.dto.response.read.*;
import com.dataracy.modules.project.application.dto.response.support.ChildProjectResponse;
import com.dataracy.modules.project.application.dto.response.support.ParentProjectResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectConnectedDataResponse;

@ExtendWith(MockitoExtension.class)
class ProjectReadWebMapperTest {

  @Mock private ProjectConnectedDataWebMapper projectConnectedDataWebMapper;

  @Mock private ParentProjectWebMapper parentProjectWebMapper;

  @InjectMocks private ProjectReadWebMapper mapper;

  @Test
  @DisplayName("성공 → ProjectDetailResponse를 ProjectDetailWebResponse로 매핑")
  void toWebDtoProjectDetail() {
    // given
    ProjectConnectedDataResponse connectedDataResponse =
        new ProjectConnectedDataResponse(
            1L,
            "데이터셋",
            1L,
            "userA",
            "https://~~",
            "토픽",
            "CSV",
            LocalDate.of(2025, 8, 1),
            LocalDate.of(2025, 8, 5),
            "thumb.png",
            3,
            55,
            100,
            LocalDateTime.of(2025, 8, 4, 10, 30),
            5L);

    ProjectConnectedDataWebResponse mappedData =
        new ProjectConnectedDataWebResponse(
            1L,
            "데이터셋",
            1L,
            "userA",
            "https://~~",
            "토픽",
            "CSV",
            LocalDate.of(2025, 8, 1),
            LocalDate.of(2025, 8, 5),
            "thumb.png",
            3,
            55,
            100,
            LocalDateTime.of(2025, 8, 4, 10, 30),
            5L);

    ParentProjectResponse parentResponse =
        new ParentProjectResponse(
            99L, "부모제목", "내용", 1L, "작성자", "https://~~", 2L, 3L, 4L, LocalDateTime.now());

    ParentProjectWebResponse mappedParent =
        new ParentProjectWebResponse(
            99L, "부모제목", "내용", 1L, "작성자", "https://~~", 2L, 3L, 4L, LocalDateTime.now());

    ProjectDetailResponse responseDto =
        new ProjectDetailResponse(
            10L,
            "title",
            1L,
            "username",
            "intro",
            "profile.png",
            "authorLevel",
            "occupation",
            "topic",
            "analysisPurpose",
            "dataSource",
            true,
            99L,
            "content",
            "thumb.png",
            LocalDateTime.now(),
            5L,
            6L,
            7L,
            true,
            true,
            List.of(connectedDataResponse),
            parentResponse);

    given(projectConnectedDataWebMapper.toWebDto(connectedDataResponse)).willReturn(mappedData);
    given(parentProjectWebMapper.toWebDto(parentResponse)).willReturn(mappedParent);

    // when
    ProjectDetailWebResponse webResponse = mapper.toWebDto(responseDto);

    // then
    assertAll(
        () -> assertThat(webResponse.id()).isEqualTo(10L),
        () -> assertThat(webResponse.connectedDataSets()).containsExactly(mappedData),
        () -> assertThat(webResponse.parentProject()).isEqualTo(mappedParent));
  }

  @Test
  @DisplayName("성공 → ContinuedProjectResponse를 ContinuedProjectWebResponse로 매핑")
  void toWebDtoContinuedProject() {
    // given
    ContinuedProjectResponse responseDto =
        new ContinuedProjectResponse(
            11L,
            "title",
            1L,
            "username",
            "profile.png",
            "thumb.png",
            "topic",
            "authorLevel",
            5L,
            6L,
            7L,
            LocalDateTime.now());

    // when
    ContinuedProjectWebResponse webResponse = mapper.toWebDto(responseDto);

    // then
    assertAll(
        () -> assertThat(webResponse.id()).isEqualTo(11L),
        () -> assertThat(webResponse.title()).isEqualTo("title"),
        () -> assertThat(webResponse.commentCount()).isEqualTo(5L));
  }

  @Test
  @DisplayName("성공 → ConnectedProjectResponse를 ConnectedProjectWebResponse로 매핑")
  void toWebDtoConnectedProject() {
    // given
    ConnectedProjectResponse responseDto =
        new ConnectedProjectResponse(
            22L, "c-title", 1L, "user", "https://~~", "topic", 3L, 4L, 5L, LocalDateTime.now());

    // when
    ConnectedProjectWebResponse webResponse = mapper.toWebDto(responseDto);

    // then
    assertAll(
        () -> assertThat(webResponse.id()).isEqualTo(22L),
        () -> assertThat(webResponse.title()).isEqualTo("c-title"),
        () -> assertThat(webResponse.viewCount()).isEqualTo(5L));
  }

  @Test
  @DisplayName("성공 → ChildProjectResponse를 ChildProjectWebResponse로 매핑")
  void toWebDtoChildProject() {
    // given
    ChildProjectResponse responseDto =
        new ChildProjectResponse(33L, "child", "content", 1L, "user", "https://~~", 1L, 2L);

    // when
    ChildProjectWebResponse webResponse = mapper.toWebDto(responseDto);

    // then
    assertAll(
        () -> assertThat(webResponse.id()).isEqualTo(33L),
        () -> assertThat(webResponse.title()).isEqualTo("child"),
        () -> assertThat(webResponse.likeCount()).isEqualTo(2L));
  }

  @Test
  @DisplayName("성공 → PopularProjectResponse를 PopularProjectWebResponse로 매핑")
  void toWebDtoPopularProject() {
    // given
    PopularProjectResponse responseDto =
        new PopularProjectResponse(
            44L,
            "pop-title",
            "content",
            1L,
            "user",
            "https://~~",
            "thumb.png",
            "topic",
            "analysisPurpose",
            "dataSource",
            "authorLevel",
            9L,
            8L,
            7L);

    // when
    PopularProjectWebResponse webResponse = mapper.toWebDto(responseDto);

    // then
    assertAll(
        () -> assertThat(webResponse.id()).isEqualTo(44L),
        () -> assertThat(webResponse.title()).isEqualTo("pop-title"),
        () -> assertThat(webResponse.commentCount()).isEqualTo(9L));
  }

  @Test
  @DisplayName("UserProjectResponse → UserProjectWebResponse 변환 성공")
  void toWebDtoUserProject() {
    // given
    UserProjectResponse responseDto =
        new UserProjectResponse(
            1L,
            "테스트 프로젝트",
            "내용입니다",
            "thumb.png",
            "데이터 분석",
            "초급",
            2L,
            5L,
            100L,
            LocalDateTime.of(2023, 8, 30, 12, 0));

    // when
    UserProjectWebResponse result = mapper.toWebDto(responseDto);

    // then
    assertAll(
        () -> assertThat(result.id()).isEqualTo(1L),
        () -> assertThat(result.title()).isEqualTo("테스트 프로젝트"),
        () -> assertThat(result.content()).isEqualTo("내용입니다"),
        () -> assertThat(result.projectThumbnailUrl()).isEqualTo("thumb.png"),
        () -> assertThat(result.topicLabel()).isEqualTo("데이터 분석"),
        () -> assertThat(result.authorLevelLabel()).isEqualTo("초급"),
        () -> assertThat(result.commentCount()).isEqualTo(2L),
        () -> assertThat(result.likeCount()).isEqualTo(5L),
        () -> assertThat(result.viewCount()).isEqualTo(100L),
        () -> assertThat(result.createdAt()).isEqualTo(LocalDateTime.of(2023, 8, 30, 12, 0)));
  }
}
