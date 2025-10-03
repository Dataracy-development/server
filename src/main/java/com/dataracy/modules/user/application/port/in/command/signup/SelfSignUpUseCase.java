/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.application.port.in.command.signup;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.user.application.dto.request.signup.SelfSignUpRequest;

public interface SelfSignUpUseCase {
  /**
   * 사용자가 자체적으로 회원가입을 진행하고, 성공 시 리프레시 토큰을 반환합니다.
   *
   * @param requestDto 자체 회원가입 요청 정보를 담은 객체
   * @return 회원가입 완료 후 발급된 리프레시 토큰 응답
   */
  RefreshTokenResponse signUpSelf(SelfSignUpRequest requestDto);
}
