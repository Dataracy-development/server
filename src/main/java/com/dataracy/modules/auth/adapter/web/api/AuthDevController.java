/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.adapter.web.api;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.dataracy.modules.auth.adapter.web.mapper.AuthDevWebMapper;
import com.dataracy.modules.auth.adapter.web.request.RefreshTokenWebRequest;
import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.auth.adapter.web.response.ReIssueTokenWebResponse;
import com.dataracy.modules.auth.adapter.web.response.RefreshTokenWebResponse;
import com.dataracy.modules.auth.application.dto.request.RefreshTokenRequest;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.auth.ReIssueTokenUseCase;
import com.dataracy.modules.auth.application.port.in.auth.SelfLoginUseCase;
import com.dataracy.modules.auth.domain.status.AuthSuccessStatus;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthDevController implements AuthDevApi {
  private final AuthDevWebMapper authDevWebMapper;

  private final SelfLoginUseCase selfLoginUseCase;
  private final ReIssueTokenUseCase reIssueTokenUseCase;

  /**
   * 개발 환경에서 자체 로그인 요청을 처리하여 리프레시 토큰 정보를 반환합니다.
   *
   * @param webRequest 자체 로그인 요청 정보를 담은 객체
   * @return 로그인 성공 상태와 리프레시 토큰 정보를 포함한 HTTP 200 OK 응답
   */
  @Override
  public ResponseEntity<SuccessResponse<RefreshTokenWebResponse>> loginDev(
      SelfLoginWebRequest webRequest) {
    Instant startTime = LoggerFactory.api().logRequest("[Login] 개발용 로그인 API 요청 시작");
    RefreshTokenWebResponse webResponse;

    try {
      SelfLoginRequest requestDto = authDevWebMapper.toApplicationDto(webRequest);
      // 자체 로그인 진행
      RefreshTokenResponse responseDto = selfLoginUseCase.login(requestDto);
      webResponse = authDevWebMapper.toWebDto(responseDto);
    } finally {
      LoggerFactory.api().logResponse("[Login] 개발용 로그인 API 응답 완료", startTime);
    }
    return ResponseEntity.ok(SuccessResponse.of(AuthSuccessStatus.OK_SELF_LOGIN, webResponse));
  }

  /**
   * 개발 환경에서 토큰 재발급 요청을 처리하여 새로운 토큰 정보를 반환합니다.
   *
   * @param webRequest 클라이언트가 전달한 토큰 재발급 요청 데이터(리프레시 토큰)
   * @return 재발급된 토큰 정보를 포함하는 성공 응답
   */
  @Override
  public ResponseEntity<SuccessResponse<ReIssueTokenWebResponse>> reIssueTokenDev(
      RefreshTokenWebRequest webRequest) {
    Instant startTime = LoggerFactory.api().logRequest("[ReIssueToken] 개발용 토큰 재발급 API 요청 시작");
    ReIssueTokenWebResponse webResponse;

    try {
      RefreshTokenRequest requestDto = authDevWebMapper.toApplicationDto(webRequest);
      // 토큰 재발급 진행
      ReIssueTokenResponse responseDto =
          reIssueTokenUseCase.reIssueToken(requestDto.refreshToken());
      webResponse = authDevWebMapper.toWebDto(responseDto);
    } finally {
      LoggerFactory.api().logResponse("[ReIssueToken] 개발용 토큰 재발급 API 응답 완료", startTime);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(SuccessResponse.of(AuthSuccessStatus.OK_RE_ISSUE_TOKEN, webResponse));
  }
}
