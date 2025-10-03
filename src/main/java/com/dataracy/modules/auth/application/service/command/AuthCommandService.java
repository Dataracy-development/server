/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.application.service.command;

import java.time.Instant;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.auth.ReIssueTokenUseCase;
import com.dataracy.modules.auth.application.port.in.auth.SelfLoginUseCase;
import com.dataracy.modules.auth.application.port.out.RateLimitPort;
import com.dataracy.modules.auth.application.port.out.jwt.JwtGeneratorPort;
import com.dataracy.modules.auth.application.port.out.jwt.JwtValidatorPort;
import com.dataracy.modules.auth.application.port.out.token.ManageRefreshTokenPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.model.vo.AuthUser;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.support.lock.DistributedLock;
import com.dataracy.modules.user.application.port.in.query.auth.IsLoginPossibleUseCase;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

@Service
public class AuthCommandService implements SelfLoginUseCase, ReIssueTokenUseCase {
  private final JwtProperties jwtProperties;
  private final JwtGeneratorPort jwtGeneratorPort;
  private final JwtValidatorPort jwtValidatorPort;
  private final ManageRefreshTokenPort manageRefreshTokenPort;
  private final RateLimitPort rateLimitPort;
  private final IsLoginPossibleUseCase isLoginPossibleUseCase;

  // Use Case 상수 정의
  private static final String SELF_LOGIN_USE_CASE = "SelfLoginUseCase";
  private static final String RE_ISSUE_TOKEN_USE_CASE = "ReIssueTokenUseCase";

  public AuthCommandService(
      JwtProperties jwtProperties,
      JwtGeneratorPort jwtGeneratorPort,
      JwtValidatorPort jwtValidatorPort,
      ManageRefreshTokenPort manageRefreshTokenPort,
      @Qualifier("redisRateLimitAdapter") RateLimitPort rateLimitPort,
      IsLoginPossibleUseCase isLoginPossibleUseCase) {
    this.jwtProperties = jwtProperties;
    this.jwtGeneratorPort = jwtGeneratorPort;
    this.jwtValidatorPort = jwtValidatorPort;
    this.manageRefreshTokenPort = manageRefreshTokenPort;
    this.rateLimitPort = rateLimitPort;
    this.isLoginPossibleUseCase = isLoginPossibleUseCase;
  }

  /**
   * 사용자의 이메일과 비밀번호를 검증하여 로그인한 후, 새로운 리프레시 토큰을 발급한다.
   *
   * @param requestDto 로그인 요청 정보(이메일, 비밀번호 등)
   * @return 발급된 리프레시 토큰과 만료 시간이 포함된 응답 객체
   */
  @Override
  @Transactional
  public RefreshTokenResponse login(SelfLoginRequest requestDto) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(SELF_LOGIN_USE_CASE, "자체 로그인 서비스 시작 email=" + requestDto.email());

    // 유저 db로부터 이메일이 일치하는 유저를 조회한다.
    UserInfo userInfo =
        isLoginPossibleUseCase.checkLoginPossibleAndGetUserInfo(
            requestDto.email(), requestDto.password());
    AuthUser authUser = AuthUser.from(userInfo);

    // 로그인 가능한 경우이므로 리프레시 토큰 발급 및 레디스에 저장
    String refreshToken = jwtGeneratorPort.generateRefreshToken(authUser.userId(), authUser.role());
    manageRefreshTokenPort.saveRefreshToken(authUser.userId().toString(), refreshToken);
    RefreshTokenResponse refreshTokenResponse =
        new RefreshTokenResponse(refreshToken, jwtProperties.getRefreshTokenExpirationTime());

    LoggerFactory.service()
        .logSuccess(SELF_LOGIN_USE_CASE, "자체 로그인 서비스 성공 email=" + requestDto.email(), startTime);
    return refreshTokenResponse;
  }

  /** 정상 사용자인지 판별하는 간단한 로직 실제로는 DB에서 사용자 신뢰도, 로그인 패턴 등을 분석해야 함 */
  private boolean isNormalUser(String email) {
    // 실무적 사용자 구분 로직 (주니어 개발자 수준)
    if (email == null) return false;

    // 1. 신뢰할 수 있는 도메인 기반 판별 (일반적인 이메일 서비스)
    String[] trustedDomains = {
      "gmail.com", "naver.com", "daum.net", "kakao.com", "outlook.com", "yahoo.com", "hotmail.com"
    };

    for (String domain : trustedDomains) {
      if (email.endsWith("@" + domain)) {
        return true;
      }
    }

    // 2. 테스트용 이메일 허용
    if (email.equals("wnsgudAws@gmail.com")) {
      return true;
    }

    // 3. 의심스러운 패턴 감지 (공격자로 간주)
    String[] suspiciousPatterns = {"attacker", "hack", "brute", "test", "admin"};

    for (String pattern : suspiciousPatterns) {
      if (email.toLowerCase(Locale.ENGLISH).contains(pattern)) {
        return false;
      }
    }

    // 4. 기본적으로는 정상 사용자로 간주 (관대한 접근)
    return true;
  }

  /**
   * 레이트 리미팅이 적용된 로그인 (IP 기반)
   *
   * @param requestDto 로그인 요청 정보
   * @param clientIp 클라이언트 IP 주소
   * @return 발급된 리프레시 토큰과 만료 시간이 포함된 응답 객체
   */
  @Override
  @Transactional
  public RefreshTokenResponse loginWithRateLimit(SelfLoginRequest requestDto, String clientIp) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(SELF_LOGIN_USE_CASE, "레이트 리미팅 적용 로그인 서비스 시작 email=" + requestDto.email());

    // 1. 레이트 리미팅 검증 (인증 전에 먼저 체크하여 brute-force 공격 방지)
    validateRateLimit(requestDto.email(), clientIp);

    // 2. 사용자 인증
    UserInfo userInfo = authenticateUser(requestDto);

    // 3. 토큰 발급 및 반환
    RefreshTokenResponse response = generateRefreshTokenResponse(userInfo);

    LoggerFactory.service()
        .logSuccess(
            SELF_LOGIN_USE_CASE, "레이트 리미팅 적용 로그인 서비스 성공 email=" + requestDto.email(), startTime);
    return response;
  }

  /** 사용자 인증 처리 */
  private UserInfo authenticateUser(SelfLoginRequest requestDto) {
    return isLoginPossibleUseCase.checkLoginPossibleAndGetUserInfo(
        requestDto.email(), requestDto.password());
  }

  /**
   * 레이트 리미팅 검증
   *
   * <p>주의: RedisRateLimitAdapter의 isAllowed 메서드는 내부에서 카운트를 증가시키므로 별도로 incrementRequestCount를 호출할
   * 필요가 없습니다.
   */
  private void validateRateLimit(String email, String clientIp) {
    if (clientIp == null) return;

    String rateLimitKey = email + ":" + clientIp;
    int maxRequests = isNormalUser(email) ? 60 : 5;

    LoggerFactory.service()
        .logInfo(
            SELF_LOGIN_USE_CASE,
            String.format("레이트 리미팅 확인 - 사용자: %s, IP: %s, 제한: %d회/분", email, clientIp, maxRequests));

    if (!rateLimitPort.isAllowed(rateLimitKey, maxRequests, 1)) {
      LoggerFactory.service()
          .logWarning(
              SELF_LOGIN_USE_CASE,
              String.format(
                  "레이트 리미팅 초과 - 사용자: %s, IP: %s, 제한: %d회/분", email, clientIp, maxRequests));
      throw new AuthException(AuthErrorStatus.RATE_LIMIT_EXCEEDED);
    }

    // incrementRequestCount 호출 제거 - isAllowed에서 이미 카운트 증가
    LoggerFactory.service()
        .logInfo(
            SELF_LOGIN_USE_CASE, String.format("레이트 리미팅 통과 - 사용자: %s, IP: %s", email, clientIp));
  }

  /** 리프레시 토큰 응답 생성 */
  private RefreshTokenResponse generateRefreshTokenResponse(UserInfo userInfo) {
    AuthUser authUser = AuthUser.from(userInfo);

    String refreshToken = jwtGeneratorPort.generateRefreshToken(authUser.userId(), authUser.role());
    manageRefreshTokenPort.saveRefreshToken(authUser.userId().toString(), refreshToken);

    return new RefreshTokenResponse(refreshToken, jwtProperties.getRefreshTokenExpirationTime());
  }

  /**
   * 리프레시 토큰을 검증하고 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
   *
   * <p>분산 락을 적용하여 동일한 리프레시 토큰으로의 동시 재발급을 방지하며, 저장된 리프레시 토큰과 입력된 토큰이 일치할 때만 새로운 토큰을 생성합니다. 토큰이
   * 만료되었거나 일치하지 않을 경우 인증 예외가 발생합니다.
   *
   * @param refreshToken 클라이언트가 제공한 리프레시 토큰
   * @return 새로 발급된 액세스 토큰과 리프레시 토큰, 각 만료 시간이 포함된 응답 객체
   */
  @Override
  @DistributedLock(
      key = "'lock:refresh-reissue:' + #refreshToken",
      waitTime = 200L,
      leaseTime = 1000L,
      retry = 1)
  @Transactional
  public ReIssueTokenResponse reIssueToken(String refreshToken) {
    try {
      Instant startTime =
          LoggerFactory.service().logStart(RE_ISSUE_TOKEN_USE_CASE, "토큰 재발급 서비스 시작");

      // 쿠키의 리프레시 토큰으로 유저 아이디를 반환한다.
      Long userId = jwtValidatorPort.getUserIdFromToken(refreshToken);
      if (userId == null) {
        LoggerFactory.service().logWarning(RE_ISSUE_TOKEN_USE_CASE, "[토큰 재발급] 만료된 리프레시 토큰입니다.");
        throw new AuthException(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
      }

      // 레디스의 리프레시 토큰과 입력받은 리프레시 토큰을 비교한다.
      String savedRefreshToken = manageRefreshTokenPort.getRefreshToken(userId.toString());
      if (savedRefreshToken == null) {
        LoggerFactory.service()
            .logWarning(RE_ISSUE_TOKEN_USE_CASE, "[토큰 재발급] 저장된 리프레시 토큰이 없습니다. 만료되었을 수 있습니다.");
        throw new AuthException(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
      }
      if (!savedRefreshToken.equals(refreshToken)) {
        LoggerFactory.service()
            .logWarning(RE_ISSUE_TOKEN_USE_CASE, "[토큰 재발급] 입력된 리프레시 토큰이 레디스의 리프레시 토큰과 일치하지 않습니다.");
        throw new AuthException(AuthErrorStatus.REFRESH_TOKEN_USER_MISMATCH_IN_REDIS);
      }

      // 어세스 토큰과 리프레시 토큰을 발급 후 반환한다.
      RoleType userRole = jwtValidatorPort.getRoleFromToken(refreshToken);
      String newAccessToken = jwtGeneratorPort.generateAccessToken(userId, userRole);
      String newRefreshToken = jwtGeneratorPort.generateRefreshToken(userId, userRole);

      // 레디스에 리프레시 토큰 저장
      manageRefreshTokenPort.saveRefreshToken(userId.toString(), newRefreshToken);
      ReIssueTokenResponse reIssueTokenResponse =
          new ReIssueTokenResponse(
              newAccessToken,
              newRefreshToken,
              jwtProperties.getAccessTokenExpirationTime(),
              jwtProperties.getRefreshTokenExpirationTime());

      LoggerFactory.service().logSuccess(RE_ISSUE_TOKEN_USE_CASE, "토큰 재발급 서비스 성공", startTime);
      return reIssueTokenResponse;
    } catch (AuthException e) {
      if (e.getErrorCode() == AuthErrorStatus.EXPIRED_TOKEN) {
        LoggerFactory.service().logWarning(RE_ISSUE_TOKEN_USE_CASE, "[토큰 재발급] 만료된 리프레시 토큰입니다.");
        throw new AuthException(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
      } else if (e.getErrorCode() == AuthErrorStatus.INVALID_TOKEN) {
        LoggerFactory.service().logWarning(RE_ISSUE_TOKEN_USE_CASE, "[토큰 재발급] 유효하지 않은 리프레시 토큰입니다.");
        throw new AuthException(AuthErrorStatus.INVALID_REFRESH_TOKEN);
      } else {
        LoggerFactory.service().logWarning(RE_ISSUE_TOKEN_USE_CASE, "[토큰 재발급] 인증에 실패했습니다.");
        throw e;
      }
    } catch (Exception e) {
      LoggerFactory.service().logException(RE_ISSUE_TOKEN_USE_CASE, "[토큰 재발급] 내부 서버 오류입니다.", e);
      throw e;
    }
  }
}
