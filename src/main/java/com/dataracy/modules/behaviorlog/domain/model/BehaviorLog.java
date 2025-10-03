/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.behaviorlog.domain.model;

import java.time.Instant;

import com.dataracy.modules.behaviorlog.domain.enums.ActionType;
import com.dataracy.modules.behaviorlog.domain.enums.DeviceType;
import com.dataracy.modules.behaviorlog.domain.enums.LogType;
import com.dataracy.modules.common.support.enums.HttpMethod;

import lombok.*;

/**
 * 행동 로그 도메인 모델
 *
 * <p>사용자 행동 분석 및 시각화를 위한 핵심 로그 객체입니다. - Kafka 메시지로 전송되며 - Elasticsearch에 저장되고 - Kibana, Grafana에서
 * 시각화됩니다.
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
   * 이 로그 항목이 유효한지 여부를 반환합니다.
   *
   * <p>userId 또는 anonymousId 중 하나라도 null이 아니면 true를 반환합니다.
   *
   * @return 로그 항목이 유효하면 true, 그렇지 않으면 false
   */
  public boolean isValid() {
    return (userId != null && !userId.isBlank()) || (anonymousId != null && !anonymousId.isBlank());
  }

  /**
   * 새 타임스탬프로 timestamp 필드만 변경된 새로운 BehaviorLog 인스턴스를 반환합니다.
   *
   * <p>기존 인스턴스의 모든 필드는 동일하게 복사되며, timestamp만 전달된 값으로 대체됩니다.
   *
   * @return timestamp가 변경된 새로운 BehaviorLog 인스턴스
   */
  public BehaviorLog withTimestampIfNull() {
    if (this.timestamp != null && !this.timestamp.isBlank()) {
      return this;
    }
    return this.toBuilder().timestamp(Instant.now().toString()).build();
  }
}
