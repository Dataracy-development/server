/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.application.port.out.jwt;

import com.dataracy.modules.user.domain.enums.RoleType;

public interface JwtGeneratorPort {
  /**
   * 소셜 인증 제공자, 제공자별 사용자 ID, 이메일을 기반으로 소셜 로그인 추가 정보 입력용 JWT 토큰을 생성합니다.
   *
   * @param provider 인증 제공자 이름(예: "google", "kakao" 등)
   * @param providerId 인증 제공자별 사용자 고유 ID
   * @param email 사용자 이메일 주소
   * @return 생성된 회원가입용 JWT 토큰 문자열
   */
  String generateRegisterToken(String provider, String providerId, String email);

  /**
   * 사용자의 이메일을 기반으로 비밀번호 재설정용 JWT 토큰을 생성합니다.
   *
   * @param email 비밀번호 재설정 대상 사용자의 이메일 주소
   * @return 비밀번호 재설정에 사용할 JWT 토큰 문자열
   */
  String generateResetPasswordToken(String email);

  /**
   * 주어진 사용자 ID와 역할 정보를 기반으로 액세스 토큰을 생성합니다.
   *
   * @param userId 액세스 토큰을 생성할 사용자의 고유 식별자
   * @param role 사용자의 역할 유형
   * @return 생성된 액세스 토큰 문자열
   */
  String generateAccessToken(Long userId, RoleType role);

  /**
   * 주어진 사용자 ID와 역할 정보를 기반으로 리프레시 토큰을 생성합니다.
   *
   * @param userId 토큰을 생성할 사용자의 고유 식별자
   * @param role 사용자의 역할 유형
   * @return 생성된 리프레시 토큰 문자열
   */
  String generateRefreshToken(Long userId, RoleType role);
}
