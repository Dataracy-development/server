/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.status;

import org.springframework.http.HttpStatus;

/** Enum값으로 에러 응답을 관리한다. 에러응답을 위한 커스텀 Enum은 BaseErrorCode를 구현한다. */
public interface BaseErrorCode {
  HttpStatus getHttpStatus();

  String getCode();

  String getMessage();
}
