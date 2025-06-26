package com.dataracy.common.util;

import com.dataracy.common.status.CommonErrorStatus;
import com.dataracy.common.status.CommonException;
import com.dataracy.user.status.AuthErrorStatus;
import com.dataracy.user.status.AuthException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    private CookieUtil() {
        throw new CommonException(CommonErrorStatus.CAN_NOT_INSTANTIATE_COOKIE_UTILITY_CLASS);
    }

    /**
     * 유저 온보딩을 위한 쿠키를 설정합니다 (Register Token, Expiration Time).
     *
     * @param response          HTTP 응답 객체
     * @param registerToken       레지스터 토큰
     * @param registerTokenExpiry 레지스터 토큰 만료 시간 (밀리초)
     */
    public static void setRegisterTokenInCookies(
            HttpServletResponse response,
            String registerToken,
            long registerTokenExpiry
    ) {
        setCookie(response, "registerToken", registerToken, (int) registerTokenExpiry / 1000);
    }

    /**
     * 리프레시토큰 쿠키를 설정합니다 (Refresh Token, Expiration Time).
     *
     * @param response          HTTP 응답 객체
     * @param refreshToken      리프레시 토큰
     * @param refreshTokenExpiry 리프레시 토큰 만료 시간 (밀리초)
     */
    public static void setRefreshTokenInCookies(
            HttpServletResponse response,
            String refreshToken,
            long refreshTokenExpiry
    ) {
        setCookie(response, "refreshToken", refreshToken, (int) refreshTokenExpiry / 1000);
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
     * 쿠키에서 name에 해당하는 값을 추출합니다.
     *
     * @param request HTTP 요청 객체
     * @return refreshToken 값
     * @throws AuthException 해당 Refresh Token이 없는 경우
     */
    public static String getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null || request.getCookies().length == 0) {
            throw new AuthException(AuthErrorStatus.NOT_FOUND_REFRESH_TOKEN_IN_COOKIES);
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("refreshToken")) {
                return cookie.getValue();
            }
        }
        throw new AuthException(AuthErrorStatus.NOT_FOUND_REFRESH_TOKEN_IN_COOKIES);
    }
}
