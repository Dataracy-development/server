package com.dataracy.modules.logging.domain.model;

import lombok.*;

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
    private int status;
    private long responseTime;
    private String userAgent;
    private String ip;
    // 클릭 or 이동 or 기타
    private String action;
    // DB 소요시간(ms)
    private long dbLatency;
    // 외부 API 등
    private long externalLatency;
    private String timestamp;
}
