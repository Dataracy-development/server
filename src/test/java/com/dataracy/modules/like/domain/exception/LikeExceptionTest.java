/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.like.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.dataracy.modules.common.status.BaseErrorCode;

@DisplayName("LikeException 테스트")
class LikeExceptionTest {

  @Test
  @DisplayName("LikeException 생성 및 속성 확인")
  void likeException_ShouldHaveCorrectProperties() {
    // Given
    BaseErrorCode errorCode =
        new BaseErrorCode() {
          @Override
          public HttpStatus getHttpStatus() {
            return HttpStatus.BAD_REQUEST;
          }

          @Override
          public String getCode() {
            return "LIKE_001";
          }

          @Override
          public String getMessage() {
            return "좋아요 처리 중 오류가 발생했습니다.";
          }
        };

    // When
    LikeException exception = new LikeException(errorCode);

    // Then
    assertAll(
        () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
        () -> assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST),
        () -> assertThat(exception.getCode()).isEqualTo("LIKE_001"),
        () -> assertThat(exception.getMessage()).isEqualTo("좋아요 처리 중 오류가 발생했습니다."));
  }

  @Test
  @DisplayName("LikeException은 BusinessException을 상속받는다")
  void likeException_ShouldExtendBusinessException() {
    // Given
    BaseErrorCode errorCode =
        new BaseErrorCode() {
          @Override
          public HttpStatus getHttpStatus() {
            return HttpStatus.CONFLICT;
          }

          @Override
          public String getCode() {
            return "LIKE_002";
          }

          @Override
          public String getMessage() {
            return "이미 좋아요가 처리되었습니다.";
          }
        };

    // When
    LikeException exception = new LikeException(errorCode);

    // Then
    assertThat(exception)
        .isInstanceOf(com.dataracy.modules.common.exception.BusinessException.class)
        .isInstanceOf(com.dataracy.modules.common.exception.CustomException.class)
        .isInstanceOf(RuntimeException.class);
  }
}
