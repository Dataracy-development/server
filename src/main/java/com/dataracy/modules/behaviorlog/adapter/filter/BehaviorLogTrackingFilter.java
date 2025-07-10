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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class BehaviorLogTrackingFilter extends OncePerRequestFilter {

    private final BehaviorLogSendProducerPort producerPort;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        try {
            initMDC(request, response);
            chain.doFilter(request, response);
        } finally {
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            BehaviorLog log = buildBehaviorLog(request, responseTime);
            if (log.isValid()) {
                producerPort.send(log);
            }

            MDC.clear();
        }
    }

    private void initMDC(HttpServletRequest request, HttpServletResponse response) {
        String userId = SecurityContextProvider.getUserId() != null
                ? String.valueOf(SecurityContextProvider.getUserId())
                : null;
        MDC.put(MdcKey.USER_ID, userId);

        String anonymousId = CookieUtil.getOrCreateAnonymousId(request, response);
        MDC.put(MdcKey.ANONYMOUS_ID, anonymousId);
        MDC.put(MdcKey.REQUEST_ID, UUID.randomUUID().toString());
        MDC.put(MdcKey.SESSION_ID, anonymousId);
        MDC.put(MdcKey.IP, request.getRemoteAddr());
        MDC.put(MdcKey.PATH, request.getRequestURI());
        MDC.put(MdcKey.METHOD, request.getMethod());
    }

    private BehaviorLog buildBehaviorLog(HttpServletRequest request, long responseTime) {
        String userAgent = request.getHeader("User-Agent");
        ActionType actionType = ActionType.valueOf(MDC.get(MdcKey.ACTION));
        long dbLatency = parseLong(MDC.get(MdcKey.DB_LATENCY));
        long stayTime = parseLong(MDC.get(MdcKey.STAY_TIME));

        String referrer = MDC.get(MdcKey.REFERRER) != null
                ? MDC.get(MdcKey.REFERRER)
                : request.getHeader("Referer");


        return BehaviorLog.builder()
                .userId(MDC.get(MdcKey.USER_ID))
                .anonymousId(MDC.get(MdcKey.ANONYMOUS_ID))
                .requestId(MDC.get(MdcKey.REQUEST_ID))
                .sessionId(MDC.get(MdcKey.SESSION_ID))
                .path(MDC.get(MdcKey.PATH))
                .httpMethod(HttpMethod.valueOf(MDC.get(MdcKey.METHOD)))
                .responseTime(responseTime)
                .userAgent(userAgent)
                .ip(MDC.get(MdcKey.IP))
                .action(actionType)
                .dbLatency(dbLatency)
                .externalLatency(0) // TODO: 외부 호출 추적 시 채워넣기
                .referrer(referrer)
                .deviceType(DeviceType.resolve(userAgent))
                .logType(LogType.ACTION)
                .browser(UserAgentParser.extractBrowser(userAgent))
                .os(UserAgentParser.extractOS(userAgent))
                .nextPath(MDC.get(MdcKey.NEXT_PATH))
                .stayTime(stayTime)
                .timestamp(Instant.now().toString())
                .build();
    }

    private long parseLong(String str) {
        try {
            return str != null ? Long.parseLong(str) : 0L;
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
