package com.dataracy.modules.reference.application.port.in.analysispurpose;

import com.dataracy.modules.reference.application.dto.response.allview.AllAnalysisPurposesResponse;

/**
 * 전체 AnalysisPurpose 조회 유스케이스
 */
public interface FindAllAnalysisPurposesUseCase {
    /**
 * 모든 분석 목적 정보를 조회하여 반환합니다.
 *
 * @return 전체 분석 목적 정보를 담은 AllAnalysisPurposesResponse 객체
 */
    AllAnalysisPurposesResponse findAllAnalysisPurposes();
}
