/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.adapter.ratelimit;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dataracy.modules.auth.application.port.out.rate.RateLimitPort;
import com.dataracy.modules.common.logging.support.LoggerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/** 메모리 기반 레이트 리미팅 구현체 비용 효율적인 단일 인스턴스 환경에 적합합니다. */
@Component("memoryRateLimitAdapter")
public class MemoryRateLimitAdapter implements RateLimitPort {

  // 로깅용 어댑터 이름 상수
  private static final String ADAPTER_NAME = "MemoryRateLimitAdapter";

  @Value("${rate-limit.memory.max-requests:10}")
  private int defaultMaxRequests;

  @Value("${rate-limit.memory.window-minutes:1}")
  private int defaultWindowMinutes;

  private final ConcurrentHashMap<String, RequestCounter> requestCounters =
      new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  @PostConstruct
  public void init() {
    // 1분마다 만료된 카운터 정리
    scheduler.scheduleAtFixedRate(this::cleanupExpiredCounters, 1, 1, TimeUnit.MINUTES);
    LoggerFactory.common().logInfo(ADAPTER_NAME, "메모리 기반 레이트 리미팅 어댑터 초기화 완료");
  }

  @PreDestroy
  public void destroy() {
    LoggerFactory.common().logInfo(ADAPTER_NAME, "메모리 기반 레이트 리미팅 어댑터 종료 시작");
    scheduler.shutdown();
    try {
      if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
        scheduler.shutdownNow();
        LoggerFactory.common().logWarning(ADAPTER_NAME, "스케줄러 강제 종료됨");
      }
    } catch (InterruptedException e) {
      scheduler.shutdownNow();
      Thread.currentThread().interrupt();
      LoggerFactory.common().logError(ADAPTER_NAME, "스케줄러 종료 중 인터럽트 발생", e);
    }
    LoggerFactory.common().logInfo(ADAPTER_NAME, "메모리 기반 레이트 리미팅 어댑터 종료 완료");
  }

  @Override
  public boolean isAllowed(String key, int maxRequests, int windowMinutes) {
    if (key == null || key.trim().isEmpty()) {
      return true; // IP가 없으면 허용
    }

    long currentTime = System.currentTimeMillis();
    long windowMillis = windowMinutes * 60 * 1000L;

    RequestCounter counter = requestCounters.computeIfAbsent(key, k -> new RequestCounter());

    // 윈도우가 지났으면 카운터 리셋
    if (currentTime - counter.getFirstRequestTime() > windowMillis) {
      counter.reset(currentTime);
    }

    boolean allowed = counter.getCount() < maxRequests;

    if (allowed) {
      LoggerFactory.common()
          .logInfo(
              ADAPTER_NAME,
              String.format("요청 허용 - IP: %s, 현재 카운트: %d/%d", key, counter.getCount(), maxRequests));
    } else {
      LoggerFactory.common()
          .logWarning(
              ADAPTER_NAME,
              String.format("요청 차단 - IP: %s, 현재 카운트: %d/%d", key, counter.getCount(), maxRequests));
    }

    return allowed;
  }

  @Override
  public void incrementRequestCount(String key, int incrementBy) {
    if (key == null || key.trim().isEmpty()) {
      return; // IP가 없으면 카운트하지 않음
    }

    long currentTime = System.currentTimeMillis();
    RequestCounter counter = requestCounters.computeIfAbsent(key, k -> new RequestCounter());

    // 윈도우가 지났으면 카운터 리셋
    long windowMillis = defaultWindowMinutes * 60 * 1000L;
    if (currentTime - counter.getFirstRequestTime() > windowMillis) {
      counter.reset(currentTime);
    }

    counter.increment();

    LoggerFactory.common()
        .logInfo(
            ADAPTER_NAME, String.format("요청 카운트 증가 - IP: %s, 현재 카운트: %d", key, counter.getCount()));
  }

  private void cleanupExpiredCounters() {
    long currentTime = System.currentTimeMillis();
    long maxAge = 60 * 60 * 1000L; // 1시간

    requestCounters
        .entrySet()
        .removeIf(
            entry -> {
              RequestCounter counter = entry.getValue();
              return currentTime - counter.getFirstRequestTime() > maxAge;
            });
  }

  private static class RequestCounter {
    private final AtomicInteger count = new AtomicInteger(0);
    private volatile long firstRequestTime = System.currentTimeMillis();

    public void increment() {
      count.incrementAndGet();
    }

    public int getCount() {
      return count.get();
    }

    public long getFirstRequestTime() {
      return firstRequestTime;
    }

    public void reset(long currentTime) {
      count.set(0);
      firstRequestTime = currentTime;
    }
  }
}
