package com.dataracy.modules.behaviorlog.domain.model;

import com.dataracy.modules.behaviorlog.domain.enums.ActionType;
import com.dataracy.modules.behaviorlog.domain.enums.DeviceType;
import com.dataracy.modules.behaviorlog.domain.enums.LogType;
import com.dataracy.modules.common.support.enums.HttpMethod;
import lombok.*;

import java.time.Instant;

/**
 * 행동 로그 도메인 모델
 *
 * 사용자 행동 분석 및 시각화를 위한 핵심 로그 객체입니다.
 * - Kafka 메시지로 전송되며
 * - Elasticsearch에 저장되고
 * - Kibana, Grafana에서 시각화됩니다.
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BehaviorLog {

    // 사용자 정보
    private String userId;
    private String anonymousId;

    // 요청 정보
    private String path;
    private HttpMethod httpMethod;
    private String ip;
    private String requestId;
    private String sessionId;
    private String userAgent;
    private String referrer;
    private String nextPath;

    // 행동 정보
    private ActionType action;
    private Long stayTime;

    // 시스템 처리 지표
    private long responseTime;
    private long dbLatency;
    private long externalLatency;

    // 디바이스 정보
    private DeviceType deviceType;
    private String os;
    private String browser;

    // 로그 타입 + 시간
    private LogType logType;
    private String timestamp;

    /**
     * 이 로그가 유효한지 확인합니다.
     *
     * userId 또는 anonymousId 중 하나라도 null이 아니고 비어 있지 않으면 true를 반환합니다.
     *
     * @return 로그가 유효하면 true, 그렇지 않으면 false
     */
    public boolean isValid() {
        return (userId != null && !userId.isBlank())
                || (anonymousId != null && !anonymousId.isBlank());
    }

    /**
     * 타임스탬프가 null이거나 비어 있을 경우, 현재 시간(ISO-8601 문자열)으로 타임스탬프를 설정한 새로운 BehaviorLog 인스턴스를 반환합니다.
     *
     * @return 타임스탬프가 이미 존재하면 현재 인스턴스, 없으면 현재 시간으로 타임스탬프가 설정된 새 인스턴스
     */
    public BehaviorLog withTimestampIfNull() {
        if (this.timestamp != null && !this.timestamp.isBlank()) {
            return this;
        }
        return this.toBuilder()
                .timestamp(Instant.now().toString())
                .build();
    }
}
