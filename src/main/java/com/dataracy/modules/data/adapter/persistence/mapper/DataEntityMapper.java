package com.dataracy.modules.data.adapter.persistence.mapper;

import com.dataracy.modules.data.adapter.persistence.entity.DataEntity;
import com.dataracy.modules.data.domain.model.Data;

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
     * DataEntity 객체를 Data 도메인 모델로 변환합니다.
     *
     * @param dataEntity 변환할 DataEntity 객체. null일 경우 null을 반환합니다.
     * @return 변환된 Data 도메인 모델 객체 또는 입력이 null인 경우 null
     */
    public static Data toDomain(DataEntity dataEntity) {
        if (dataEntity == null) {
            return null;
        }

        return Data.toDomain(
                dataEntity.getId(),
                dataEntity.getTitle(),
                dataEntity.getTopicId(),
                dataEntity.getUserId(),
                dataEntity.getDataSourceId(),
                dataEntity.getAuthorLevelId(),
                dataEntity.getStartDate(),
                dataEntity.getEndDate(),
                dataEntity.getDescription(),
                dataEntity.getAnalysisGuide(),
                dataEntity.getDataFileUrl(),
                dataEntity.getThumbnailUrl(),
                dataEntity.getDownloadCount(),
                dataEntity.getRecentWeekDownloadCount(),
                DataMetadataEntityMapper.toDomain(dataEntity.getMetadata())
                );
    }

    /**
     * Data 도메인 객체를 DataEntity 엔티티로 변환합니다.
     *
     * 입력값이 null인 경우 null을 반환합니다. 데이터의 모든 필드와 메타데이터를 매핑하여 DataEntity 인스턴스를 생성합니다.
     *
     * @param data 변환할 Data 도메인 객체
     * @return 변환된 DataEntity 엔티티 또는 입력이 null일 경우 null
     */
    public static DataEntity toEntity(Data data) {
        if (data == null) {
            return null;
        }

        return DataEntity.toEntity(
                data.getTitle(),
                data.getTopicId(),
                data.getUserId(),
                data.getDataSourceId(),
                data.getAuthorLevelId(),
                data.getStartDate(),
                data.getEndDate(),
                data.getDescription(),
                data.getAnalysisGuide(),
                data.getDataFileUrl(),
                data.getThumbnailUrl(),
                data.getDownloadCount(),
                data.getRecentWeekDownloadCount(),
                DataMetadataEntityMapper.toEntity(data.getMetadata())
        );
    }
}
