/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.web.mapper.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.dataset.adapter.web.request.command.ModifyDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.request.command.UploadDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.command.UploadDataWebResponse;
import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import com.dataracy.modules.dataset.application.dto.response.command.UploadDataResponse;

@DisplayName("DataCommandWebMapper 테스트")
class DataCommandWebMapperTest {

  private final DataCommandWebMapper mapper = new DataCommandWebMapper();

  @Test
  @DisplayName("UploadDataWebRequest를 UploadDataRequest로 변환 성공")
  void toApplicationDto_ShouldConvertUploadRequest() {
    // Given
    UploadDataWebRequest webRequest =
        new UploadDataWebRequest(
            "Test Dataset",
            1L,
            2L,
            3L,
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 12, 31),
            "Test description",
            "Test analysis guide");

    // When
    UploadDataRequest applicationRequest = mapper.toApplicationDto(webRequest);

    // Then
    assertAll(
        () -> assertThat(applicationRequest).isNotNull(),
        () -> assertThat(applicationRequest.title()).isEqualTo("Test Dataset"),
        () -> assertThat(applicationRequest.topicId()).isEqualTo(1L),
        () -> assertThat(applicationRequest.dataSourceId()).isEqualTo(2L),
        () -> assertThat(applicationRequest.dataTypeId()).isEqualTo(3L),
        () -> assertThat(applicationRequest.startDate()).isEqualTo(LocalDate.of(2023, 1, 1)),
        () -> assertThat(applicationRequest.endDate()).isEqualTo(LocalDate.of(2023, 12, 31)),
        () -> assertThat(applicationRequest.description()).isEqualTo("Test description"),
        () -> assertThat(applicationRequest.analysisGuide()).isEqualTo("Test analysis guide"));
  }

  @Test
  @DisplayName("ModifyDataWebRequest를 ModifyDataRequest로 변환 성공")
  void toApplicationDto_ShouldConvertModifyRequest() {
    // Given
    ModifyDataWebRequest webRequest =
        new ModifyDataWebRequest(
            "Modified Dataset",
            1L,
            2L,
            3L,
            LocalDate.of(2023, 6, 1),
            LocalDate.of(2023, 12, 31),
            "Modified description",
            "Modified analysis guide");

    // When
    ModifyDataRequest applicationRequest = mapper.toApplicationDto(webRequest);

    // Then
    assertAll(
        () -> assertThat(applicationRequest).isNotNull(),
        () -> assertThat(applicationRequest.title()).isEqualTo("Modified Dataset"),
        () -> assertThat(applicationRequest.topicId()).isEqualTo(1L),
        () -> assertThat(applicationRequest.dataSourceId()).isEqualTo(2L),
        () -> assertThat(applicationRequest.dataTypeId()).isEqualTo(3L),
        () -> assertThat(applicationRequest.startDate()).isEqualTo(LocalDate.of(2023, 6, 1)),
        () -> assertThat(applicationRequest.endDate()).isEqualTo(LocalDate.of(2023, 12, 31)),
        () -> assertThat(applicationRequest.description()).isEqualTo("Modified description"),
        () -> assertThat(applicationRequest.analysisGuide()).isEqualTo("Modified analysis guide"));
  }

  @Test
  @DisplayName("UploadDataResponse를 UploadDataWebResponse로 변환 성공")
  void toWebDto_ShouldConvertUploadResponse() {
    // Given
    UploadDataResponse applicationResponse = new UploadDataResponse(123L);

    // When
    UploadDataWebResponse webResponse = mapper.toWebDto(applicationResponse);

    // Then
    assertAll(
        () -> assertThat(webResponse).isNotNull(),
        () -> assertThat(webResponse.id()).isEqualTo(123L));
  }
}
