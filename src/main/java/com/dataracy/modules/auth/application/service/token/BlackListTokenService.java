/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.application.service.token;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.dataracy.modules.auth.application.port.in.token.BlackListTokenUseCase;
import com.dataracy.modules.auth.application.port.out.token.BlackListTokenPort;
import com.dataracy.modules.common.logging.support.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlackListTokenService implements BlackListTokenUseCase {

  private final BlackListTokenPort blackListTokenPort;

  private static final String USE_CASE = "BlackListTokenUseCase";

  /**
   * 주어진 토큰을 블랙리스트에 등록한다.
   *
   * @param token 블랙리스트에 등록할 JWT 토큰 문자열
   * @param expirationMillis 블랙리스트 유지 기간(밀리초, 현재 시점부터의 지속 시간)
   */
  @Override
  public void addToBlackList(String token, long expirationMillis) {
    Instant startTime = LoggerFactory.service().logStart(USE_CASE, "토큰 블랙리스트 등록 서비스 시작");
    blackListTokenPort.setBlackListToken(token, expirationMillis);
    LoggerFactory.service().logSuccess(USE_CASE, "토큰 블랙리스트 등록 서비스 성공", startTime);
  }

  /**
   * 주어진 토큰이 블랙리스트에 있는지 여부를 반환합니다.
   *
   * @param token 토큰 문자열
   * @return 블랙리스트에 존재하면 true
   */
  @Override
  public boolean isBlacklisted(String token) {
    Instant startTime = LoggerFactory.service().logStart(USE_CASE, "토큰 블랙리스트 확인 서비스 시작");
    boolean result = blackListTokenPort.isBlacklisted(token);
    LoggerFactory.service().logSuccess(USE_CASE, "토큰 블랙리스트 확인 서비스 성공", startTime);
    return result;
  }
}
