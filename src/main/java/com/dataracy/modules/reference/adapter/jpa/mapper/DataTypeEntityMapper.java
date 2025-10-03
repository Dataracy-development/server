/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.adapter.jpa.mapper;

import com.dataracy.modules.reference.adapter.jpa.entity.DataTypeEntity;
import com.dataracy.modules.reference.domain.model.DataType;

/** 데이터 유형 엔티티와 데이터 유형 도메인 모델을 변환하는 매퍼 */
public final class DataTypeEntityMapper {
  /** 인스턴스 생성을 방지하기 위한 private 생성자입니다. */
  private DataTypeEntityMapper() {}

  /**
   * DataTypeEntity 객체를 DataType 도메인 모델로 변환합니다. 입력이 null이면 null을 반환합니다.
   *
   * @param dataTypeEntity 변환할 DataTypeEntity 객체
   * @return 변환된 DataType 도메인 모델 객체 또는 입력이 null인 경우 null
   */
  public static DataType toDomain(DataTypeEntity dataTypeEntity) {
    if (dataTypeEntity == null) {
      return null;
    }

    return new DataType(
        dataTypeEntity.getId(), dataTypeEntity.getValue(), dataTypeEntity.getLabel());
  }

  /**
   * 데이터 유형 도메인 모델 객체를 데이터 유형 JPA 엔티티로 변환합니다.
   *
   * @param dataType 변환할 데이터 유형 도메인 모델 객체
   * @return 변환된 데이터 유형 엔티티 객체, 입력이 null이면 null을 반환합니다.
   */
  public static DataTypeEntity toEntity(DataType dataType) {
    if (dataType == null) {
      return null;
    }

    return DataTypeEntity.of(dataType.value(), dataType.label());
  }
}
