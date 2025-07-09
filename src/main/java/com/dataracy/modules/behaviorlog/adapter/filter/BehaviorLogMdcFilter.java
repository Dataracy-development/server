package com.dataracy.modules.behaviorlog.adapter.filter;

import com.dataracy.modules.behaviorlog.domain.support.MdcKey;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.security.handler.SecurityContextProvider;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class BehaviorLogMdcFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // 요청 경로, IP, 메서드 등 MDC에 삽입
            MDC.put(MdcKey.REQUEST_ID, UUID.randomUUID().toString());
            MDC.put(MdcKey.PATH, httpRequest.getRequestURI());
            MDC.put(MdcKey.METHOD, httpRequest.getMethod());
            MDC.put(MdcKey.IP, httpRequest.getRemoteAddr());
            MDC.put(MdcKey.USER_AGENT, httpRequest.getHeader("User-Agent"));
            MDC.put(MdcKey.REFERRER, httpRequest.getHeader("Referer"));

            // 익명 id 설정
            String anonymousId = CookieUtil.getOrCreateAnonymousId(
                    (HttpServletRequest) request,
                    (HttpServletResponse) response
            );
            MDC.put("anonymousId", anonymousId);

            // 로그인 유저라면 SecurityContext 등에서 주입 가능 (이후 AOP로 확장)
            String userId = String.valueOf(SecurityContextProvider.getUserId());
            MDC.put("userId", userId);

            // sessionId도 anonymousId와 동일하게 사용 가능
            MDC.put(MdcKey.SESSION_ID, anonymousId);

            chain.doFilter(request, response);
        } finally {
            MDC.clear(); // 누수 방지
        }
    }
}
