package com.dataracy.modules.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component
public class CookieUtil {
    private static final String ANONYMOUS_ID_COOKIE_NAME = "anonymousId";
    private static final int COOKIE_EXPIRE_SECONDS = 60 * 60 * 24 * 30; // 30일
    
    @Value("${spring.profiles.active:local}")
    private String activeProfile;
    
    @Value("${server.url:http://localhost:8080}")
    private String serverUrl;

    /**
     * 지정한 이름, 값, 만료 시간을 가진 HTTP-Only 쿠키를 HTTP 응답에 추가합니다.
     * 환경에 따라 Secure, SameSite 설정이 동적으로 적용됩니다.
     *
     * @param name   쿠키의 이름
     * @param value  쿠키의 값
     * @param maxAge 쿠키의 만료 시간(초 단위)
     */
    public void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        boolean isSecure = isSecureEnvironment();
        String sameSite = getSameSitePolicy();
        
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(isSecure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ofSeconds(maxAge))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
    
    /**
     * 현재 환경이 HTTPS를 사용하는지 확인합니다.
     */
    private boolean isSecureEnvironment() {
        return "prod".equals(activeProfile) || serverUrl.startsWith("https://");
    }
    
    /**
     * 환경에 따른 SameSite 정책을 반환합니다.
     */
    private String getSameSitePolicy() {
        if ("prod".equals(activeProfile)) {
            return "Strict"; // 운영 환경: Strict
        } else if ("dev".equals(activeProfile)) {
            return "Lax"; // 개발 환경: Lax
        } else {
            return "None"; // 로컬 환경: None
        }
    }

    /**
     * HTTP 요청의 쿠키에서 "refreshToken" 값을 찾아 반환합니다.
     * 요청에 "refreshToken" 쿠키가 없으면 빈 Optional을 반환합니다.
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
        setCookie(
                response,
                ANONYMOUS_ID_COOKIE_NAME,
                newAnonymousId,
                COOKIE_EXPIRE_SECONDS
        );
        return newAnonymousId;
    }
}
