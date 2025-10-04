package com.dataracy.modules.reference.application.dto.response.singleview;

/**
 * 데이터 유형 애플리케이션 응답 DTO
 *
 * @param id 아이디
 * @param value 값
 * @param label 라벨
 */
public record DataTypeResponse(Long id, String value, String label) {}
