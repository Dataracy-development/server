package com.dataracy.modules.behaviorlog.adapter.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 익명 사용자의 행동을 추적하기 위한 Filter
 * 모든 요청에 대해 anonymousId(UUID)를 부여하거나 읽고, ThreadLocal에 저장
 */
@Slf4j
@Component
public class AnonymousIdFilter extends OncePerRequestFilter {

    public static final String HEADER_KEY = "X-ANONYMOUS-ID";
    public static final String COOKIE_KEY = "anonymousId";
    private static final ThreadLocal<String> anonymousIdHolder = new ThreadLocal<>();

    /**
     * 외부에서 현재 요청의 anonymousId를 참조할 수 있도록 제공
     */
    public static String getCurrentAnonymousId() {
        return anonymousIdHolder.get();
    }

    /**
     * 필터 로직: anonymousId를 요청에서 추출하고 없으면 생성 → 응답 쿠키로도 반환
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String anonymousId = extractFromRequest(request);

        if (anonymousId == null || anonymousId.isBlank()) {
            anonymousId = UUID.randomUUID().toString();
            log.debug("새로운 anonymousId 생성: {}", anonymousId);
            setAnonymousIdCookie(response, anonymousId);
        } else {
            log.debug("요청에 anonymousId 포함: {}", anonymousId);
        }

        anonymousIdHolder.set(anonymousId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            // 메모리 누수 방지
            anonymousIdHolder.remove();
        }
    }

    /**
     * 요청에서 쿠키 또는 헤더를 통해 anonymousId 추출
     */
    private String extractFromRequest(HttpServletRequest request) {
        // eader 우선
        String header = request.getHeader(HEADER_KEY);
        if (header != null && !header.isBlank()) return header;

        // 쿠키에서 추출
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (COOKIE_KEY.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 응답에 anonymousId를 쿠키로 추가
     */
    private void setAnonymousIdCookie(HttpServletResponse response, String anonymousId) {
        Cookie cookie = new Cookie(COOKIE_KEY, anonymousId);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge(60 * 60 * 24 * 30); // 30일
        response.addCookie(cookie);
    }
}
