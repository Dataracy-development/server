/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.exception;

import org.springframework.http.HttpStatus;

import com.dataracy.modules.common.status.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 에러 처리 공통 응답을 위한 커스텀 Exception */
@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final transient BaseErrorCode errorCode;

  public HttpStatus getHttpStatus() {
    return errorCode.getHttpStatus();
  }

  public String getCode() {
    return errorCode.getCode();
  }

  @Override
  public String getMessage() {
    return errorCode.getMessage();
  }
}
