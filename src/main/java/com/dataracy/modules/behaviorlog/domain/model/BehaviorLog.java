package com.dataracy.modules.behaviorlog.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 순수 도메인 모델. 로그 저장 용도
 * DB/Elasticsearch에 매핑되기 위하여 사용됨.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BehaviorLog {
    // 유저 아이디
    private String userId;
    // 익명 유저 아이디
    private String anonymousId;
    // url
    private String path;
    // 메서드
    private String method;
    // 응답 코드
    private int statusCode;
    // 응답 시간
    private long responseTime;
    private String userAgent;
    private String ip;
    private Instant timestamp;
}
