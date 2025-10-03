/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.behaviorlog.support.aop;

import java.time.Duration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.dataracy.modules.behaviorlog.domain.enums.ActionType;
import com.dataracy.modules.behaviorlog.support.annotation.TrackClick;
import com.dataracy.modules.behaviorlog.support.annotation.TrackNavigation;
import com.dataracy.modules.behaviorlog.support.mdc.MdcKey;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class BehaviorLogActionAspect {

  private final StringRedisTemplate redisTemplate;

  /**
   * `@TrackClick` 애노테이션이 적용된 메서드 실행 전, 클릭 액션 정보를 MDC에 기록합니다.
   *
   * <p>클릭 액션 값이 비어 있으면 기본값 "CLICK"이 사용됩니다.
   */
  @Before("@annotation(trackClick)")
  public void handleTrackClick(JoinPoint joinPoint, TrackClick trackClick) {
    String actionValue = trackClick.value();
    MDC.put(MdcKey.ACTION, actionValue.isEmpty() ? ActionType.CLICK.name() : actionValue);
  }

  /**
   * `@TrackNavigation` 애노테이션이 적용된 메서드 실행 전 호출되어, 사용자의 네비게이션 행동 정보를 MDC와 Redis에 기록합니다.
   *
   * <p>사용자의 이전 경로와 머문 시간을 Redis에서 조회하여 계산하고, 현재 경로 및 타임스탬프를 Redis에 10분간 저장합니다. MDC에는 액션 타입, 이전
   * 경로(REFERRER), 다음 경로(NEXT_PATH), 머문 시간(STAY_TIME) 정보를 추가합니다. Redis에 저장된 타임스탬프가 잘못된 경우 경고 로그를
   * 남깁니다.
   */
  @Before("@annotation(trackNavigation)")
  public void handleTrackNavigation(JoinPoint joinPoint, TrackNavigation trackNavigation) {
    String actionValue = trackNavigation.value();
    MDC.put(MdcKey.ACTION, actionValue.isEmpty() ? ActionType.NAVIGATION.name() : actionValue);

    String anonymousId = MDC.get(MdcKey.ANONYMOUS_ID);
    String sessionId = MDC.get(MdcKey.SESSION_ID);
    String path = MDC.get(MdcKey.PATH);

    long now = System.currentTimeMillis();
    String redisKey = buildRedisKey(anonymousId, sessionId);
    String redisValue = redisTemplate.opsForValue().get(redisKey);

    String lastPath = null;
    Long stayTime = null;

    if (redisValue != null && redisValue.contains(",")) {
      String[] parts = redisValue.split(",");
      if (parts.length >= 2) {
        lastPath = parts[0];
        try {
          long lastTime = Long.parseLong(parts[1]);
          stayTime = now - lastTime;
        } catch (NumberFormatException e) {
          log.warn("Invalid timestamp in Redis value: {}", parts[1]);
        }
      }
    }

    // Redis 업데이트
    redisTemplate.opsForValue().set(redisKey, path + "," + now, Duration.ofMinutes(10));

    MDC.put(MdcKey.REFERRER, lastPath);
    MDC.put(MdcKey.NEXT_PATH, path);
    if (stayTime != null) {
      MDC.put(MdcKey.STAY_TIME, String.valueOf(stayTime));
    }

    log.trace("AOP 탐지 - referrer={}, nextPath={}, stayTime={}", lastPath, path, stayTime);
  }

  /**
   * 사용자 식별자(익명 ID 또는 세션 ID)를 기반으로 마지막 네비게이션 정보를 저장할 Redis 키를 생성합니다.
   *
   * @param anonymousId 익명 사용자 식별자(존재할 경우 우선 사용)
   * @param sessionId 세션 식별자(익명 ID가 없을 경우 사용)
   * @return 네비게이션 상태 저장에 사용되는 Redis 키 문자열
   */
  private String buildRedisKey(String anonymousId, String sessionId) {
    return "behavior:last:" + (anonymousId != null ? anonymousId : sessionId);
  }
}
