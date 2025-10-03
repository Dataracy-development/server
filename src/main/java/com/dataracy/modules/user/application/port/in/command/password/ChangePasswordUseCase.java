/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.application.port.in.command.password;

import com.dataracy.modules.user.application.dto.request.password.ChangePasswordRequest;
import com.dataracy.modules.user.application.dto.request.password.ResetPasswordWithTokenRequest;

public interface ChangePasswordUseCase {
  /**
   * 지정한 사용자의 비밀번호를 새로운 값으로 변경합니다.
   *
   * @param userId 비밀번호를 변경할 대상 사용자의 식별자
   * @param requestDto 비밀번호 변경에 필요한 정보를 담은 요청 객체
   */
  void changePassword(Long userId, ChangePasswordRequest requestDto);

  /**
   * 토큰 기반 요청을 사용하여 사용자의 비밀번호를 재설정합니다.
   *
   * @param requestDto 비밀번호 재설정에 필요한 정보를 담은 요청 객체
   */
  void resetPassword(ResetPasswordWithTokenRequest requestDto);
}
