package com.dataracy.modules.reference.application.port.in.datasource;

import com.dataracy.modules.reference.application.dto.response.allview.AllDataSourcesResponse;

public interface FindAllDataSourcesUseCase {
  /**
   * 모든 데이터 소스를 조회하여 전체 목록을 반환합니다.
   *
   * @return 모든 데이터 소스 정보를 포함하는 AllDataSourcesResponse 객체
   */
  AllDataSourcesResponse findAllDataSources();
}
