/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.application.port.in.datasource;

import java.util.List;
import java.util.Map;

public interface GetDataSourceLabelFromIdUseCase {
  /**
   * 주어진 데이터 소스 ID에 해당하는 라벨을 반환합니다.
   *
   * @param dataSourceId 데이터 소스의 고유 식별자
   * @return 해당 데이터 소스의 라벨 문자열
   */
  String getLabelById(Long dataSourceId);

  /**
   * 여러 데이터 소스 ID에 대해 각 ID에 해당하는 라벨 문자열을 반환합니다.
   *
   * @param dataSourceIds 라벨을 조회할 데이터 소스 ID 목록
   * @return 각 데이터 소스 ID와 해당 라벨 문자열의 매핑
   */
  Map<Long, String> getLabelsByIds(List<Long> dataSourceIds);
}
