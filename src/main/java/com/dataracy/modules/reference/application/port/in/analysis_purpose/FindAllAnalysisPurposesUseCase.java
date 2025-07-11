package com.dataracy.modules.reference.application.port.in.analysis_purpose;

import com.dataracy.modules.reference.application.dto.response.AllAnalysisPurposesResponse;

/**
 * 전체 AnalysisPurpose 조회 유스케이스
 */
public interface FindAllAnalysisPurposesUseCase {
    /**
 * 모든 분석 목적 정보를 조회합니다.
 *
 * @return 전체 분석 목적 정보를 포함하는 응답 객체
 */
AllAnalysisPurposesResponse allAnalysisPurposes();
}
