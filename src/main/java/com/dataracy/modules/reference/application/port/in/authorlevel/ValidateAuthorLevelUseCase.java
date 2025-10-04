package com.dataracy.modules.reference.application.port.in.authorlevel;

public interface ValidateAuthorLevelUseCase {
  /**
   * 주어진 authorLevelId에 해당하는 저자 등급의 유효성을 검사합니다.
   *
   * @param authorLevelId 검증할 저자 등급의 식별자
   */
  void validateAuthorLevel(Long authorLevelId);
}
