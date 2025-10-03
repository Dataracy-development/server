/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.dataracy.modules.common.status.BaseErrorCode;

@DisplayName("ReferenceException 테스트")
class ReferenceExceptionTest {

  @Test
  @DisplayName("ReferenceException 생성 및 속성 확인")
  void referenceException_ShouldHaveCorrectProperties() {
    // Given
    BaseErrorCode errorCode =
        new BaseErrorCode() {
          @Override
          public HttpStatus getHttpStatus() {
            return HttpStatus.NOT_FOUND;
          }

          @Override
          public String getCode() {
            return "REFERENCE_001";
          }

          @Override
          public String getMessage() {
            return "참조 데이터를 찾을 수 없습니다.";
          }
        };

    // When
    ReferenceException exception = new ReferenceException(errorCode);

    // Then
    assertAll(
        () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
        () -> assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
        () -> assertThat(exception.getCode()).isEqualTo("REFERENCE_001"),
        () -> assertThat(exception.getMessage()).isEqualTo("참조 데이터를 찾을 수 없습니다."));
  }

  @Test
  @DisplayName("ReferenceException은 BusinessException을 상속받는다")
  void referenceException_ShouldExtendBusinessException() {
    // Given
    BaseErrorCode errorCode =
        new BaseErrorCode() {
          @Override
          public HttpStatus getHttpStatus() {
            return HttpStatus.BAD_REQUEST;
          }

          @Override
          public String getCode() {
            return "REFERENCE_002";
          }

          @Override
          public String getMessage() {
            return "잘못된 참조 데이터입니다.";
          }
        };

    // When
    ReferenceException exception = new ReferenceException(errorCode);

    // Then
    assertThat(exception)
        .isInstanceOf(com.dataracy.modules.common.exception.BusinessException.class)
        .isInstanceOf(com.dataracy.modules.common.exception.CustomException.class)
        .isInstanceOf(RuntimeException.class);
  }
}
