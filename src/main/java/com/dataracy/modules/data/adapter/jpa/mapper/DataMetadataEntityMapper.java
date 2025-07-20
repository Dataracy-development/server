package com.dataracy.modules.data.adapter.jpa.mapper;

import com.dataracy.modules.data.adapter.jpa.entity.DataMetadataEntity;
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
     * DataMetadataEntity를 DataMetadata 도메인 객체로 변환합니다.
     *
     * @param dataMetadataEntity 변환할 DataMetadataEntity 객체
     * @return 변환된 DataMetadata 객체, 입력이 null이면 null을 반환합니다.
     */
    public static DataMetadata toDomain(DataMetadataEntity dataMetadataEntity) {
        if (dataMetadataEntity == null) {
            return null;
        }

        return DataMetadata.of(
                dataMetadataEntity.getId(),
                dataMetadataEntity.getRowCount(),
                dataMetadataEntity.getColumnCount(),
                dataMetadataEntity.getPreviewJson()
                );
    }

    /**
     * {@link DataMetadata} 도메인 객체를 {@link DataMetadataEntity} 영속성 엔티티로 변환합니다.
     *
     * @param dataMetadata 변환할 데이터 메타데이터 도메인 객체
     * @return 변환된 {@link DataMetadataEntity} 객체, 입력이 {@code null}이면 {@code null} 반환
     */
    public static DataMetadataEntity toEntity(DataMetadata dataMetadata) {
        if (dataMetadata == null) {
            return null;
        }

        return DataMetadataEntity.of(
                dataMetadata.getRowCount(),
                dataMetadata.getColumnCount(),
                dataMetadata.getPreviewJson()
        );
    }
}
