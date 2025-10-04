package com.dataracy.modules.common.logging;

import com.dataracy.modules.common.logging.support.BaseLogger;

public class CommonLogger extends BaseLogger {
  /**
   * 지정된 주제와 메시지로 프로세스의 시작을 디버그 레벨로 기록합니다.
   *
   * @param topic 로그의 주제 또는 카테고리
   * @param message 시작 시 기록할 메시지
   */
  public void logStart(String topic, String message) {
    debug("[{}] message={}", topic, message);
  }

  /**
   * 지정된 토픽과 메시지로 프로세스의 종료를 디버그 레벨로 기록합니다.
   *
   * @param topic 로그의 주제를 나타내는 문자열
   * @param message 로그에 포함될 상세 메시지
   */
  public void logEnd(String topic, String message) {
    debug("[{}] message={}", topic, message);
  }

  /**
   * 지정된 토픽과 메시지로 경고 로그를 기록합니다.
   *
   * @param topic 경고와 관련된 프로세스 또는 이벤트의 식별자
   * @param message 경고에 대한 상세 메시지
   */
  public void logWarning(String topic, String message) {
    warn("[{} 경고] message={}", topic, message);
  }

  /**
   * 지정된 주제와 메시지를 포함하여 오류 로그를 기록합니다.
   *
   * @param topic 오류와 관련된 주제
   * @param message 오류에 대한 상세 메시지
   */
  public void logError(String topic, String message) {
    error("[{} 오류] message={}", topic, message);
  }

  /**
   * 지정된 주제와 메시지, 예외 정보를 포함하여 오류 로그를 기록합니다.
   *
   * @param topic 오류가 발생한 주제 또는 영역
   * @param message 오류에 대한 상세 메시지
   * @param e 관련 예외 객체
   */
  public void logError(String topic, String message, Throwable e) {
    error(e, "[{} 오류] message={}", topic, message);
  }

  /**
   * 지정된 주제와 메시지로 일반적인 정보를 기록합니다.
   *
   * @param topic 로그의 주제 또는 카테고리
   * @param message 정보 메시지
   */
  public void logInfo(String topic, String message) {
    info("[{} 정보] message={}", topic, message);
  }
}
