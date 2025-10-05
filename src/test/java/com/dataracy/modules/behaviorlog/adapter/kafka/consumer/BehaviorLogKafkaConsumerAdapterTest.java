package com.dataracy.modules.behaviorlog.adapter.kafka.consumer;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dataracy.modules.behaviorlog.application.port.out.SaveBehaviorLogPort;
import com.dataracy.modules.behaviorlog.domain.enums.ActionType;
import com.dataracy.modules.behaviorlog.domain.enums.DeviceType;
import com.dataracy.modules.behaviorlog.domain.enums.LogType;
import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import com.dataracy.modules.common.support.enums.HttpMethod;

@ExtendWith(MockitoExtension.class)
class BehaviorLogKafkaConsumerAdapterTest {

  @Mock private SaveBehaviorLogPort saveBehaviorLogPort;

  @InjectMocks private BehaviorLogKafkaConsumerAdapter adapter;

  @Test
  @DisplayName("행동 로그 수신 및 저장 성공")
  void consumeBehaviorLogSuccess() {
    // given
    BehaviorLog behaviorLog = createTestBehaviorLog();

    // when
    adapter.consume(behaviorLog);

    // then
    then(saveBehaviorLogPort).should().save(behaviorLog);
  }

  @Test
  @DisplayName("저장 실패 시 예외를 재발생하여 Kafka 재시도 유발")
  void consumeBehaviorLogFailure() {
    // given
    BehaviorLog behaviorLog = createTestBehaviorLog();
    RuntimeException dbError = new RuntimeException("Database connection failed");
    willThrow(dbError).given(saveBehaviorLogPort).save(behaviorLog);

    // when & then
    RuntimeException exception =
        catchThrowableOfType(() -> adapter.consume(behaviorLog), RuntimeException.class);
    assertAll(
        () ->
            org.assertj.core.api.Assertions.assertThat(exception)
                .isInstanceOf(RuntimeException.class),
        () ->
            org.assertj.core.api.Assertions.assertThat(exception)
                .hasMessage("Database connection failed"));

    then(saveBehaviorLogPort).should().save(behaviorLog);
  }

  @Test
  @DisplayName("저장 실패 시 로깅 후 예외 재발생")
  void consumeBehaviorLogFailureWithLogging() {
    // given
    BehaviorLog behaviorLog = createTestBehaviorLog();
    RuntimeException dbError = new RuntimeException("Storage error");
    willThrow(dbError).given(saveBehaviorLogPort).save(behaviorLog);

    // when & then
    RuntimeException exception =
        catchThrowableOfType(() -> adapter.consume(behaviorLog), RuntimeException.class);
    assertAll(() -> org.assertj.core.api.Assertions.assertThat(exception).isSameAs(dbError));

    // 저장이 호출되었는지 확인
    then(saveBehaviorLogPort).should().save(behaviorLog);
  }

  private BehaviorLog createTestBehaviorLog() {
    return BehaviorLog.builder()
        .userId("1")
        .path("/api/projects/456")
        .httpMethod(HttpMethod.GET)
        .action(ActionType.CLICK)
        .deviceType(DeviceType.PC)
        .logType(LogType.ACTION)
        .timestamp("CURRENT_YEAR-01-01T00:00:00Z")
        .build();
  }
}
