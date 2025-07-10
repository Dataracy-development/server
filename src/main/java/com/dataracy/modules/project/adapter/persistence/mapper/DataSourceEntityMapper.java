package com.dataracy.modules.project.adapter.persistence.mapper;

import com.dataracy.modules.project.adapter.persistence.entity.reference.DataSourceEntity;
import com.dataracy.modules.project.domain.model.reference.DataSource;

/**
 * 데이터 출처 엔티티와 데이터 출처 도메인 모델을 변환하는 매퍼
 */
public class DataSourceEntityMapper {
    // DataSource 엔티티 -> DataSource 도메인 모델
    public static DataSource toDomain(DataSourceEntity dataSourceEntity) {
        if (dataSourceEntity == null) {
            return null;
        }

        return new DataSource (
                dataSourceEntity.getId(),
                dataSourceEntity.getValue(),
                dataSourceEntity.getLabel()
        );
    }

    // DataSource 도메인 모델 -> DataSource 엔티티
    public static DataSourceEntity toEntity(DataSource dataSource) {
        if (dataSource == null) {
            return null;
        }

        return DataSourceEntity.toEntity(
                dataSource.id(),
                dataSource.value(),
                dataSource.label()
        );
    }
}
