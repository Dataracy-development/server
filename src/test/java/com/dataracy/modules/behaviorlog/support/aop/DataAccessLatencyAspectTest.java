/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.behaviorlog.support.aop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataAccessLatencyAspectTest {

  @Mock private ProceedingJoinPoint proceedingJoinPoint;

  @Mock private Signature signature;

  private DataAccessLatencyAspect dataAccessLatencyAspect;

  @BeforeEach
  void setUp() {
    dataAccessLatencyAspect = new DataAccessLatencyAspect();
    given(proceedingJoinPoint.getSignature()).willReturn(signature);
    given(signature.toString())
        .willReturn("com.dataracy.modules.dataset.adapter.jpa.impl.DataAdapter.findData(..)");
  }

  @Nested
  @DisplayName("trackDataAccessLatency 메서드 테스트")
  class TrackDataAccessLatencyTest {

    @Test
    @DisplayName("성공: 정상적인 메서드 실행")
    void trackDataAccessLatency_정상실행_성공() throws Throwable {
      // given
      String expectedResult = "test-result";
      given(proceedingJoinPoint.proceed()).willReturn(expectedResult);

      // when
      Object result = dataAccessLatencyAspect.trackDataAccessLatency(proceedingJoinPoint);

      // then
      assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("성공: null 반환값 처리")
    void trackDataAccessLatency_null반환값_정상처리() throws Throwable {
      // given
      given(proceedingJoinPoint.proceed()).willReturn(null);

      // when
      Object result = dataAccessLatencyAspect.trackDataAccessLatency(proceedingJoinPoint);

      // then
      assertThat(result).isNull();
    }

    @Test
    @DisplayName("성공: 예외 발생 처리")
    void trackDataAccessLatency_예외발생_처리() throws Throwable {
      // given
      RuntimeException expectedException = new RuntimeException("Test exception");
      willThrow(expectedException).given(proceedingJoinPoint).proceed();

      // when & then
      RuntimeException exception =
          catchThrowableOfType(
              () -> dataAccessLatencyAspect.trackDataAccessLatency(proceedingJoinPoint),
              RuntimeException.class);
      assertAll(
          () -> assertThat(exception).isInstanceOf(RuntimeException.class),
          () -> assertThat(exception).hasMessage("Test exception"));
    }
  }
}
