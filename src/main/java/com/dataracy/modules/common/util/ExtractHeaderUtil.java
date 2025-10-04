package com.dataracy.modules.common.util;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExtractHeaderUtil {
  private final JwtValidateUseCase jwtValidateUseCase;
  private final CookieUtil cookieUtil;

  /**
   * HTTP 요청의 Authorization 헤더에서 Bearer 타입의 액세스 토큰을 추출합니다.
   *
   * @param request 액세스 토큰을 추출할 HttpServletRequest 객체
   * @return Bearer 토큰이 존재하면 해당 토큰을, 없거나 형식이 올바르지 않으면 빈 Optional을 반환합니다.
   */
  public static Optional<String> extractAccessToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      return Optional.empty();
    }

    return Optional.of(header.substring(7));
  }

  /**
   * HTTP 요청에서 JWT 액세스 토큰을 추출하고, 해당 토큰에서 인증된 사용자 ID를 반환합니다.
   *
   * @param request 사용자 인증 정보를 포함할 수 있는 HTTP 요청
   * @return 인증된 사용자 ID(Long) 또는 인증 실패 시 null
   */
  public Long extractAuthenticatedUserIdFromRequest(HttpServletRequest request) {
    try {
      return extractAccessToken(request).map(jwtValidateUseCase::getUserIdFromToken).orElse(null);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * HTTP 요청에서 인증된 사용자 ID를 추출하거나, 인증에 실패할 경우 익명 ID를 반환합니다.
   *
   * <p>인증 토큰이 유효하고 사용자 ID를 추출할 수 있으면 해당 ID를 문자열로 반환하며, 그렇지 않은 경우 쿠키를 통해 익명 ID를 생성하거나 반환합니다.
   *
   * @param request 사용자 요청 객체
   * @param response 응답 객체 (익명 ID 생성 시 쿠키 설정에 사용)
   * @return 인증된 사용자 ID 또는 익명 ID 문자열
   */
  public String extractViewerIdFromRequest(
      HttpServletRequest request, HttpServletResponse response) {
    // 인증된 유저라면 accessToken → userId 추출
    try {
      Optional<String> accessToken = extractAccessToken(request);
      if (accessToken.isPresent()) {
        Long userId = jwtValidateUseCase.getUserIdFromToken(accessToken.get());
        if (userId != null) {
          return String.valueOf(userId);
        }
      }
    } catch (Exception ignored) {
      // 인증 실패 시 무시하고 anonymousId로 대체
      return cookieUtil.getOrCreateAnonymousId(request, response);
    }
    // anonymousId 반환
    return cookieUtil.getOrCreateAnonymousId(request, response);
  }
}
