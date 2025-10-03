/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.application.port.in.command.command;

public interface LogoutUserUseCase {
  /**
   * 지정한 사용자 ID와 리프레시 토큰으로 사용자의 로그아웃을 처리합니다.
   *
   * <p>구현체는 전달된 리프레시 토큰을 무효화하거나 관련 세션/토큰 정보를 제거하여 해당 사용자의 인증 상태를 만료시켜야 합니다.
   *
   * @param userId 로그아웃할 사용자의 식별자
   * @param refreshToken 무효화할 리프레시 토큰 문자열
   */
  void logout(Long userId, String refreshToken);
}
