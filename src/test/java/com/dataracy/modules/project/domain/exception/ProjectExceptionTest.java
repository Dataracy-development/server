/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.dataracy.modules.common.status.BaseErrorCode;

@DisplayName("ProjectException 테스트")
class ProjectExceptionTest {

  @Test
  @DisplayName("ProjectException 생성 및 속성 확인")
  void projectException_ShouldHaveCorrectProperties() {
    // Given
    BaseErrorCode errorCode =
        new BaseErrorCode() {
          @Override
          public HttpStatus getHttpStatus() {
            return HttpStatus.NOT_FOUND;
          }

          @Override
          public String getCode() {
            return "PROJECT_001";
          }

          @Override
          public String getMessage() {
            return "프로젝트를 찾을 수 없습니다.";
          }
        };

    // When
    ProjectException exception = new ProjectException(errorCode);

    // Then
    assertAll(
        () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
        () -> assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
        () -> assertThat(exception.getCode()).isEqualTo("PROJECT_001"),
        () -> assertThat(exception.getMessage()).isEqualTo("프로젝트를 찾을 수 없습니다."));
  }

  @Test
  @DisplayName("ProjectException은 BusinessException을 상속받는다")
  void projectException_ShouldExtendBusinessException() {
    // Given
    BaseErrorCode errorCode =
        new BaseErrorCode() {
          @Override
          public HttpStatus getHttpStatus() {
            return HttpStatus.FORBIDDEN;
          }

          @Override
          public String getCode() {
            return "PROJECT_002";
          }

          @Override
          public String getMessage() {
            return "프로젝트에 대한 권한이 없습니다.";
          }
        };

    // When
    ProjectException exception = new ProjectException(errorCode);

    // Then
    assertThat(exception)
        .isInstanceOf(com.dataracy.modules.common.exception.BusinessException.class)
        .isInstanceOf(com.dataracy.modules.common.exception.CustomException.class)
        .isInstanceOf(RuntimeException.class);
  }
}
