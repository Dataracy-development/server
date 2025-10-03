/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.application.service.command;

import org.springframework.stereotype.Service;

import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.user.domain.enums.RoleType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtCommandService implements JwtGenerateUseCase {
  private final JwtTokenGenerator jwtTokenGenerator;

  /**
   * OAuth2 제공자 정보와 이메일을 기반으로 추가정보 입력용 JWT 토큰을 생성합니다.
   *
   * @param provider OAuth2 제공자 이름
   * @param providerId 제공자에서 발급한 사용자 ID
   * @param email 사용자 이메일 주소
   * @return 생성된 추가정보 입력용 JWT 토큰 문자열
   * @throws AuthException 토큰 생성에 실패한 경우 발생
   */
  @Override
  public String generateRegisterToken(String provider, String providerId, String email) {
    return jwtTokenGenerator.generateRegisterToken(provider, providerId, email);
  }

  /**
   * 주어진 이메일을 기반으로 패스워드 재설정용 JWT 토큰을 생성합니다.
   *
   * @param email 패스워드 재설정 토큰을 발급할 사용자의 이메일
   * @return 생성된 패스워드 재설정 JWT 토큰 문자열
   * @throws AuthException 토큰 생성에 실패한 경우 발생
   */
  @Override
  public String generateResetPasswordToken(String email) {
    return jwtTokenGenerator.generateResetPasswordToken(email);
  }

  /**
   * 주어진 사용자 ID와 역할을 기반으로 어세스 토큰을 생성합니다.
   *
   * @param userId 토큰을 발급할 사용자 ID
   * @param role 사용자의 역할 정보
   * @return 생성된 어세스 토큰 문자열
   * @throws AuthException 어세스 토큰 생성에 실패한 경우 발생
   */
  @Override
  public String generateAccessToken(Long userId, RoleType role) {
    return jwtTokenGenerator.generateAccessToken(userId, role);
  }

  /**
   * 지정된 사용자 ID와 역할을 기반으로 리프레시 토큰을 생성합니다.
   *
   * @param userId 토큰을 발급할 사용자 ID
   * @param role 토큰을 발급할 사용자의 역할
   * @return 생성된 리프레시 토큰 문자열
   * @throws AuthException 토큰 생성에 실패한 경우 발생
   */
  @Override
  public String generateRefreshToken(Long userId, RoleType role) {
    return jwtTokenGenerator.generateRefreshToken(userId, role);
  }
}
