package com.dataracy.modules.common.util;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieUtil {
  private static final String ANONYMOUS_ID_COOKIE_NAME = "anonymousId";
  private static final int COOKIE_EXPIRE_SECONDS = 60 * 60 * 24 * 30; // 30일

  @Value("${spring.profiles.active:local}")
  private String activeProfile;

  /**
   * 지정한 이름, 값, 만료 시간을 가진 HTTP-Only 쿠키를 HTTP 응답에 추가합니다. 실제 요청의 프로토콜을 감지하여 Secure 설정을 동적으로 적용합니다.
   *
   * @param request HTTP 요청 객체 (프로토콜 감지용)
   * @param response HTTP 응답 객체
   * @param name 쿠키의 이름
   * @param value 쿠키의 값
   * @param maxAge 쿠키의 만료 시간(초 단위)
   */
  public void setCookie(
      HttpServletRequest request,
      HttpServletResponse response,
      String name,
      String value,
      int maxAge) {
    boolean isSecure = isSecureEnvironment(request);
    String sameSite = getSameSitePolicy(request);
    String domain = getCookieDomain();

    ResponseCookie cookie =
        ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(isSecure)
            .sameSite(sameSite)
            .domain(domain)
            .path("/")
            .maxAge(Duration.ofSeconds(maxAge))
            .build();

    response.addHeader("Set-Cookie", cookie.toString());
  }

  /** 실제 요청의 프로토콜을 감지하여 HTTPS 사용 여부를 확인합니다. 개발 환경에서 HTTP/HTTPS 모두 지원할 때 사용합니다. */
  private boolean isSecureEnvironment(HttpServletRequest request) {
    if ("prod".equals(activeProfile)) {
      return true; // 운영 환경은 항상 HTTPS
    } else if ("dev".equals(activeProfile)) {
      // 개발 환경: 실제 요청 프로토콜 감지
      return isHttpsRequest(request);
    } else {
      // 로컬 환경: 실제 요청 프로토콜 감지
      return isHttpsRequest(request);
    }
  }

  /** HTTP 요청이 HTTPS인지 확인합니다. X-Forwarded-Proto 헤더와 isSecure() 메서드를 모두 확인합니다. */
  private boolean isHttpsRequest(HttpServletRequest request) {
    // 1. X-Forwarded-Proto 헤더 확인 (프록시/로드밸런서 뒤에서 사용)
    String forwardedProto = request.getHeader("X-Forwarded-Proto");
    if (forwardedProto != null) {
      return "https".equalsIgnoreCase(forwardedProto);
    }

    // 2. X-Forwarded-Ssl 헤더 확인 (일부 프록시에서 사용)
    String forwardedSsl = request.getHeader("X-Forwarded-Ssl");
    if (forwardedSsl != null) {
      return "on".equalsIgnoreCase(forwardedSsl);
    }

    // 3. 서블릿의 isSecure() 메서드 확인
    return request.isSecure();
  }

  /** 환경에 따른 SameSite 정책을 반환합니다. HTTPS에서는 None, HTTP에서는 Lax를 사용합니다. */
  private String getSameSitePolicy(HttpServletRequest request) {
    boolean isSecure = isSecureEnvironment(request);

    if ("prod".equals(activeProfile)) {
      return "None"; // 운영 환경: 항상 HTTPS이므로 None
    } else if ("dev".equals(activeProfile)) {
      return isSecure ? "None" : "Lax"; // 개발 환경: HTTPS면 None, HTTP면 Lax
    } else {
      return isSecure ? "None" : "Lax"; // 로컬 환경: HTTPS면 None, HTTP면 Lax
    }
  }

  /**
   * 환경에 따른 쿠키 도메인을 반환합니다. dataracy.store, api.dataracy.store, dev-api.dataracy.store 간의 쿠키 공유를 위해
   * 상위 도메인으로 설정합니다.
   */
  private String getCookieDomain() {
    if ("prod".equals(activeProfile)) {
      return ".dataracy.store"; // 운영 환경: dataracy.store ↔ api.dataracy.store 쿠키 공유
    } else if ("dev".equals(activeProfile)) {
      return ".dataracy.store"; // 개발 환경: dataracy.store ↔ dev-api.dataracy.store 쿠키 공유
    } else {
      return null; // 로컬 환경: localhost에서는 도메인 설정 없음
    }
  }

  /**
   * HTTP 요청의 쿠키에서 "refreshToken" 값을 찾아 반환합니다. 요청에 "refreshToken" 쿠키가 없으면 빈 Optional을 반환합니다.
   *
   * @param request HTTP 요청 객체
   * @return "refreshToken" 쿠키 값이 존재하면 해당 값을 포함한 Optional, 없으면 빈 Optional
   */
  public Optional<String> getRefreshTokenFromCookies(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return Optional.empty();
    }
    return Arrays.stream(request.getCookies())
        .filter(cookie -> "refreshToken".equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst();
  }

  /**
   * 지정한 이름의 쿠키를 삭제합니다. 쿠키를 삭제하기 위해 maxAge를 0으로 설정하고 빈 값을 설정합니다.
   *
   * @param request HTTP 요청 객체 (프로토콜 감지용)
   * @param response HTTP 응답 객체
   * @param name 삭제할 쿠키의 이름
   */
  public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
    boolean isSecure = isSecureEnvironment(request);
    String sameSite = getSameSitePolicy(request);
    String domain = getCookieDomain();

    ResponseCookie cookie =
        ResponseCookie.from(name, "")
            .httpOnly(true)
            .secure(isSecure)
            .sameSite(sameSite)
            .domain(domain)
            .path("/")
            .maxAge(Duration.ofSeconds(0))
            .build();

    response.addHeader("Set-Cookie", cookie.toString());
  }

  /**
   * 로그아웃 시 모든 인증 관련 쿠키를 삭제합니다. accessToken, refreshToken, accessTokenExpiration,
   * refreshTokenExpiration, registerToken 쿠키를 모두 삭제합니다.
   *
   * @param request HTTP 요청 객체 (프로토콜 감지용)
   * @param response HTTP 응답 객체
   */
  public void deleteAllAuthCookies(HttpServletRequest request, HttpServletResponse response) {
    deleteCookie(request, response, "accessToken");
    deleteCookie(request, response, "refreshToken");
    deleteCookie(request, response, "accessTokenExpiration");
    deleteCookie(request, response, "refreshTokenExpiration");
    deleteCookie(request, response, "registerToken");
  }

  /**
   * HTTP 요청에서 "anonymousId" 쿠키 값을 반환하거나, 존재하지 않을 경우 새로 생성하여 응답에 설정한 후 반환합니다.
   *
   * @param request HTTP 요청 객체
   * @param response HTTP 응답 객체
   * @return "anonymousId" 쿠키의 기존 값 또는 새로 생성된 UUID 문자열
   */
  public String getOrCreateAnonymousId(HttpServletRequest request, HttpServletResponse response) {
    // 쿠키에서 익명id 조회 및 조회
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (ANONYMOUS_ID_COOKIE_NAME.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }

    // 없으면 새로 생성
    String newAnonymousId = UUID.randomUUID().toString();
    setCookie(request, response, ANONYMOUS_ID_COOKIE_NAME, newAnonymousId, COOKIE_EXPIRE_SECONDS);
    return newAnonymousId;
  }
}
