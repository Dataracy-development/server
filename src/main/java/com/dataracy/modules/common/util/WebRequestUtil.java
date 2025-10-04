package com.dataracy.modules.common.util;

import java.util.List;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;

public final class WebRequestUtil {
  private WebRequestUtil() {}

  /**
   * 현재 스레드에 바인딩된 HttpServletRequest 객체를 안전하게 반환합니다. 요청 객체가 존재하지 않으면 null을 반환합니다.
   *
   * @return 현재 요청의 HttpServletRequest 객체 또는 요청이 없을 경우 null
   */
  @Nullable
  public static HttpServletRequest getCurrentRequestSafely() {
    try {
      return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
          .getRequest();
    } catch (IllegalStateException e) {
      return null;
    }
  }

  /**
   * Swagger, static 등 로그 예외 처리 요청인지 확인 주어진 HTTP 요청이 로그에서 제외되어야 하는 경로에 해당하는지 확인합니다. Swagger UI, API
   * 문서, 정적 리소스, 오류 페이지, favicon 등과 관련된 URI 요청은 로그 예외 대상으로 간주됩니다.
   *
   * @param request 검사할 HttpServletRequest 객체
   * @return 로그 예외 대상 경로일 경우 true, 그렇지 않으면 false
   */
  public static boolean isLogExceptRequest(HttpServletRequest request) {
    if (request == null) return false;
    String uri = request.getRequestURI();
    return EXCLUDED_PREFIXES.stream().anyMatch(uri::startsWith)
        || EXCLUDED_EQUALS.stream().anyMatch(uri::equals);
  }

  private static final List<String> EXCLUDED_PREFIXES =
      List.of(
          "/swagger",
          "/v3/api-docs",
          "/swagger-ui",
          "/swagger-resources",
          "/.well-known",
          "/webjars",
          "/static",
          "/error");

  private static final List<String> EXCLUDED_EQUALS = List.of("/swagger-ui.html", "/favicon.ico");
}
