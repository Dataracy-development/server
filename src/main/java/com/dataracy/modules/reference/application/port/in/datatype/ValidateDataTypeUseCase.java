/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.application.port.in.datatype;

public interface ValidateDataTypeUseCase {
  /**
   * 주어진 데이터 타입 ID에 해당하는 데이터 타입의 유효성을 검사합니다.
   *
   * @param dataTypeId 유효성 검사를 수행할 데이터 타입의 식별자
   */
  void validateDataType(Long dataTypeId);
}
