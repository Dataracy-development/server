package com.dataracy.modules.reference.application.port.in.data_source;

import com.dataracy.modules.reference.application.dto.response.allview.AllDataSourcesResponse;

/**
 * DataSource 조회 유스케이스
 */
public interface FindDataSourceUseCase {
    /**
     * 주어진 데이터 소스 ID에 해당하는 데이터 소스 정보를 조회합니다.
     *
     * @param dataSourceId 조회할 데이터 소스의 고유 식별자
     * @return 데이터 소스의 상세 정보를 담은 DataSourceResponse 객체
     */
    AllDataSourcesResponse.DataSourceResponse findDataSource(Long dataSourceId);
}
