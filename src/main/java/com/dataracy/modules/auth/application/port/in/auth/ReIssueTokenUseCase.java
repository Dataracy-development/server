/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.application.port.in.auth;

import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;

public interface ReIssueTokenUseCase {
  /**
   * 주어진 리프레시 토큰을 사용하여 새로운 어세스 토큰과 리프레시 토큰을 발급한다.
   *
   * @param refreshToken 재발급에 사용할 리프레시 토큰
   * @return 새로 발급된 어세스 토큰, 리프레시 토큰, 그리고 각 토큰의 유효 기간 정보를 포함한 응답 객체
   */
  ReIssueTokenResponse reIssueToken(String refreshToken);
}
