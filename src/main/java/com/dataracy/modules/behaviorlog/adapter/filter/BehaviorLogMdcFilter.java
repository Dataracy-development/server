package com.dataracy.modules.behaviorlog.adapter.filter;

import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.behaviorlog.domain.enums.ActionType;
import com.dataracy.modules.behaviorlog.domain.enums.DeviceType;
import com.dataracy.modules.behaviorlog.domain.enums.LogType;
import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import com.dataracy.modules.behaviorlog.support.mdc.MdcKey;
import com.dataracy.modules.behaviorlog.support.parser.UserAgentParser;
import com.dataracy.modules.common.support.enums.HttpMethod;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.security.handler.SecurityContextProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
@Order(1)
@RequiredArgsConstructor
public class BehaviorLogMdcFilter extends OncePerRequestFilter {
    private final BehaviorLogSendProducerPort behaviorLogSendProducerPort;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        try {
            // 로그인 유저라면 SecurityContext 등에서 주입 가능 (이후 AOP로 확장)
            String userId = SecurityContextProvider.getUserId() != null
                    ? String.valueOf(SecurityContextProvider.getUserId())
                    : null;
            MDC.put(MdcKey.USER_ID, userId);
            // 익명 아이디
            String anonymousId = CookieUtil.getOrCreateAnonymousId(request, response);
            MDC.put(MdcKey.ANONYMOUS_ID, anonymousId);
            // 요청 경로, IP, 메서드 등 MDC에 삽입
            MDC.put(MdcKey.REQUEST_ID, UUID.randomUUID().toString());
            // sessionId도 anonymousId와 동일하게 사용 가능
            MDC.put(MdcKey.SESSION_ID, anonymousId);

            MDC.put(MdcKey.IP, request.getRemoteAddr());
            MDC.put(MdcKey.PATH, request.getRequestURI());
            MDC.put(MdcKey.METHOD, request.getMethod());

            filterChain.doFilter(request, response);
        } finally {
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            // User-Agendt
            String userAgent = request.getHeader("User-Agent");

            // 액션 MDC 우선
            ActionType actionType = ActionType.OTHER;
            String actionFromMdc = MDC.get(MdcKey.ACTION);
            if (actionFromMdc != null) {
                actionType = ActionType.valueOf(actionFromMdc);
            }

            // DB 지연
            String dbLatencyStr = MDC.get(MdcKey.DB_LATENCY);
            long dbLatency = dbLatencyStr != null
                    ? Long.parseLong(dbLatencyStr)
                    : 0L;

            //이전 경로
            String referer = request.getHeader("Referer") != null
                    ? request.getHeader("Referer")
                    : MDC.get(MdcKey.REFERRER);

            // 머무른 시간
            String stayTimeStr = MDC.get(MdcKey.STAY_TIME);
            Long stayTime = stayTimeStr != null ? Long.parseLong(stayTimeStr) : 0L;

            BehaviorLog behaviorLog = BehaviorLog
                    .builder()
                    .userId(MDC.get(MdcKey.USER_ID))                                        // 유저 아이디
                    .anonymousId(MDC.get(MdcKey.ANONYMOUS_ID))                              // 익명 아이디
                    .requestId(MDC.get(MdcKey.REQUEST_ID))                                  // UUID
                    .sessionId(MDC.get(MdcKey.SESSION_ID))                                  // 쿠키 기반 세션 추적 ID
                    .path(MDC.get(MdcKey.PATH))                                             // 현재 요청 경로
                    .httpMethod(HttpMethod.valueOf(MDC.get(MdcKey.METHOD)))                 // 메서드
                    .responseTime(responseTime)                                             // 전체 응답시간
                    .userAgent(userAgent)                                                   // 디바이스
                    .ip(MDC.get(MdcKey.IP))                                                 // 클라이언트 IP
                    .action(actionType)                                                     // AOP에서 덮어쓸 수도 있음
                    .dbLatency(dbLatency > 0 ? dbLatency : 0)                               // 추후 AOP에서 삽입
                    .externalLatency(0)                                                     // 추후 AOP에서 삽입
                    .referrer(referer)                                                      // 이전 경로
                    .deviceType(DeviceType.resolve(userAgent))                              // 모바일/PC 판단
                    .logType(LogType.ACTION)                                                // 기본값
                    .browser(UserAgentParser.extractBrowser(userAgent))                     // 사용 브라우저
                    .os(UserAgentParser.extractOS(userAgent))                               // 사용 os
                    .nextPath(MDC.get(MdcKey.NEXT_PATH))                                    // 다음 경로
                    .stayTime(stayTime)                                                     // 머무를 시간
                    .timestamp(Instant.now().toString())                                               // 타임스탬프 (Elasticsearch용)
                    .build();

            if (behaviorLog.isValid()) {
                // Kafka 전송 (Producer 호출)
                behaviorLogSendProducerPort.send(behaviorLog);
            }
            MDC.clear();
        }
    }
}
