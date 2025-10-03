/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;

class DataEntityTest {

  @Test
  @DisplayName("delete() 호출 시 isDeleted=true로 변경된다")
  void deleteShouldSetIsDeletedTrue() {
    // given
    DataEntity entity = DataEntity.builder().isDeleted(false).build();

    // when
    entity.delete();

    // then
    assertThat(entity.getIsDeleted()).isTrue();
  }

  @Test
  @DisplayName("restore() 호출 시 isDeleted=false로 변경된다")
  void restoreShouldSetIsDeletedFalse() {
    // given
    DataEntity entity = DataEntity.builder().isDeleted(true).build();

    // when
    entity.restore();

    // then
    assertThat(entity.getIsDeleted()).isFalse();
  }

  @Test
  @DisplayName("modify() 호출 시 필드 값이 변경된다")
  void modifyShouldUpdateFields() {
    // given
    DataEntity entity =
        DataEntity.builder()
            .title("old")
            .topicId(1L)
            .dataSourceId(2L)
            .dataTypeId(3L)
            .description("desc")
            .analysisGuide("guide")
            .build();

    ModifyDataRequest request =
        new ModifyDataRequest(
            "new",
            10L,
            20L,
            30L,
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 12, 31),
            "newDesc",
            "newGuide");

    // when
    entity.modify(request);

    // then
    assertAll(
        () -> assertThat(entity.getTitle()).isEqualTo("new"),
        () -> assertThat(entity.getTopicId()).isEqualTo(10L),
        () -> assertThat(entity.getDescription()).isEqualTo("newDesc"));
  }

  @Test
  @DisplayName("updateDataFile() 호출 시 URL이 비정상이면 예외 발생")
  void updateDataFileShouldThrowWhenUrlInvalid() {
    // given
    DataEntity entity = DataEntity.builder().build();

    // when & then
    DataException ex =
        catchThrowableOfType(() -> entity.updateDataFile(" ", 100L), DataException.class);
    assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.INVALID_FILE_URL);
  }

  @Test
  @DisplayName("updateDataFile() 호출 시 URL과 크기를 갱신한다")
  void updateDataFileShouldUpdateUrlAndSize() {
    // given
    DataEntity entity = DataEntity.builder().dataFileUrl("old").sizeBytes(10L).build();

    // when
    entity.updateDataFile("newUrl", 200L);

    // then
    assertAll(
        () -> assertThat(entity.getDataFileUrl()).isEqualTo("newUrl"),
        () -> assertThat(entity.getSizeBytes()).isEqualTo(200L));
  }

  @Test
  @DisplayName("updateDataFile() 호출 시 동일한 URL이면 변경하지 않는다")
  void updateDataFileShouldNotUpdateWhenSameUrl() {
    // given
    DataEntity entity = DataEntity.builder().dataFileUrl("sameUrl").sizeBytes(100L).build();

    // when
    entity.updateDataFile("sameUrl", 200L);

    // then
    assertAll(
        () -> assertThat(entity.getDataFileUrl()).isEqualTo("sameUrl"),
        () -> assertThat(entity.getSizeBytes()).isEqualTo(100L) // 변경되지 않음
        );
  }

  @Test
  @DisplayName("updateDataThumbnailFile() 호출 시 URL이 비정상이면 예외 발생")
  void updateThumbnailFileShouldThrowWhenInvalid() {
    // given
    DataEntity entity = DataEntity.builder().build();

    // when & then
    DataException ex =
        catchThrowableOfType(() -> entity.updateDataThumbnailFile(""), DataException.class);
    assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.INVALID_FILE_URL);
  }

  @Test
  @DisplayName("updateDataThumbnailFile() 호출 시 썸네일 URL을 갱신한다")
  void updateThumbnailFileShouldUpdateWhenDifferent() {
    // given
    DataEntity entity = DataEntity.builder().dataThumbnailUrl("old").build();

    // when
    entity.updateDataThumbnailFile("newThumb");

    // then
    assertThat(entity.getDataThumbnailUrl()).isEqualTo("newThumb");
  }

  @Test
  @DisplayName("updateDataThumbnailFile() 호출 시 동일한 URL이면 변경하지 않는다")
  void updateThumbnailFileShouldNotUpdateWhenSameUrl() {
    // given
    DataEntity entity = DataEntity.builder().dataThumbnailUrl("sameThumb").build();

    // when
    entity.updateDataThumbnailFile("sameThumb");

    // then
    assertThat(entity.getDataThumbnailUrl()).isEqualTo("sameThumb");
  }

  @Test
  @DisplayName("increaseDownloadCount() 호출 시 다운로드 카운트가 1 증가한다")
  void increaseDownloadCountShouldIncrementByOne() {
    // given
    DataEntity entity = DataEntity.builder().downloadCount(0).build();

    // when
    entity.increaseDownloadCount();

    // then
    assertThat(entity.getDownloadCount()).isEqualTo(1);
  }
}
