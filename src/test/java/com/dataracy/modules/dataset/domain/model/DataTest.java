package com.dataracy.modules.dataset.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Data 도메인 모델 테스트 */
class DataTest {

  // Test constants
  private static final Integer CURRENT_YEAR = 2024;

  @Test
  @DisplayName("기본 생성자로 Data 인스턴스 생성")
  void createDataWithDefaultConstructor() {
    // when
    Data data = new Data();

    // then
    assertThat(data).isNotNull();
  }

  @Test
  @DisplayName("Data.of() 정적 팩토리 메서드로 인스턴스 생성")
  void createDataWithOfMethod() {
    // given
    LocalDate startDate = LocalDate.of(2023, 1, 1);
    LocalDate endDate = LocalDate.of(2023, 12, 31);
    LocalDateTime createdAt = LocalDateTime.now();
    DataMetadata metadata = DataMetadata.of(1L, 1000, 10, "{\"preview\": \"테스트 데이터 미리보기\"}");

    // when
    Data data =
        Data.of(
            1L,
            "테스트 데이터셋",
            1L,
            1L,
            1L,
            1L,
            startDate,
            endDate,
            "데이터셋 설명",
            "분석 가이드",
            "dataFile.csv",
            "thumbnail.jpg",
            100,
            1024L,
            metadata,
            createdAt);

    // then
    assertAll(
        () -> assertThat(data).isNotNull(),
        () -> assertThat(data.getId()).isEqualTo(1L),
        () -> assertThat(data.getTitle()).isEqualTo("테스트 데이터셋"),
        () -> assertThat(data.getUserId()).isEqualTo(1L),
        () -> assertThat(data.getDataSourceId()).isEqualTo(1L),
        () -> assertThat(data.getDataTypeId()).isEqualTo(1L),
        () -> assertThat(data.getTopicId()).isEqualTo(1L),
        () -> assertThat(data.getStartDate()).isEqualTo(startDate),
        () -> assertThat(data.getEndDate()).isEqualTo(endDate),
        () -> assertThat(data.getDescription()).isEqualTo("데이터셋 설명"),
        () -> assertThat(data.getAnalysisGuide()).isEqualTo("분석 가이드"),
        () -> assertThat(data.getDataFileUrl()).isEqualTo("dataFile.csv"),
        () -> assertThat(data.getDataThumbnailUrl()).isEqualTo("thumbnail.jpg"),
        () -> assertThat(data.getDownloadCount()).isEqualTo(100),
        () -> assertThat(data.getSizeBytes()).isEqualTo(1024L),
        () -> assertThat(data.getMetadata()).isEqualTo(metadata),
        () -> assertThat(data.getCreatedAt()).isEqualTo(createdAt));
  }

  @Test
  @DisplayName("Data 빌더 패턴으로 인스턴스 생성")
  void createDataWithBuilder() {
    // given
    LocalDate startDate = LocalDate.of(CURRENT_YEAR, 1, 1);
    LocalDate endDate = LocalDate.of(CURRENT_YEAR, 12, 31);
    LocalDateTime createdAt = LocalDateTime.now();
    DataMetadata metadata = DataMetadata.of(2L, 2000, 20, "{\"preview\": \"빌더 데이터 미리보기\"}");

    // when
    Data data =
        Data.builder()
            .id(2L)
            .title("빌더 테스트 데이터셋")
            .userId(3L)
            .dataSourceId(4L)
            .dataTypeId(5L)
            .topicId(6L)
            .startDate(startDate)
            .endDate(endDate)
            .description("빌더로 생성한 데이터셋")
            .analysisGuide("빌더 분석 가이드")
            .dataFileUrl("builderData.csv")
            .dataThumbnailUrl("builderThumbnail.jpg")
            .downloadCount(200)
            .sizeBytes(2048L)
            .metadata(metadata)
            .createdAt(createdAt)
            .build();

    // then
    assertAll(
        () -> assertThat(data).isNotNull(),
        () -> assertThat(data.getId()).isEqualTo(2L),
        () -> assertThat(data.getTitle()).isEqualTo("빌더 테스트 데이터셋"),
        () -> assertThat(data.getUserId()).isEqualTo(3L),
        () -> assertThat(data.getDataSourceId()).isEqualTo(4L),
        () -> assertThat(data.getDataTypeId()).isEqualTo(5L),
        () -> assertThat(data.getTopicId()).isEqualTo(6L),
        () -> assertThat(data.getStartDate()).isEqualTo(startDate),
        () -> assertThat(data.getEndDate()).isEqualTo(endDate),
        () -> assertThat(data.getDescription()).isEqualTo("빌더로 생성한 데이터셋"),
        () -> assertThat(data.getAnalysisGuide()).isEqualTo("빌더 분석 가이드"),
        () -> assertThat(data.getDataFileUrl()).isEqualTo("builderData.csv"),
        () -> assertThat(data.getDataThumbnailUrl()).isEqualTo("builderThumbnail.jpg"),
        () -> assertThat(data.getDownloadCount()).isEqualTo(200),
        () -> assertThat(data.getSizeBytes()).isEqualTo(2048L),
        () -> assertThat(data.getMetadata()).isEqualTo(metadata),
        () -> assertThat(data.getCreatedAt()).isEqualTo(createdAt));
  }

  @Test
  @DisplayName("null 메타데이터로 Data 생성")
  void createDataWithNullMetadata() {
    // given
    LocalDate startDate = LocalDate.of(2023, 1, 1);
    LocalDate endDate = LocalDate.of(2023, 12, 31);
    LocalDateTime createdAt = LocalDateTime.now();

    // when
    Data data =
        Data.of(
            1L,
            "테스트 데이터셋",
            1L,
            1L,
            1L,
            1L,
            startDate,
            endDate,
            "데이터셋 설명",
            "분석 가이드",
            "dataFile.csv",
            "thumbnail.jpg",
            100,
            1024L,
            null,
            createdAt);

    // then
    assertAll(() -> assertThat(data).isNotNull(), () -> assertThat(data.getMetadata()).isNull());
  }

  @Test
  @DisplayName("null 날짜로 Data 생성")
  void createDataWithNullDates() {
    // given
    LocalDateTime createdAt = LocalDateTime.now();
    DataMetadata metadata = DataMetadata.of(3L, 500, 5, "{\"preview\": \"기본 데이터 미리보기\"}");

    // when
    Data data =
        Data.of(
            1L,
            "테스트 데이터셋",
            1L,
            1L,
            1L,
            1L,
            null,
            null,
            "데이터셋 설명",
            "분석 가이드",
            "dataFile.csv",
            "thumbnail.jpg",
            100,
            1024L,
            metadata,
            createdAt);

    // then
    assertAll(
        () -> assertThat(data).isNotNull(),
        () -> assertThat(data.getStartDate()).isNull(),
        () -> assertThat(data.getEndDate()).isNull());
  }

  @Test
  @DisplayName("빈 문자열로 Data 생성")
  void createDataWithEmptyStrings() {
    // given
    LocalDate startDate = LocalDate.of(2023, 1, 1);
    LocalDate endDate = LocalDate.of(2023, 12, 31);
    LocalDateTime createdAt = LocalDateTime.now();
    DataMetadata metadata = DataMetadata.of(4L, 0, 0, "{}");

    // when
    Data data =
        Data.of(
            1L, "", 1L, 1L, 1L, 1L, startDate, endDate, "", "", "", "", 0, 0L, metadata, createdAt);

    // then
    assertAll(
        () -> assertThat(data).isNotNull(),
        () -> assertThat(data.getTitle()).isEmpty(),
        () -> assertThat(data.getDescription()).isEmpty(),
        () -> assertThat(data.getAnalysisGuide()).isEmpty(),
        () -> assertThat(data.getDataFileUrl()).isEmpty(),
        () -> assertThat(data.getDataThumbnailUrl()).isEmpty(),
        () -> assertThat(data.getDownloadCount()).isZero(),
        () -> assertThat(data.getSizeBytes()).isZero());
  }
}
