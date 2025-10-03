/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.support.lock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LockAcquisitionException 테스트")
class LockAcquisitionExceptionTest {

  @Test
  @DisplayName("LockAcquisitionException 생성 및 속성 확인")
  void lockAcquisitionException_ShouldHaveCorrectProperties() {
    // Given
    String message = "분산 락 획득에 실패했습니다.";

    // When
    LockAcquisitionException exception = new LockAcquisitionException(message);

    // Then
    assertAll(
        () -> assertThat(exception.getMessage()).isEqualTo(message),
        () -> assertThat(exception.getCause()).isNull());
  }

  @Test
  @DisplayName("LockAcquisitionException - 메시지와 원인으로 생성")
  void lockAcquisitionException_ShouldCreateWithMessageAndCause() {
    // Given
    String message = "락 획득 중 오류 발생";
    RuntimeException cause = new RuntimeException("타임아웃");

    // When
    LockAcquisitionException exception = new LockAcquisitionException(message, cause);

    // Then
    assertAll(
        () -> assertThat(exception.getMessage()).isEqualTo(message),
        () -> assertThat(exception.getCause()).isEqualTo(cause));
  }

  @Test
  @DisplayName("LockAcquisitionException은 RuntimeException을 상속받는다")
  void lockAcquisitionException_ShouldExtendRuntimeException() {
    // Given
    String message = "락 획득 실패";

    // When
    LockAcquisitionException exception = new LockAcquisitionException(message);

    // Then
    assertThat(exception).isInstanceOf(RuntimeException.class).isInstanceOf(Exception.class);
  }
}
