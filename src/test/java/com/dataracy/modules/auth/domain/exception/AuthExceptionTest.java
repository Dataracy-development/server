/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.dataracy.modules.common.status.BaseErrorCode;

@DisplayName("AuthException 테스트")
class AuthExceptionTest {

  @Test
  @DisplayName("AuthException 생성 및 속성 확인")
  void authException_ShouldHaveCorrectProperties() {
    // Given
    BaseErrorCode errorCode =
        new BaseErrorCode() {
          @Override
          public HttpStatus getHttpStatus() {
            return HttpStatus.UNAUTHORIZED;
          }

          @Override
          public String getCode() {
            return "AUTH_001";
          }

          @Override
          public String getMessage() {
            return "인증에 실패했습니다.";
          }
        };

    // When
    AuthException exception = new AuthException(errorCode);

    // Then
    assertAll(
        () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
        () -> assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED),
        () -> assertThat(exception.getCode()).isEqualTo("AUTH_001"),
        () -> assertThat(exception.getMessage()).isEqualTo("인증에 실패했습니다."));
  }

  @Test
  @DisplayName("AuthException은 BusinessException을 상속받는다")
  void authException_ShouldExtendBusinessException() {
    // Given
    BaseErrorCode errorCode =
        new BaseErrorCode() {
          @Override
          public HttpStatus getHttpStatus() {
            return HttpStatus.FORBIDDEN;
          }

          @Override
          public String getCode() {
            return "AUTH_002";
          }

          @Override
          public String getMessage() {
            return "권한이 없습니다.";
          }
        };

    // When
    AuthException exception = new AuthException(errorCode);

    // Then
    assertThat(exception)
        .isInstanceOf(com.dataracy.modules.common.exception.BusinessException.class)
        .isInstanceOf(com.dataracy.modules.common.exception.CustomException.class)
        .isInstanceOf(RuntimeException.class);
  }
}
