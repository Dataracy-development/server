/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EsUpdateException 테스트")
class EsUpdateExceptionTest {

  @Test
  @DisplayName("EsUpdateException 생성 및 속성 확인")
  void esUpdateException_ShouldHaveCorrectProperties() {
    // Given
    String message = "Elasticsearch 업데이트에 실패했습니다.";
    RuntimeException cause = new RuntimeException("인덱스 오류");

    // When
    EsUpdateException exception = new EsUpdateException(message, cause);

    // Then
    assertAll(
        () -> assertThat(exception.getMessage()).isEqualTo(message),
        () -> assertThat(exception.getCause()).isEqualTo(cause));
  }

  @Test
  @DisplayName("EsUpdateException은 RuntimeException을 상속받는다")
  void esUpdateException_ShouldExtendRuntimeException() {
    // Given
    String message = "ES 업데이트 실패";
    RuntimeException cause = new RuntimeException("연결 오류");

    // When
    EsUpdateException exception = new EsUpdateException(message, cause);

    // Then
    assertThat(exception).isInstanceOf(RuntimeException.class).isInstanceOf(Exception.class);
  }

  @Test
  @DisplayName("EsUpdateException - null 원인으로 생성")
  void esUpdateException_ShouldCreateWithNullCause() {
    // Given
    String message = "ES 업데이트 실패";

    // When
    EsUpdateException exception = new EsUpdateException(message, null);

    // Then
    assertAll(
        () -> assertThat(exception.getMessage()).isEqualTo(message),
        () -> assertThat(exception.getCause()).isNull());
  }
}
