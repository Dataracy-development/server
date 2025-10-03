/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.adapter.web.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dataracy.modules.reference.adapter.web.response.allview.AllDataTypesWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.DataTypeWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataTypeResponse;

/** 데이터 유형 웹 DTO와 데이터 유형 애플리케이션 DTO를 변환하는 매퍼 */
@Component
public class DataTypeWebMapper {
  /**
   * 단일 애플리케이션 데이터 유형 응답 객체를 웹 응답 DTO로 변환합니다.
   *
   * @param dataTypeResponse 변환할 애플리케이션 데이터 유형 응답 객체
   * @return 변환된 데이터 유형 웹 응답 DTO
   */
  public DataTypeWebResponse toWebDto(DataTypeResponse dataTypeResponse) {
    return new DataTypeWebResponse(
        dataTypeResponse.id(), dataTypeResponse.value(), dataTypeResponse.label());
  }

  /**
   * 애플리케이션 전체 데이터 유형 응답 DTO를 웹 전체 데이터 유형 응답 DTO로 변환합니다.
   *
   * @param allDataTypesResponse 변환할 애플리케이션 전체 데이터 유형 응답 DTO
   * @return 변환된 웹 전체 데이터 유형 응답 DTO. 입력값이나 내부 리스트가 null인 경우 빈 리스트를 포함합니다.
   */
  public AllDataTypesWebResponse toWebDto(AllDataTypesResponse allDataTypesResponse) {
    if (allDataTypesResponse == null || allDataTypesResponse.dataTypes() == null) {
      return new AllDataTypesWebResponse(List.of());
    }

    return new AllDataTypesWebResponse(
        allDataTypesResponse.dataTypes().stream().map(this::toWebDto).toList());
  }
}
