package com.dataracy.modules.reference.domain.model;

/**
 * 데이터 유형 도메인 모델
 *
 * @param id 데이터 유형 id
 * @param value 데이터 유형 값
 * @param label 데이터 유형 라벨
 */
public record DataType(Long id, String value, String label) {}
