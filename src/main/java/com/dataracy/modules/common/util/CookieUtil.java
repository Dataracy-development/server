package com.dataracy.modules.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public final class CookieUtil {
    private CookieUtil() {
    }

    private static final String ANONYMOUS_ID_COOKIE_NAME = "anonymousId";
    private static final int COOKIE_EXPIRE_SECONDS = 60 * 60 * 24 * 30; // 30일

    /**
     * HTTP-Only, Secure 쿠키를 설정합니다.
     *
     * @param response HTTP 응답 객체
     * @param name     쿠키 이름
     * @param value    쿠키 값
     * @param maxAge   쿠키 만료 시간 (초)
     */
    public static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain("dataracy.co.kr");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * HTTP 요청의 쿠키에서 "refreshToken" 값을 찾아 반환합니다.
     *
     * 요청에 "refreshToken" 쿠키가 없으면 빈 Optional을 반환합니다.
     *
     * @param request HTTP 요청 객체
     * @return "refreshToken" 쿠키 값이 존재하면 해당 값을 포함한 Optional, 없으면 빈 Optional
     */
    public static Optional<String> getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    /**
     * HTTP 요청에서 "anonymousId" 쿠키 값을 반환하거나, 존재하지 않을 경우 새로 생성하여 응답에 설정한 후 반환합니다.
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return "anonymousId" 쿠키의 기존 값 또는 새로 생성된 UUID 문자열
     */
    public static String getOrCreateAnonymousId(HttpServletRequest request, HttpServletResponse response) {
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
        setCookie(
                response,
                ANONYMOUS_ID_COOKIE_NAME,
                newAnonymousId,
                COOKIE_EXPIRE_SECONDS
        );
        return newAnonymousId;
    }
}
