package com.dataracy.modules.dataset.adapter.jpa.mapper;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.domain.model.Data;

/** 데이터 엔티티와 데이터 도메인 모델을 변환하는 매퍼 */
public final class DataEntityMapper {
  /** 인스턴스 생성을 방지하기 위한 private 생성자입니다. */
  private DataEntityMapper() {}

  /**
   * DataEntity를 Data 도메인 모델로 변환합니다.
   *
   * <p>입력이 null이면 null을 반환합니다. 변환 시 id, 생성일(createdAt)과 함께 기본 속성들, 중첩된 메타데이터 및 sizeBytes(바이트 단위
   * 크기)를 도메인 모델로 매핑합니다.
   *
   * @param dataEntity 변환할 JPA 엔티티 (null 허용)
   * @return 변환된 Data 도메인 객체 또는 입력이 null인 경우 null
   */
  public static Data toDomain(DataEntity dataEntity) {
    if (dataEntity == null) {
      return null;
    }

    return Data.of(
        dataEntity.getId(),
        dataEntity.getTitle(),
        dataEntity.getTopicId(),
        dataEntity.getUserId(),
        dataEntity.getDataSourceId(),
        dataEntity.getDataTypeId(),
        dataEntity.getStartDate(),
        dataEntity.getEndDate(),
        dataEntity.getDescription(),
        dataEntity.getAnalysisGuide(),
        dataEntity.getDataFileUrl(),
        dataEntity.getDataThumbnailUrl(),
        dataEntity.getDownloadCount(),
        dataEntity.getSizeBytes(),
        DataMetadataEntityMapper.toDomain(dataEntity.getMetadata()),
        dataEntity.getCreatedAt());
  }

  /**
   * Data 도메인 객체를 JPA 엔티티인 DataEntity로 변환합니다.
   *
   * <p>입력값이 null이면 null을 반환합니다. 도메인 객체의 대부분 필드를 엔티티에 매핑하며, 중첩된 메타데이터는 DataMetadataEntityMapper를 사용해
   * 변환합니다. 이 변환은 title, topicId, userId, dataSourceId, dataTypeId, 날짜 범위, 설명, 분석 가이드, 파일/섬네일 URL,
   * downloadCount 및 sizeBytes 등을 포함합니다.
   *
   * <p>주의: 이 메서드는 엔티티의 id나 createdAt 같은 생성/식별 관련 필드를 설정하지 않습니다.
   *
   * @param data 변환할 Data 도메인 객체 (null 허용)
   * @return 변환된 DataEntity, 입력이 null이면 null
   */
  public static DataEntity toEntity(Data data) {
    if (data == null) {
      return null;
    }

    return DataEntity.of(
        data.getTitle(),
        data.getTopicId(),
        data.getUserId(),
        data.getDataSourceId(),
        data.getDataTypeId(),
        data.getStartDate(),
        data.getEndDate(),
        data.getDescription(),
        data.getAnalysisGuide(),
        data.getDataFileUrl(),
        data.getDataThumbnailUrl(),
        data.getDownloadCount(),
        data.getSizeBytes(),
        DataMetadataEntityMapper.toEntity(data.getMetadata()));
  }
}
