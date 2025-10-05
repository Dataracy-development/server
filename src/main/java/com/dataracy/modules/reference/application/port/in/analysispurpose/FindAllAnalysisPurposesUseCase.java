package com.dataracy.modules.reference.application.port.in.analysispurpose;

import com.dataracy.modules.reference.application.dto.response.allview.AllAnalysisPurposesResponse;

public interface FindAllAnalysisPurposesUseCase {
  /**
   * 모든 분석 목적 정보를 조회하여 반환합니다.
   *
   * @return 전체 분석 목적 정보를 담은 AllAnalysisPurposesResponse 객체
   */
  AllAnalysisPurposesResponse findAllAnalysisPurposes();
}
