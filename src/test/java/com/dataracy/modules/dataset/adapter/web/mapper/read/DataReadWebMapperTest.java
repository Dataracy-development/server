package com.dataracy.modules.dataset.adapter.web.mapper.read;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.dataset.adapter.web.response.read.*;
import com.dataracy.modules.dataset.application.dto.response.read.*;

class DataReadWebMapperTest {

  // Test constants
  private static final Integer CURRENT_YEAR = 2024;

  private final DataReadWebMapper mapper = new DataReadWebMapper();

  @Test
  @DisplayName("DataDetailResponse → DataDetailWebResponse 매핑 성공")
  void toWebDtoFromDataDetailResponseSuccess() {
    // given
    DataDetailResponse dto =
        new DataDetailResponse(
            1L,
            "title",
            1L,
            "userA",
            "profile.png",
            "intro",
            "author",
            "occupation",
            "topic",
            "source",
            "type",
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 12, 31),
            "desc",
            "guide",
            "thumb.png",
            100,
            200L,
            300,
            10,
            "{\"col\":\"val\"}",
            LocalDateTime.of(2023, 5, 1, 10, 0));

    // when
    DataDetailWebResponse result = mapper.toWebDto(dto);

    // then
    assertAll(
        () -> assertThat(result.id()).isEqualTo(1L),
        () -> assertThat(result.title()).isEqualTo("title"),
        () -> assertThat(result.creatorId()).isEqualTo(1L),
        () -> assertThat(result.creatorName()).isEqualTo("userA"),
        () -> assertThat(result.downloadCount()).isEqualTo(100),
        () -> assertThat(result.previewJson()).contains("col"));
  }

  @Test
  @DisplayName("DataGroupCountResponse → DataGroupCountWebResponse 매핑 성공")
  void toWebDtoFromDataGroupCountResponseSuccess() {
    // given
    DataGroupCountResponse dto = new DataGroupCountResponse(10L, "topicA", 5L);

    // when
    DataGroupCountWebResponse result = mapper.toWebDto(dto);

    // then
    assertAll(
        () -> assertThat(result.topicId()).isEqualTo(10L),
        () -> assertThat(result.topicLabel()).isEqualTo("topicA"),
        () -> assertThat(result.count()).isEqualTo(5L));
  }

  @Test
  @DisplayName("ConnectedDataResponse → ConnectedDataWebResponse 매핑 성공")
  void toWebDtoFromConnectedDataResponseSuccess() {
    // given
    ConnectedDataResponse dto =
        new ConnectedDataResponse(
            2L,
            "dataset",
            1L,
            "userA",
            "https://~~",
            "topicX",
            "typeY",
            LocalDate.of(CURRENT_YEAR, 1, 1),
            LocalDate.of(CURRENT_YEAR, 6, 30),
            "thumb.png",
            10,
            2048L,
            100,
            20,
            LocalDateTime.of(CURRENT_YEAR, 2, 1, 9, 0),
            3L);

    // when
    ConnectedDataWebResponse result = mapper.toWebDto(dto);

    // then
    assertAll(
        () -> assertThat(result.id()).isEqualTo(2L),
        () -> assertThat(result.title()).isEqualTo("dataset"),
        () -> assertThat(result.creatorId()).isEqualTo(1L),
        () -> assertThat(result.creatorName()).isEqualTo("userA"),
        () -> assertThat(result.countConnectedProjects()).isEqualTo(3L));
  }

  @Test
  @DisplayName("RecentMinimalDataResponse → RecentMinimalDataWebResponse 매핑 성공")
  void toWebDtoFromRecentMinimalDataResponseSuccess() {
    // given
    RecentMinimalDataResponse dto =
        new RecentMinimalDataResponse(
            3L,
            "recentData",
            1L,
            "userA",
            "https://~~",
            "thumb.png",
            LocalDateTime.of(CURRENT_YEAR, 3, 1, 12, 0));

    // when
    RecentMinimalDataWebResponse result = mapper.toWebDto(dto);

    // then
    assertAll(
        () -> assertThat(result.id()).isEqualTo(3L),
        () -> assertThat(result.title()).isEqualTo("recentData"),
        () -> assertThat(result.creatorId()).isEqualTo(1L),
        () -> assertThat(result.creatorName()).isEqualTo("userA"),
        () -> assertThat(result.dataThumbnailUrl()).isEqualTo("thumb.png"));
  }

  @Test
  @DisplayName("PopularDataResponse → PopularDataWebResponse 매핑 성공")
  void toWebDtoFromPopularDataResponseSuccess() {
    // given
    PopularDataResponse dto =
        new PopularDataResponse(
            4L,
            "popularData",
            1L,
            "userA",
            "https://~~",
            "topicY",
            "sourceZ",
            "typeX",
            LocalDate.of(2022, 1, 1),
            LocalDate.of(2022, 12, 31),
            "desc",
            "thumb.png",
            50,
            1024L,
            200,
            30,
            LocalDateTime.of(2022, 5, 5, 15, 0),
            7L);

    // when
    PopularDataWebResponse result = mapper.toWebDto(dto);

    // then
    assertAll(
        () -> assertThat(result.id()).isEqualTo(4L),
        () -> assertThat(result.title()).isEqualTo("popularData"),
        () -> assertThat(result.creatorId()).isEqualTo(1L),
        () -> assertThat(result.creatorName()).isEqualTo("userA"),
        () -> assertThat(result.countConnectedProjects()).isEqualTo(7L));
  }

  @Test
  @DisplayName("UserDataResponse → UserDataWebResponse 매핑 성공")
  void toWebDtoFromUserDataResponseSuccess() {
    // given
    UserDataResponse dto =
        new UserDataResponse(
            4L,
            "userData",
            "topicY",
            "typeX",
            LocalDate.of(2022, 1, 1),
            LocalDate.of(2022, 12, 31),
            "thumb.png",
            50,
            1024L,
            200,
            30,
            LocalDateTime.of(2022, 5, 5, 15, 0),
            7L);

    // when
    UserDataWebResponse result = mapper.toWebDto(dto);

    // then
    assertAll(
        () -> assertThat(result.id()).isEqualTo(4L),
        () -> assertThat(result.title()).isEqualTo("userData"),
        () -> assertThat(result.topicLabel()).isEqualTo("topicY"),
        () -> assertThat(result.countConnectedProjects()).isEqualTo(7L));
  }
}
