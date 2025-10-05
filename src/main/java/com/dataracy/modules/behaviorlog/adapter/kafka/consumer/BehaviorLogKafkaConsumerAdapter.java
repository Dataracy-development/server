package com.dataracy.modules.behaviorlog.adapter.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.dataracy.modules.behaviorlog.application.port.out.SaveBehaviorLogPort;
import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorLogKafkaConsumerAdapter {

  private final SaveBehaviorLogPort saveBehaviorLogPort;

  /**
   * Kafka에서 수신한 행동 로그 메시지를 저장합니다.
   *
   * <p>역직렬화된 `BehaviorLog` 객체를 받아 저장하며, 저장 중 예외가 발생하면 예외를 다시 던져 Kafka의 재시도 메커니즘을 유발합니다.
   */
  @KafkaListener(
      topics = "${spring.kafka.consumer.behavior-log.topic:behavior-logs}",
      groupId = "${spring.kafka.consumer.behavior-log.group-id:behavior-log-consumer-group}",
      containerFactory = "behaviorLogKafkaListenerContainerFactory")
  public void consume(BehaviorLog behaviorLog) {

    try {
      saveBehaviorLogPort.save(behaviorLog);
    } catch (Exception e) {
      log.error("행동 로그 저장 중 오류 발생", e);
      throw e; // Spring-Kafka가 retry 하도록
    }
  }
}
