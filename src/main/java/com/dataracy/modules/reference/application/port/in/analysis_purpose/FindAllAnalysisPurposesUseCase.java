package com.dataracy.modules.reference.application.port.in.analysis_purpose;

import com.dataracy.modules.reference.application.dto.response.AllAnalysisPurposesResponse;

/**
 * 전체 AnalysisPurpose 조회 유스케이스
 */
public interface FindAllAnalysisPurposesUseCase {
    AllAnalysisPurposesResponse allAnalysisPurposes();
}
