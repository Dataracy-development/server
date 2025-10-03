/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.application.port.in.analysispurpose;

import com.dataracy.modules.reference.application.dto.response.singleview.AnalysisPurposeResponse;

public interface FindAnalysisPurposeUseCase {
  /**
   * 주어진 ID에 해당하는 분석 목적 정보를 반환합니다.
   *
   * @param analysisPurposeId 조회할 분석 목적의 ID
   * @return 분석 목적 정보가 담긴 응답 객체
   */
  AnalysisPurposeResponse findAnalysisPurpose(Long analysisPurposeId);
}
