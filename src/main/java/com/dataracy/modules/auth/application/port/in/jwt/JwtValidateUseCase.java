/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.application.port.in.jwt;

import com.dataracy.modules.user.domain.enums.RoleType;

public interface JwtValidateUseCase {
  /**
   * 주어진 JWT 토큰의 유효성을 검사합니다.
   *
   * @param token 검사할 JWT 토큰 문자열
   */
  void validateToken(String token);

  /**
   * JWT 토큰에서 유저의 ID를 추출하여 반환합니다.
   *
   * @param token 유저 정보를 포함한 JWT 토큰
   * @return 토큰에 인코딩된 유저의 ID
   */
  Long getUserIdFromToken(String token);

  /**
   * JWT 토큰에서 사용자의 역할(RoleType)을 추출합니다.
   *
   * @param token 역할 정보를 포함한 JWT 토큰
   * @return 토큰에 인코딩된 사용자의 역할
   */
  RoleType getRoleFromToken(String token);

  /**
   * 레지스터 토큰을 파싱하여 소셜 제공자 정보를 반환합니다.
   *
   * @param token 소셜 회원가입을 위해 발급된 레지스터 토큰
   * @return 소셜 제공자
   */
  String getProviderFromRegisterToken(String token);

  /**
   * 레지스터 토큰을 파싱하여 소셜 제공자 ID를 반환합니다.
   *
   * @param token 등록 토큰 문자열
   * @return 소셜 제공자 사용자별 ID
   */
  String getProviderIdFromRegisterToken(String token);

  /**
   * 추가정보 입력 토큰에서 사용자의 이메일 주소를 반환합니다.
   *
   * @param token 소셜 로그인 추가정보 입력용 JWT 토큰 문자열
   * @return 토큰에 포함된 이메일 주소
   */
  String getEmailFromRegisterToken(String token);

  /**
   * 비밀번호 재설정 토큰에서 사용자의 이메일 주소를 추출합니다.
   *
   * @param token 비밀번호 재설정용 JWT 토큰 문자열
   * @return 토큰에 포함된 이메일 주소
   */
  String getEmailFromResetToken(String token);

  /**
   * 소셜 회원가입용 토큰의 만료 시간을 반환합니다.
   *
   * @return 회원가입 토큰의 만료 시간(밀리초 단위)
   */
  long getRegisterTokenExpirationTime();

  /**
   * 액세스 토큰의 만료 시간을 반환합니다.
   *
   * @return 액세스 토큰의 만료 시간(밀리초 단위)
   */
  long getAccessTokenExpirationTime();

  /**
   * 리프레시 토큰의 만료 시간을 반환합니다.
   *
   * @return 리프레시 토큰의 만료 시간(밀리초 단위)
   */
  long getRefreshTokenExpirationTime();

  /**
   * 온보딩 페이지로 리디렉션할 URL을 반환합니다.
   *
   * @return 온보딩 페이지 리디렉션 URL
   */
  String getRedirectOnboardingUrl();

  /**
   * 메인 페이지 리디렉션을 위한 기본 URL을 반환합니다.
   *
   * @return 메인 리디렉션에 사용되는 기본 URL
   */
  String getRedirectBaseUrl();
}
