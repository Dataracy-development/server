/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.email.application.port.in.validate;

import com.dataracy.modules.email.application.dto.response.GetResetTokenResponse;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;

public interface VerifyEmailUseCase {
  /**
   * 이메일 주소와 인증 코드, 인증 유형을 받아 이메일 인증을 검증합니다.
   *
   * @param email 인증할 이메일 주소
   * @param code 이메일로 전송된 인증 코드
   * @param verificationType 이메일 인증 유형
   * @return 인증 결과 또는 상태를 나타내는 문자열
   */
  GetResetTokenResponse verifyCode(
      String email, String code, EmailVerificationType verificationType);
}
