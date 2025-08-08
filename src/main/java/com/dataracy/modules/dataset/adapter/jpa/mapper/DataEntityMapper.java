package com.dataracy.modules.dataset.adapter.jpa.mapper;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.domain.model.Data;

/**
 * 데이터 엔티티와 데이터 도메인 모델을 변환하는 매퍼
 */
public final class DataEntityMapper {
    /**
     * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
     */
    private DataEntityMapper() {
    }

    /**
     * DataEntity 객체를 Data 도메인 모델 객체로 변환합니다.
     *
     * 입력이 null이면 null을 반환하며, 모든 필드와 중첩된 메타데이터 및 생성일자를 포함하여 Data 도메인 모델로 매핑합니다.
     *
     * @param dataEntity 변환할 DataEntity 객체
     * @return 변환된 Data 도메인 모델 객체 또는 입력이 null인 경우 null
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
                DataMetadataEntityMapper.toDomain(dataEntity.getMetadata()),
                dataEntity.getCreatedAt()
                );
    }

    /**
     * Data 도메인 객체를 DataEntity 엔티티로 변환합니다.
     *
     * 입력값이 null이면 null을 반환하며, 모든 필드와 중첩된 메타데이터까지 DataEntity로 매핑합니다.
     *
     * @param data 변환할 Data 도메인 객체
     * @return 변환된 DataEntity 엔티티 또는 입력이 null일 경우 null
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
                DataMetadataEntityMapper.toEntity(data.getMetadata())
        );
    }
}
