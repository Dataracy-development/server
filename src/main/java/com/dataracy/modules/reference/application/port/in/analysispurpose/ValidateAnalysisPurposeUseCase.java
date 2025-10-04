package com.dataracy.modules.reference.application.port.in.analysispurpose;

public interface ValidateAnalysisPurposeUseCase {
  /**
   * 주어진 분석 목적 ID에 대해 유효성을 검사합니다.
   *
   * @param analysisPurposeId 유효성 검사를 수행할 분석 목적의 식별자
   */
  void validateAnalysisPurpose(Long analysisPurposeId);
}
