package com.dataracy.modules.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WebRequestUtil {

    /**
     * 현재 요청 객체를 안전하게 가져옵니다. 없으면 null 반환
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
