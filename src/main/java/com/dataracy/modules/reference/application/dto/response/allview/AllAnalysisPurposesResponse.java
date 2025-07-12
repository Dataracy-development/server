package com.dataracy.modules.reference.application.dto.response.allview;

import com.dataracy.modules.reference.application.dto.response.singleview.AnalysisPurposeResponse;

import java.util.List;

/**
 * analysisPurposes 리스트 조회를 위한 도메인 응답 DTO
 * @param analysisPurposes analysisPurposes 리스트
 */
public record AllAnalysisPurposesResponse(List<AnalysisPurposeResponse> analysisPurposes) {
}
