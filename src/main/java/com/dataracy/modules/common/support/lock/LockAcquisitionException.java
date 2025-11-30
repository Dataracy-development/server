package com.dataracy.modules.common.support.lock;

public class LockAcquisitionException extends RuntimeException {
  public LockAcquisitionException(String message) {
    super(message);
  }

  /**
   * 지정한 메시지와 원인으로 새로운 LockAcquisitionException을 생성합니다.
   *
   * @param message 예외에 대한 설명 메시지
   * @param cause 예외의 원인이 되는 Throwable 객체
   */
  public LockAcquisitionException(String message, Throwable cause) {
    super(message, cause);
  }
}
