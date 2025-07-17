package com.dataracy.modules.reference.application.dto.response.allview;

import com.dataracy.modules.reference.application.dto.response.singleview.DataTypeResponse;

import java.util.List;

/**
 * dataTypes 리스트 조회를 위한 도메인 응답 DTO
 * @param dataTypes dataTypes 리스트
 */
public record AllDataTypesResponse(List<DataTypeResponse> dataTypes) {
}
