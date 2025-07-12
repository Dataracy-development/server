package com.dataracy.modules.reference.adapter.web.response.allview;

import com.dataracy.modules.reference.adapter.web.response.singleview.AnalysisPurposeWebResponse;

import java.util.List;

/**
 * AnalysisPurpose 리스트 조회를 위한 웹응답 DTO
 * @param analysisPurposes analysisPurpose 리스트
 */
public record AllAnalysisPurposesWebResponse(List<AnalysisPurposeWebResponse> analysisPurposes) {
}
