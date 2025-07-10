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
import com.dataracy.modules.common.util.WebRequestUtil;
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

    /**
     * HTTP 요청을 가로채어 사용자 행동 로그를 비동기적으로 수집 및 전송하는 필터의 핵심 메서드입니다.
     *
     * 로그 제외 대상 요청은 필터 체인을 바로 진행하며, 그 외 요청에 대해서는 MDC를 초기화하고 요청 처리 시간을 측정한 후, 행동 로그를 생성하여 전송합니다.
     * 요청 처리 후 MDC를 반드시 정리합니다.
     *
     * @param request  현재 처리 중인 HTTP 요청
     * @param response 현재 처리 중인 HTTP 응답
     * @param chain    필터 체인
     * @throws ServletException 필터 처리 중 서블릿 예외가 발생한 경우
     * @throws IOException      입출력 예외가 발생한 경우
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (WebRequestUtil.isLogExceptRequest(request)) {
            chain.doFilter(request, response); // 꼭 체인 호출
            return;
        }

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

    /**
     * HTTP 요청 및 세션 정보를 기반으로 MDC(Mapped Diagnostic Context)를 초기화합니다.
     *
     * 사용자 ID, 익명 ID, 요청 ID, 세션 ID, 클라이언트 IP, 요청 경로, HTTP 메서드 정보를 MDC에 저장하여
     * 요청 처리 중 일관된 로깅 컨텍스트를 제공합니다.
     *
     * @param request  현재 HTTP 요청 객체
     * @param response 현재 HTTP 응답 객체
     */
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

    /**
     * HTTP 요청 및 MDC 정보를 기반으로 BehaviorLog 객체를 생성합니다.
     *
     * @param request      현재 HTTP 요청 객체
     * @param responseTime 요청 처리에 소요된 시간(밀리초)
     * @return             수집된 사용자 행동 로그 정보가 담긴 BehaviorLog 객체
     */
    private BehaviorLog buildBehaviorLog(HttpServletRequest request, long responseTime) {
        String userAgent = request.getHeader("User-Agent");
        String actionRaw = MDC.get(MdcKey.ACTION);
        ActionType action = ActionType.fromNullableString(actionRaw);

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
                .action(action)
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

    /**
     * 문자열을 long 타입으로 안전하게 변환합니다.
     * 
     * 입력값이 null이거나 숫자로 변환할 수 없는 경우 0L을 반환합니다.
     *
     * @param str long으로 변환할 문자열
     * @return 변환된 long 값 또는 변환 실패 시 0L
     */
    private long parseLong(String str) {
        try {
            return str != null ? Long.parseLong(str) : 0L;
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
