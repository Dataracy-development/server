/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.email.application.service.query;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.token.ManageResetTokenUseCase;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.email.application.dto.response.GetResetTokenResponse;
import com.dataracy.modules.email.application.port.in.validate.VerifyEmailUseCase;
import com.dataracy.modules.email.application.port.out.code.ManageEmailCodePort;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.exception.EmailException;
import com.dataracy.modules.email.domain.status.EmailErrorStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailVerifyService implements VerifyEmailUseCase {
  private final ManageEmailCodePort manageEmailCodePort;
  private final ManageResetTokenUseCase manageResetTokenUseCase;

  private final JwtGenerateUseCase jwtGenerateUseCase;

  // Use Case 상수 정의
  private static final String VERIFY_EMAIL_USE_CASE = "VerifyEmailUseCase";

  /**
   * 이메일 인증코드를 검증하고, 비밀번호 찾기 목적이면 리셋 비밀번호 토큰을 발급하여 응답합니다.
   *
   * <p>저장된 인증코드와 입력된 인증코드를 비교해 일치하면 인증코드를 삭제합니다. verificationType이 PASSWORD_SEARCH이면 리셋 토큰을 생성하여
   * 저장한 뒤 응답 객체에 포함합니다.
   *
   * @param email 인증을 진행할 이메일 주소
   * @param code 사용자가 입력한 인증 코드
   * @param verificationType 이메일 인증의 목적 (예: 회원가입, 비밀번호 찾기)
   * @return 비밀번호 찾기 인증인 경우 발급된 리셋 비밀번호 토큰을 담은 GetResetTokenResponse(없으면 null 토큰)
   * @throws EmailException 인증코드가 만료되었거나 일치하지 않을 경우 발생
   */
  @Override
  public GetResetTokenResponse verifyCode(
      String email, String code, EmailVerificationType verificationType) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(VERIFY_EMAIL_USE_CASE, "이메일 인증코드 검증 서비스 시작 email=" + email);

    // 레디스에서 이메일 인증 코드 조회
    String savedCode = manageEmailCodePort.verifyCode(email, code, verificationType);
    if (savedCode == null) {
      LoggerFactory.service()
          .logWarning(VERIFY_EMAIL_USE_CASE, "이메일 인증코드가 만료되었습니다. email=" + email);
      throw new EmailException(EmailErrorStatus.EXPIRED_EMAIL_CODE);
    }

    // 이메일 인증코드 일치하지 않을 경우
    if (!savedCode.equals(code)) {
      LoggerFactory.service()
          .logWarning(VERIFY_EMAIL_USE_CASE, "이메일 인증코드가 일치하지 않습니다. email=" + email);
      throw new EmailException(EmailErrorStatus.FAIL_VERIFY_EMAIL_CODE);
    }

    // 검증 완료 후 레디스에서 삭제
    // 트래잭션 정합성을 유지해야 하는 경우는 afterCommit을 사용하지만 검증 후 삭제는 생략해도 비즈니스 로직상 문제가 없다.
    manageEmailCodePort.deleteCode(email, verificationType);

    String resetPasswordToken = null;
    if (verificationType == EmailVerificationType.PASSWORD_SEARCH) {
      resetPasswordToken = jwtGenerateUseCase.generateResetPasswordToken(email);
      manageResetTokenUseCase.saveResetToken(resetPasswordToken);
    }
    GetResetTokenResponse getResetTokenResponse = new GetResetTokenResponse(resetPasswordToken);

    LoggerFactory.service()
        .logSuccess(VERIFY_EMAIL_USE_CASE, "이메일 인증코드 검증 서비스 종료 email=" + email, startTime);
    return getResetTokenResponse;
  }
}
