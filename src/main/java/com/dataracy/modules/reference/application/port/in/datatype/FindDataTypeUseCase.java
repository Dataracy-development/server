/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.application.port.in.datatype;

import com.dataracy.modules.reference.application.dto.response.singleview.DataTypeResponse;

public interface FindDataTypeUseCase {
  /**
   * 주어진 데이터 유형 ID로 해당 데이터 유형의 상세 정보를 반환합니다.
   *
   * @param dataTypeId 조회할 데이터 유형의 고유 ID
   * @return 데이터 유형의 상세 정보를 포함하는 DataTypeResponse 객체
   */
  DataTypeResponse findDataType(Long dataTypeId);
}
