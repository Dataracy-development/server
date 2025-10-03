/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.dto.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.common.status.BaseErrorCode;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.user.domain.status.UserErrorStatus;

class ErrorResponseTest {

  @Test
  @DisplayName("of - BaseErrorCode로 ErrorResponse를 생성한다")
  void of_WithErrorCode_CreatesErrorResponse() {
    // given
    BaseErrorCode errorCode = CommonErrorStatus.BAD_REQUEST;

    // when
    ErrorResponse result = ErrorResponse.of(errorCode);

    // then
    assertAll(
        () -> assertThat(result.getHttpStatus()).isEqualTo(400),
        () -> assertThat(result.getCode()).isEqualTo("COMMON-400"),
        () -> assertThat(result.getMessage()).isEqualTo("잘못된 요청입니다."));
  }

  @Test
  @DisplayName("of - BaseErrorCode와 커스텀 메시지로 ErrorResponse를 생성한다")
  void of_WithErrorCodeAndCustomMessage_CreatesErrorResponse() {
    // given
    BaseErrorCode errorCode = CommonErrorStatus.UNAUTHORIZED;
    String customMessage = "인증이 필요합니다.";

    // when
    ErrorResponse result = ErrorResponse.of(errorCode, customMessage);

    // then
    assertAll(
        () -> assertThat(result.getHttpStatus()).isEqualTo(401),
        () -> assertThat(result.getCode()).isEqualTo("COMMON-401"),
        () -> assertThat(result.getMessage()).isEqualTo("인증이 필요합니다."));
  }

  @Test
  @DisplayName("of - UserErrorStatus로 ErrorResponse를 생성한다")
  void of_WithUserErrorStatus_CreatesErrorResponse() {
    // given
    BaseErrorCode errorCode = UserErrorStatus.BAD_REQUEST_LOGIN;

    // when
    ErrorResponse result = ErrorResponse.of(errorCode);

    // then
    assertAll(
        () -> assertThat(result.getHttpStatus()).isEqualTo(400),
        () -> assertThat(result.getCode()).isEqualTo("USER-020"),
        () -> assertThat(result.getMessage()).isEqualTo("이메일 또는 비밀번호를 확인해주세요"));
  }

  @Test
  @DisplayName("of - UserErrorStatus와 커스텀 메시지로 ErrorResponse를 생성한다")
  void of_WithUserErrorStatusAndCustomMessage_CreatesErrorResponse() {
    // given
    BaseErrorCode errorCode = UserErrorStatus.DUPLICATED_NICKNAME;
    String customMessage = "닉네임은 2-8자 사이여야 합니다.";

    // when
    ErrorResponse result = ErrorResponse.of(errorCode, customMessage);

    // then
    assertAll(
        () -> assertThat(result.getHttpStatus()).isEqualTo(409),
        () -> assertThat(result.getCode()).isEqualTo("USER-004"),
        () -> assertThat(result.getMessage()).isEqualTo("닉네임은 2-8자 사이여야 합니다."));
  }

  @Test
  @DisplayName("of - INTERNAL_SERVER_ERROR로 ErrorResponse를 생성한다")
  void of_WithInternalServerError_CreatesErrorResponse() {
    // given
    BaseErrorCode errorCode = CommonErrorStatus.INTERNAL_SERVER_ERROR;

    // when
    ErrorResponse result = ErrorResponse.of(errorCode);

    // then
    assertAll(
        () -> assertThat(result.getHttpStatus()).isEqualTo(500),
        () -> assertThat(result.getCode()).isEqualTo("COMMON-500"),
        () -> assertThat(result.getMessage()).isEqualTo("서버 내부 오류가 발생했습니다. 관리자에게 문의하세요."));
  }

  @Test
  @DisplayName("of - 빈 문자열 커스텀 메시지로 ErrorResponse를 생성한다")
  void of_WithEmptyCustomMessage_CreatesErrorResponse() {
    // given
    BaseErrorCode errorCode = CommonErrorStatus.BAD_REQUEST;
    String customMessage = "";

    // when
    ErrorResponse result = ErrorResponse.of(errorCode, customMessage);

    // then
    assertAll(
        () -> assertThat(result.getHttpStatus()).isEqualTo(400),
        () -> assertThat(result.getCode()).isEqualTo("COMMON-400"),
        () -> assertThat(result.getMessage()).isEmpty());
  }

  @Test
  @DisplayName("of - null 커스텀 메시지로 ErrorResponse를 생성한다")
  void of_WithNullCustomMessage_CreatesErrorResponse() {
    // given
    BaseErrorCode errorCode = CommonErrorStatus.BAD_REQUEST;
    String customMessage = null;

    // when
    ErrorResponse result = ErrorResponse.of(errorCode, customMessage);

    // then
    assertAll(
        () -> assertThat(result.getHttpStatus()).isEqualTo(400),
        () -> assertThat(result.getCode()).isEqualTo("COMMON-400"),
        () -> assertThat(result.getMessage()).isNull());
  }
}
