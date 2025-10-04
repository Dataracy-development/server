package com.dataracy.modules.reference.application.port.in.datasource;

public interface ValidateDataSourceUseCase {
  /**
   * 지정된 ID를 가진 데이터 소스의 유효성을 검사합니다.
   *
   * @param dataSourceId 유효성 검사를 수행할 데이터 소스의 식별자
   */
  void validateDataSource(Long dataSourceId);
}
