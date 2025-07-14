package com.dataracy.modules.data.adapter.persistence.mapper;

import com.dataracy.modules.data.adapter.persistence.entity.DataEntity;
import com.dataracy.modules.data.domain.model.Data;

/**
 * 데이터 엔티티와 데이터 도메인 모델을 변환하는 매퍼
 */
public final class DataEntityMapper {
    private DataEntityMapper() {
    }

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
                dataEntity.getMetadata()
                );
    }

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
                data.getMetadata()
        );
    }
}
