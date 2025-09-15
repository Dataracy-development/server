package com.dataracy.modules.reference.adapter.web.response.allview;

import com.dataracy.modules.reference.adapter.web.response.singleview.AnalysisPurposeWebResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "분석 목적 리스트 조회 응답")
public record AllAnalysisPurposesWebResponse(List<AnalysisPurposeWebResponse> analysisPurposes) {
}
