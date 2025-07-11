package com.dataracy.modules.reference.application.port.in.data_source;

import com.dataracy.modules.reference.domain.model.DataSource;

/**
 * DataSource 조회 유스케이스
 */
public interface FindDataSourceUseCase {
    DataSource findDataSource(Long dataSourceId);
}
