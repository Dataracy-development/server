package com.dataracy.modules.reference.application.port.in.datasource;

import com.dataracy.modules.reference.application.dto.response.singleview.DataSourceResponse;

public interface FindDataSourceUseCase {
  /**
   * 주어진 데이터 소스 ID로 해당 데이터 소스의 상세 정보를 반환합니다.
   *
   * @param dataSourceId 조회할 데이터 소스의 고유 ID
   * @return 데이터 소스의 상세 정보를 포함하는 DataSourceResponse 객체
   */
  DataSourceResponse findDataSource(Long dataSourceId);
}
