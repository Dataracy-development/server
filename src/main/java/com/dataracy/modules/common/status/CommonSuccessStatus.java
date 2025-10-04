package com.dataracy.modules.common.status;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonSuccessStatus implements BaseSuccessCode {
  // Global
  OK(HttpStatus.OK, "COMMON-200", "성공입니다."),
  CREATED(HttpStatus.CREATED, "COMMON-201", "생성에 성공했습니다."),
  NO_CONTENT(HttpStatus.NO_CONTENT, "COMMON-204", "성공입니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
