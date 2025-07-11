package com.dataracy.modules.reference.adapter.web.response;

import java.util.List;

/**
 * AnalysisPurpose 리스트 조회를 위한 웹응답 DTO
 * @param analysisPurposes analysisPurpose 리스트
 */
public record AllAnalysisPurposesWebResponse(List<AnalysisPurposeWebResponse> analysisPurposes) {
    public record AnalysisPurposeWebResponse(
            Long id,
            String value,
            String label
    ) {}
}
