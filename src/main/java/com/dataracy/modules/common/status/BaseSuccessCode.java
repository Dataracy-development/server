package com.dataracy.modules.common.status;

import org.springframework.http.HttpStatus;

/** Enum값으로 성공 응답을 관리한다. 성공응답을 위한 커스텀 Enum은 BaseSuccessCode를 구현한다. */
public interface BaseSuccessCode {
  HttpStatus getHttpStatus();

  String getCode();

  String getMessage();
}
