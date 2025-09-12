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
     * 지정한 이름, 값, 만료 시간을 가진 HTTP-Only 쿠키를 HTTP 응답에 추가합니다.
     * 실제 요청의 프로토콜을 감지하여 Secure 설정을 동적으로 적용합니다.
     *
     * @param request HTTP 요청 객체 (프로토콜 감지용)
     * @param response HTTP 응답 객체
     * @param name   쿠키의 이름
     * @param value  쿠키의 값
     * @param maxAge 쿠키의 만료 시간(초 단위)
     */
    public void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge) {
        boolean isSecure = isSecureEnvironment(request);
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
     * (기존 메서드 - request 객체가 없을 때 사용)
     */
    private boolean isSecureEnvironment() {
        if ("prod".equals(activeProfile)) {
            return true; // 운영 환경은 항상 HTTPS
        } else {
            // 개발/로컬 환경: 기본적으로 false (실제 요청에서는 request 기반으로 판단)
            return false;
        }
    }

    /**
     * 실제 요청의 프로토콜을 감지하여 HTTPS 사용 여부를 확인합니다.
     * 개발 환경에서 HTTP/HTTPS 모두 지원할 때 사용합니다.
     */
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

    /**
     * HTTP 요청이 HTTPS인지 확인합니다.
     * X-Forwarded-Proto 헤더와 isSecure() 메서드를 모두 확인합니다.
     */
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
    
    /**
     * 환경에 따른 SameSite 정책을 반환합니다.
     */
    private String getSameSitePolicy() {
        if ("prod".equals(activeProfile)) {
            return "Strict"; // 운영 환경: Strict
        } else if ("dev".equals(activeProfile)) {
            return "Lax"; // 개발 환경: Lax (HTTP/HTTPS 모두 지원)
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
                request,
                response,
                ANONYMOUS_ID_COOKIE_NAME,
                newAnonymousId,
                COOKIE_EXPIRE_SECONDS
        );
        return newAnonymousId;
    }
}
