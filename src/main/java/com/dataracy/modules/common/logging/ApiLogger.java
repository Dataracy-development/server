/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.logging;

import java.time.Duration;
import java.time.Instant;

import com.dataracy.modules.common.logging.support.BaseLogger;

public class ApiLogger extends BaseLogger {
  /**
   * API 요청 메시지를 정보 로그로 기록하고, 요청 시각의 타임스탬프를 반환합니다.
   *
   * @param message 기록할 API 요청 메시지
   * @return 요청이 기록된 시점의 Instant 객체
   */
  public Instant logRequest(String message) {
    info("[API 요청] {}", message);
    return Instant.now();
  }

  /**
   * API 응답 메시지를 정보 로그로 기록하고, 요청 시작 시각부터의 소요 시간을 함께 기록합니다.
   *
   * @param message 응답에 대한 설명 또는 상세 메시지
   * @param startTime 요청이 시작된 시각
   */
  public void logResponse(String message, Instant startTime) {
    long durationMs = Duration.between(startTime, Instant.now()).toMillis();
    info("[API 응답] {} duration={}ms", message, durationMs);
  }

  /**
   * API 유효성 검사 실패 시 경고 메시지를 기록합니다.
   *
   * @param message 유효성 실패에 대한 상세 설명
   */
  public void logValidation(String message) {
    warn("[API 유효성 실패] {}", message);
  }

  /**
   * API 예외 발생 시 오류 메시지와 예외 정보를 로그로 기록합니다.
   *
   * @param message 예외와 관련된 설명 메시지
   * @param e 발생한 예외 객체
   */
  public void logException(String message, Throwable e) {
    error(e, "[API 예외 발생] {}", message);
  }

  /**
   * API 요청/응답 중 발생한 일반적인 정보를 로그로 기록합니다.
   *
   * @param message 정보와 관련된 메시지
   */
  public void logInfo(String message) {
    info("[API 정보] {}", message);
  }

  /**
   * API 요청/응답 중 발생한 오류를 로그로 기록합니다.
   *
   * @param message 오류와 관련된 메시지
   * @param e 발생한 예외 객체
   * @param startTime 요청이 시작된 시각
   */
  public void logError(String message, Throwable e, Instant startTime) {
    long durationMs = Duration.between(startTime, Instant.now()).toMillis();
    error(e, "[API 오류] {} duration={}ms", message, durationMs);
  }

  /**
   * API 요청/응답이 성공적으로 완료되었음을 로그로 기록합니다.
   *
   * @param message 성공과 관련된 메시지
   * @param startTime 요청이 시작된 시각
   */
  public void logSuccess(String message, Instant startTime) {
    long durationMs = Duration.between(startTime, Instant.now()).toMillis();
    info("[API 성공] {} duration={}ms", message, durationMs);
  }
}
