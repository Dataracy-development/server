package com.dataracy.modules.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WebRequestUtil {
    /**
     * 인스턴스 생성을 방지하기 위해 예외를 발생시키는 private 생성자입니다.
     */
    private WebRequestUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 현재 스레드에 바인딩된 HttpServletRequest 객체를 안전하게 반환합니다.
     *
     * 요청 객체가 존재하지 않으면 null을 반환합니다.
     *
     * @return 현재 요청의 HttpServletRequest 객체 또는 요청이 없을 경우 null
     */
    public static HttpServletRequest getCurrentRequestSafely() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (IllegalStateException e) {
            return null;
        }
    }

    /**
     * Swagger, static 등 로그 예외 처리 요청인지 확인
     * 주어진 HTTP 요청이 로그에서 제외되어야 하는 경로에 해당하는지 확인합니다.
     *
     * Swagger UI, API 문서, 정적 리소스, 오류 페이지, favicon 등과 관련된 URI 요청은 로그 예외 대상으로 간주됩니다.
     *
     * @param request 검사할 HttpServletRequest 객체
     * @return 로그 예외 대상 경로일 경우 true, 그렇지 않으면 false
     */
    public static boolean isLogExceptRequest(HttpServletRequest request) {
        if (request == null) return false;
        String uri = request.getRequestURI();
        return uri.startsWith("/swagger")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/swagger-resources")
                || uri.startsWith("/.well-known")
                || uri.startsWith("/webjars")
                || uri.startsWith("/static")
                || uri.startsWith("/error")
                || uri.equals("/swagger-ui.html")
                || uri.equals("/favicon.ico")
                ;
    }
}
