package com.dataracy.modules.reference.adapter.persistence.mapper;

import com.dataracy.modules.reference.adapter.persistence.entity.DataSourceEntity;
import com.dataracy.modules.reference.domain.model.DataSource;

/**
 * 데이터 출처 엔티티와 데이터 출처 도메인 모델을 변환하는 매퍼
 */
public final class DataSourceEntityMapper {
    private DataSourceEntityMapper() {
    }

    /**
     * DataSourceEntity 객체를 DataSource 도메인 모델로 변환합니다.
     *
     * @param dataSourceEntity 변환할 DataSourceEntity 객체
     * @return 변환된 DataSource 도메인 모델 객체, 입력이 null이면 null 반환
     */
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

    /**
     * DataSource 도메인 모델을 DataSourceEntity 엔티티로 변환합니다.
     *
     * @param dataSource 변환할 DataSource 도메인 모델 객체
     * @return 변환된 DataSourceEntity 객체, 입력이 null이면 null 반환
     */
    public static DataSourceEntity toEntity(DataSource dataSource) {
        if (dataSource == null) {
            return null;
        }

        return DataSourceEntity.toEntity(
                dataSource.value(),
                dataSource.label()
        );
    }
}
