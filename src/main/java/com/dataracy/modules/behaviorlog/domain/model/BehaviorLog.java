package com.dataracy.modules.behaviorlog.domain.model;

import com.dataracy.modules.behaviorlog.domain.enums.ActionType;
import com.dataracy.modules.behaviorlog.domain.enums.DeviceType;
import com.dataracy.modules.behaviorlog.domain.enums.LogType;
import com.dataracy.modules.common.support.enums.HttpMethod;
import lombok.*;

import java.time.Instant;

/**
 * 행동 로그 도메인 모델
 * Elasticsearch에 저장되는 로그 데이터로,
 * 사용자 행동 분석 및 시각화를 위해 사용됩니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BehaviorLog {

    // 로그인 유저 ID (없을 경우 null)
    private String userId;

    // 익명 유저 ID (UUID 기반)
    private String anonymousId;

    // 현재 요청 경로
    private String path;

    // HTTP 메서드 (GET, POST 등)
    private HttpMethod httpMethod;

    // 전체 응답 소요 시간 (ms)
    private long responseTime;

    // 브라우저 및 디바이스 정보
    private String userAgent;

    // 요청한 클라이언트의 IP
    private String ip;

    // 사용자의 행동 종류 (클릭, 이동, 기타)
    private ActionType action;

    // DB 처리 지연 시간
    private long dbLatency;

    // 외부 API 호출 지연 시간
    private long externalLatency;

    // 최초 요청한 referer (없을 수도 있음)
    private String referrer;

    // User-Agent 기반 디바이스 유형 (모바일/PC 등)
    private DeviceType deviceType;

    // 요청 흐름 전체 추적용 ID (MDC에서 삽입)
    private String requestId;

    // 같은 세션 내에서의 추적을 위한 ID (쿠키 기반)
    private String sessionId;

    // 행동 로그 타입 (일반 액션, 에러 등)
    private LogType logType;

    // 타임스탬프 (Elasticsearch 분석용)
    private Instant timestamp;

    /**
     * 익명 사용자 또는 로그인 유저 중 최소 1명은 있어야 유효한 로그로 간주
     */
    public boolean isValid() {
        return userId != null || anonymousId != null;
    }

    /**
     * 타임스탬프가 없을 경우 외부에서 주입해서 생성 (불변 객체 스타일)
     */
    public BehaviorLog withTimestamp(Instant timestamp) {
        return BehaviorLog.builder()
                .userId(this.userId)
                .anonymousId(this.anonymousId)
                .path(this.path)
                .httpMethod(this.httpMethod)
                .responseTime(this.responseTime)
                .userAgent(this.userAgent)
                .ip(this.ip)
                .action(this.action)
                .dbLatency(this.dbLatency)
                .externalLatency(this.externalLatency)
                .referrer(this.referrer)
                .deviceType(this.deviceType)
                .requestId(this.requestId)
                .sessionId(this.sessionId)
                .logType(this.logType)
                .timestamp(timestamp)
                .build();
    }
}
