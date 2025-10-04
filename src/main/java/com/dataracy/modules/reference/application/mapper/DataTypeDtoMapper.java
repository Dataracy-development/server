package com.dataracy.modules.reference.application.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataTypeResponse;
import com.dataracy.modules.reference.domain.model.DataType;

/** 데이터 유형 도메인 DTO와 데이터 유형 도메인 모델을 변환하는 매퍼 */
@Component
public class DataTypeDtoMapper {
  /**
   * 단일 DataType 도메인 객체를 DataTypeResponse DTO로 변환합니다.
   *
   * @param dataType 변환할 DataType 도메인 객체
   * @return DataType의 id, value, label 정보를 포함하는 DataTypeResponse DTO
   */
  public DataTypeResponse toResponseDto(DataType dataType) {
    return new DataTypeResponse(dataType.id(), dataType.value(), dataType.label());
  }

  /**
   * 데이터 유형 도메인 객체 리스트를 전체 데이터 유형 응답 DTO로 변환합니다.
   *
   * @param dataTypes 변환할 데이터 유형 도메인 객체 리스트
   * @return 변환된 데이터 유형 응답 DTO
   */
  public AllDataTypesResponse toResponseDto(List<DataType> dataTypes) {
    return new AllDataTypesResponse(dataTypes.stream().map(this::toResponseDto).toList());
  }
}
