package com.dataracy.modules.common.util;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;

public final class CookieUtil {

    private CookieUtil() {
        throw new CommonException(CommonErrorStatus.CAN_NOT_INSTANTIATE_COOKIE_UTILITY_CLASS);
    }

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
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * 쿠키에서 refreshToken에 해당하는 값을 추출합니다.
     *
     * @param request HTTP 요청 객체
     * @return refreshToken 값
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
     * 쿠키에서 anonymousId에 해당하는 값을 추출합니다.
     *
     * @param request HTTP 요청 객체
     * @return anonymousId 값
     */
    public static Optional<String> getAnonymousIdFromCookies(HttpServletRequest request) {
        // 쿠키에서 익명 ID 찾기
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("anonymousId".equals(cookie.getName())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }
}
