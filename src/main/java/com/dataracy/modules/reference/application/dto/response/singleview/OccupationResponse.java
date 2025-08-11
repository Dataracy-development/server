package com.dataracy.modules.reference.application.dto.response.singleview;

/**
 * 직업 애플리케이션 응답 DTO
 *
 * @param id 아이디
 * @param value 값
 * @param label 라벨
 */
public record OccupationResponse(Long id, String value, String label
) {}
