package com.dataracy.modules.data.adapter.persistence.mapper;

import com.dataracy.modules.data.adapter.persistence.entity.DataMetadataEntity;
import com.dataracy.modules.data.domain.model.DataMetadata;

/**
 * 데이터 메타데이터 엔티티와 데이터 메타데이터 도메인 모델을 변환하는 매퍼
 */
public final class DataMetadataEntityMapper {
    private DataMetadataEntityMapper() {
    }

    public static DataMetadata toDomain(DataMetadataEntity dataMetadataEntity) {
        if (dataMetadataEntity == null) {
            return null;
        }

        return DataMetadata.toDomain(
                dataMetadataEntity.getId(),
                dataMetadataEntity.getRowCount(),
                dataMetadataEntity.getColumnCount(),
                dataMetadataEntity.getPreviewJson(),
                dataMetadataEntity.getQualityScore()
                );
    }

    public static DataMetadataEntity toEntity(DataMetadata dataMetadata) {
        if (dataMetadata == null) {
            return null;
        }

        return DataMetadataEntity.toEntity(
                dataMetadata.getRowCount(),
                dataMetadata.getColumnCount(),
                dataMetadata.getPreviewJson(),
                dataMetadata.getQualityScore()
        );
    }
}
