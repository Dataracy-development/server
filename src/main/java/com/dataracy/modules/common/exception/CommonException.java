package com.dataracy.modules.common.exception;

import com.dataracy.modules.common.status.BaseErrorCode;

/**
 * CommonException은 애플리케이션 내의 공통 규칙 위반을 처리하기 위한 기본 커스텀 예외 클래스입니다. 로직 내에서 발생하는 공통적인 오류를 포괄적으로 처리합니다.
 */
public class CommonException extends CustomException {
  public CommonException(BaseErrorCode errorCode) {
    super(errorCode);
  }
}
