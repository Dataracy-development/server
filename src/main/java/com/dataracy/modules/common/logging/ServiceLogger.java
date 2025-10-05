package com.dataracy.modules.common.logging;

import java.time.Duration;
import java.time.Instant;

import com.dataracy.modules.common.logging.support.BaseLogger;

public class ServiceLogger extends BaseLogger {
  /**
   * 서비스 작업의 시작을 정보 로그로 기록하고, 작업 시작 시각을 반환합니다.
   *
   * @param useCase 서비스 작업의 용도 또는 이름
   * @param message 서비스 작업에 대한 설명 또는 메시지
   * @return 작업 시작 시각을 나타내는 Instant 객체
   */
  public Instant logStart(String useCase, String message) {
    Instant start = Instant.now();
    info("[Service 시작] {} message={}", useCase, message);
    return start;
  }

  /**
   * 서비스 작업이 성공적으로 완료되었음을 기록하고, 작업에 소요된 시간을 밀리초 단위로 로그에 남깁니다.
   *
   * @param useCase 서비스 작업의 용도 또는 이름
   * @param message 서비스 작업에 대한 설명 또는 메시지
   * @param startTime 서비스 작업이 시작된 시각
   */
  public void logSuccess(String useCase, String message, Instant startTime) {
    long durationMs = Duration.between(startTime, Instant.now()).toMillis();
    info("[Service 완료] {} message={} duration={}ms", useCase, message, durationMs);
  }

  /**
   * 서비스 작업 중 발생한 경고를 유스케이스와 메시지와 함께 로그로 기록합니다.
   *
   * @param useCase 경고가 발생한 서비스의 유스케이스 식별자
   * @param message 경고와 관련된 추가 메시지
   */
  public void logWarning(String useCase, String message) {
    warn("[Service 경고] useCase={} message={}", useCase, message);
  }

  /**
   * 서비스 작업 중 발생한 예외를 유스케이스와 메시지, 예외 객체와 함께 에러 로그로 기록합니다.
   *
   * @param useCase 예외가 발생한 서비스 작업의 식별자
   * @param message 예외와 관련된 추가 설명 메시지
   * @param e 기록할 예외 객체
   */
  public void logException(String useCase, String message, Throwable e) {
    error(e, "[Service 예외] useCase={} message={}", useCase, message);
  }

  /**
   * 서비스 작업 중 발생한 예외를 유스케이스, 메시지, 예외 객체, 시작 시간과 함께 에러 로그로 기록합니다.
   *
   * @param useCase 예외가 발생한 서비스 작업의 식별자
   * @param message 예외와 관련된 추가 설명 메시지
   * @param e 기록할 예외 객체
   * @param startTime 서비스 작업이 시작된 시각
   */
  public void logError(String useCase, String message, Throwable e, Instant startTime) {
    long durationMs = Duration.between(startTime, Instant.now()).toMillis();
    error(e, "[Service 오류] useCase={} message={} duration={}ms", useCase, message, durationMs);
  }

  /**
   * 서비스 작업 중 발생한 일반적인 정보를 로그로 기록합니다.
   *
   * @param useCase 정보가 발생한 서비스 작업의 식별자
   * @param message 정보와 관련된 메시지
   */
  public void logInfo(String useCase, String message) {
    info("[Service 정보] useCase={} message={}", useCase, message);
  }
}
