package com.dataracy.modules.reference.application.port.in.analysis_purpose;

import com.dataracy.modules.reference.application.dto.response.AllAnalysisPurposesResponse;

/**
 * AnalysisPurpose 조회 유스케이스
 */
public interface FindAnalysisPurposeUseCase {
    /**
 * 주어진 분석 목적 ID에 해당하는 분석 목적 정보를 조회합니다.
 *
 * @param analysisPurposeId 조회할 분석 목적의 ID
 * @return 해당 ID에 대한 분석 목적 응답 객체
 */
AllAnalysisPurposesResponse.AnalysisPurposeResponse findAnalysisPurpose(Long analysisPurposeId);
}
