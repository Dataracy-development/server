package com.dataracy.modules.reference.adapter.web.response.allview;

import com.dataracy.modules.reference.adapter.web.response.singleview.AnalysisPurposeWebResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * AnalysisPurpose 리스트 조회를 위한 웹응답 DTO
 * @param analysisPurposes analysisPurpose 리스트
 */
@Schema(description = "분석 목적 리스트 조회 응답")
public record AllAnalysisPurposesWebResponse(List<AnalysisPurposeWebResponse> analysisPurposes) {
}
