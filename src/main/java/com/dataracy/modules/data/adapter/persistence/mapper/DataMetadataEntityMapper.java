package com.dataracy.modules.data.adapter.persistence.mapper;

import com.dataracy.modules.data.adapter.persistence.entity.DataMetadataEntity;
import com.dataracy.modules.data.domain.model.DataMetadata;

/**
 * 데이터 메타데이터 엔티티와 데이터 메타데이터 도메인 모델을 변환하는 매퍼
 */
public final class DataMetadataEntityMapper {
    /**
     * 이 클래스의 인스턴스 생성을 방지하기 위한 private 생성자입니다.
     */
    private DataMetadataEntityMapper() {
    }

    /**
     * DataMetadataEntity 객체를 DataMetadata 도메인 모델로 변환합니다.
     *
     * @param dataMetadataEntity 변환할 DataMetadataEntity 객체
     * @return 변환된 DataMetadata 객체, 입력이 null인 경우 null 반환
     */
    public static DataMetadata toDomain(DataMetadataEntity dataMetadataEntity) {
        if (dataMetadataEntity == null) {
            return null;
        }

        return DataMetadata.toDomain(
                dataMetadataEntity.getId(),
                dataMetadataEntity.getRowCount(),
                dataMetadataEntity.getColumnCount(),
                dataMetadataEntity.getPreviewJson()
                );
    }

    /**
     * 도메인 모델인 {@link DataMetadata}를 영속성 엔티티 {@link DataMetadataEntity}로 변환합니다.
     *
     * @param dataMetadata 변환할 데이터 메타데이터 도메인 객체
     * @return 변환된 {@link DataMetadataEntity} 객체, 입력이 {@code null}이면 {@code null} 반환
     */
    public static DataMetadataEntity toEntity(DataMetadata dataMetadata) {
        if (dataMetadata == null) {
            return null;
        }

        return DataMetadataEntity.toEntity(
                dataMetadata.getRowCount(),
                dataMetadata.getColumnCount(),
                dataMetadata.getPreviewJson()
        );
    }
}
