package com.dataracy.modules.reference.application.port.in.data_source;

import com.dataracy.modules.reference.application.dto.response.AllDataSourcesResponse;

/**
 * DataSource 조회 유스케이스
 */
public interface FindDataSourceUseCase {
    AllDataSourcesResponse.DataSourceResponse findDataSource(Long dataSourceId);
}
