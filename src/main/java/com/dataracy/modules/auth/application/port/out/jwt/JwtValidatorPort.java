/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.application.port.out.jwt;

import com.dataracy.modules.user.domain.enums.RoleType;

public interface JwtValidatorPort {
  /**
   * 주어진 JWT 토큰의 유효성을 검사합니다.
   *
   * @param token 검증할 JWT 토큰 문자열
   */
  void validateToken(String token);

  /**
   * JWT 토큰에서 사용자 ID를 추출하여 반환합니다.
   *
   * @param token 사용자 정보를 포함한 JWT 토큰
   * @return 토큰에 포함된 사용자 ID
   */
  Long getUserIdFromToken(String token);

  /**
   * JWT 토큰에서 사용자의 역할(RoleType)을 추출합니다.
   *
   * @param token 역할 정보를 포함한 JWT 토큰
   * @return 토큰에 포함된 사용자의 역할
   */
  RoleType getRoleFromToken(String token);

  /**
   * 토큰에서 이메일 주소를 추출하여 반환합니다.
   *
   * @param token 이메일 정보를 포함하고 있는 JWT 토큰
   * @return 토큰에서 추출된 이메일 주소
   */
  String getEmailFromToken(String token);

  /**
   * 레지스터 토큰에서 제공자(provider) 이름을 추출하여 반환합니다.
   *
   * @param token 등록 토큰 문자열
   * @return 토큰에 포함된 소셜 제공자
   */
  String getProviderFromRegisterToken(String token);

  /**
   * 레지스터 토큰에서 소셜 제공자 개인별 ID를 추출하여 반환합니다.
   *
   * @param token 소셜 제공자 개인별 ID를 포함하는 레지스터 토큰
   * @return 소셜 제공자 개인별 ID 문자열
   */
  String getProviderIdFromRegisterToken(String token);
}
