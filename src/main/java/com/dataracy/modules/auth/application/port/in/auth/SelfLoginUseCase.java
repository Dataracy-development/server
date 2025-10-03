/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.application.port.in.auth;

import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;

public interface SelfLoginUseCase {
  /**
   * 사용자의 자체 로그인 요청을 처리하고 새로운 리프레시 토큰을 반환합니다.
   *
   * @param requestDto 자체 로그인에 필요한 사용자 인증 정보가 포함된 요청 객체
   * @return 인증에 성공한 사용자를 위한 리프레시 토큰 응답
   */
  RefreshTokenResponse login(SelfLoginRequest requestDto);

  /**
   * 레이트 리미팅이 적용된 로그인 (IP 기반)
   *
   * @param requestDto 로그인 요청 정보
   * @param clientIp 클라이언트 IP 주소
   * @return 발급된 리프레시 토큰과 만료 시간이 포함된 응답 객체
   */
  RefreshTokenResponse loginWithRateLimit(SelfLoginRequest requestDto, String clientIp);
}
