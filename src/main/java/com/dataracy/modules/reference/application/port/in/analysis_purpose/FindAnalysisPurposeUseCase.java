package com.dataracy.modules.reference.application.port.in.analysis_purpose;

import com.dataracy.modules.reference.domain.model.AnalysisPurpose;

/**
 * AnalysisPurpose 조회 유스케이스
 */
public interface FindAnalysisPurposeUseCase {
    AnalysisPurpose findAnalysisPurpose(Long analysisPurposeId);
}
