package com.dataracy.modules.auth.adapter.web.mapper;

import org.springframework.stereotype.Component;

import com.dataracy.modules.auth.adapter.web.request.RefreshTokenWebRequest;
import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.auth.adapter.web.response.ReIssueTokenWebResponse;
import com.dataracy.modules.auth.adapter.web.response.RefreshTokenWebResponse;
import com.dataracy.modules.auth.application.dto.request.RefreshTokenRequest;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;

/** 개발용 Auth 웹 DTO와 Auth 애플리케이션 DTO를 변환하는 매퍼 */
@Component
public class AuthDevWebMapper {
  /**
   * 자체 로그인 웹 요청 DTO를 애플리케이션 요청 DTO로 변환합니다.
   *
   * @param webRequest 자체 로그인 웹 요청 DTO
   * @return 변환된 자체 로그인 애플리케이션 요청 DTO
   */
  public SelfLoginRequest toApplicationDto(SelfLoginWebRequest webRequest) {
    return new SelfLoginRequest(webRequest.email(), webRequest.password());
  }

  /**
   * 로그인 시 리프레시 토큰 발급 애플리케이션 응답 DTO를 웹 응답 DTO로 변환합니다.
   *
   * @param responseDto 리프레시 토큰 발급 애플리케이션 응답 DTO
   * @return 리프레시 토큰 발급 웹 응답 DTO
   */
  public RefreshTokenWebResponse toWebDto(RefreshTokenResponse responseDto) {
    return new RefreshTokenWebResponse(
        responseDto.refreshToken(), responseDto.refreshTokenExpiration());
  }

  /**
   * 토큰 재발급 웹 요청 DTO를 애플리케이션 요청 DTO로 변환합니다.
   *
   * @param webRequest 웹 계층의 토큰 재발급 요청 DTO
   * @return 애플리케이션 계층의 토큰 재발급 요청 DTO
   */
  public RefreshTokenRequest toApplicationDto(RefreshTokenWebRequest webRequest) {
    return new RefreshTokenRequest(webRequest.refreshToken());
  }

  /**
   * 토큰 재발급 애플리케이션 응답 DTO를 토큰 재발급 웹 응답 DTO로 변환합니다.
   *
   * @param responseDto 토큰 재발급 애플리케이션 응답 DTO
   * @return 토큰 재발급 웹 응답 DTO
   */
  public ReIssueTokenWebResponse toWebDto(ReIssueTokenResponse responseDto) {
    return new ReIssueTokenWebResponse(
        responseDto.accessToken(),
        responseDto.refreshToken(),
        responseDto.accessTokenExpiration(),
        responseDto.refreshTokenExpiration());
  }
}
