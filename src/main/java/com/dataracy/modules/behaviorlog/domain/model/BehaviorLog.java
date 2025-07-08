package com.dataracy.modules.behaviorlog.domain.model;

import com.dataracy.modules.common.support.enums.HttpMethod;
import lombok.*;

import java.time.Instant;

/**
 * 행동 로그 도메인 모델
 * elasticSearch 구현체로 저장되는 로그 정보
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BehaviorLog {
    private String userId;
    private String anonymousId;
    private String path;
    private String method;
    private HttpMethod status;
    private long responseTime;
    private String userAgent;
    private String ip;
    // 클릭 or 이동 or 기타
    private String action;
    // DB 소요시간(ms)
    private long dbLatency;
    // 외부 API 등
    private long externalLatency;
    private Instant timestamp;

    // 타임스탬프가 없을 때 추가해주는 도메인 로직
    public BehaviorLog withTimestamp(Instant timestamp) {
        return BehaviorLog.builder()
                .userId(this.userId)
                .anonymousId(this.anonymousId)
                .path(this.path)
                .method(this.method)
                .status(this.status)
                .responseTime(this.responseTime)
                .userAgent(this.userAgent)
                .ip(this.ip)
                .action(this.action)
                .dbLatency(this.dbLatency)
                .externalLatency(this.externalLatency)
                .timestamp(timestamp)
                .build();
    }
}
