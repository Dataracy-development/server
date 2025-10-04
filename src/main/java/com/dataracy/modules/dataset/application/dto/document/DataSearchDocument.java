package com.dataracy.modules.dataset.application.dto.document;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.dataracy.modules.dataset.application.dto.response.support.DataLabels;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.model.DataMetadata;

import lombok.Builder;

@Builder
public record DataSearchDocument(
    Long id,
    String title,
    Long topicId,
    String topicLabel,
    Long userId,
    String username,
    String userProfileImageUrl,
    Long dataSourceId,
    String dataSourceLabel,
    Long dataTypeId,
    String dataTypeLabel,
    LocalDate startDate,
    LocalDate endDate,
    String description,
    String analysisGuide,
    String dataFileUrl,
    String dataThumbnailUrl,
    int downloadCount,
    Long sizeBytes,
    Integer rowCount,
    Integer columnCount,
    String previewJson,
    LocalDateTime createdAt,
    Boolean isDeleted) {
  /**
   * 데이터, 메타데이터, 라벨 정보를 결합해 검색/색인에 적합한 DataSearchDocument를 생성합니다.
   *
   * <p>데이터의 주요 속성(식별자, 제목, 시간 범위 등), 메타데이터(row/column/previewJson) 및 라벨(topic, 사용자, 소스/타입 라벨 등)을
   * 통합하여 빌더로 구성된 DataSearchDocument 인스턴스를 반환합니다. 반환된 문서는 isDeleted가 false로 설정됩니다.
   *
   * @param data 데이터의 기본 속성을 가진 엔티티
   * @param dataMetadata 행/열 수 및 미리보기 JSON을 포함하는 메타데이터
   * @param dataLabels 토픽 라벨, 사용자명, 사용자 프로필 이미지 URL 등 라벨 정보를 제공하는 DTO
   * @return 검색 및 색인에 사용할 DataSearchDocument 인스턴스
   */
  public static DataSearchDocument from(
      Data data, DataMetadata dataMetadata, DataLabels dataLabels) {
    return DataSearchDocument.builder()
        .id(data.getId())
        .title(data.getTitle())
        .topicId(data.getTopicId())
        .topicLabel(dataLabels.topicLabel())
        .userId(data.getUserId())
        .username(dataLabels.username())
        .userProfileImageUrl(dataLabels.userProfileImageUrl())
        .dataSourceId(data.getDataSourceId())
        .dataSourceLabel(dataLabels.dataSourceLabel())
        .dataTypeId(data.getDataTypeId())
        .dataTypeLabel(dataLabels.dataTypeLabel())
        .startDate(data.getStartDate())
        .endDate(data.getEndDate())
        .description(data.getDescription())
        .analysisGuide(data.getAnalysisGuide())
        .dataFileUrl(data.getDataFileUrl())
        .dataThumbnailUrl(data.getDataThumbnailUrl())
        .downloadCount(data.getDownloadCount())
        .sizeBytes(data.getSizeBytes())
        .rowCount(dataMetadata.getRowCount())
        .columnCount(dataMetadata.getColumnCount())
        .previewJson(dataMetadata.getPreviewJson())
        .createdAt(data.getCreatedAt())
        .isDeleted(false)
        .build();
  }
}
