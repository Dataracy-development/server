package com.dataracy.modules.behaviorlog.adapter.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.dataracy.modules.behaviorlog.application.port.out.SaveBehaviorLogPort;
import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import com.dataracy.modules.common.logging.support.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorLogKafkaConsumerAdapter {

  private final StringRedisTemplate redisTemplate;
  private final SaveBehaviorLogPort saveBehaviorLogPort;

  /**
   * Kafka에서 수신한 행동 로그 메시지를 저장합니다.
   *
   * <p>멱등성 보장: partition + offset 기반 중복 처리 방지
   *
   * @param record Kafka ConsumerRecord
   * @param acknowledgment 수동 커밋용 Acknowledgment
   */
  @KafkaListener(
      topics = "${spring.kafka.consumer.behavior-log.topic:behavior-logs}",
      groupId = "${spring.kafka.consumer.behavior-log.group-id:behavior-log-consumer-group}",
      containerFactory = "behaviorLogKafkaListenerContainerFactory")
  public void consume(ConsumerRecord<String, BehaviorLog> record, Acknowledgment acknowledgment) {
    processIdempotently(
        record,
        acknowledgment,
        () -> {
          BehaviorLog behaviorLog = record.value();
          saveBehaviorLogPort.save(behaviorLog);
        });
  }

  /**
   * 멱등성 보장을 위한 공통 처리 메서드
   *
   * @param record Kafka ConsumerRecord
   * @param acknowledgment 수동 커밋용 Acknowledgment
   * @param processor 실제 처리 로직
   */
  private <K, V> void processIdempotently(
      ConsumerRecord<K, V> record, Acknowledgment acknowledgment, Runnable processor) {
    String idempotentKey =
        "kafka:idempotent:" + record.topic() + ":" + record.partition() + ":" + record.offset();

    // Redis에 이미 존재하면 중복 메시지로 간주하고 스킵
    Boolean wasSet =
        redisTemplate.opsForValue().setIfAbsent(idempotentKey, "1", java.time.Duration.ofDays(7));
    if (Boolean.FALSE.equals(wasSet)) {
      LoggerFactory.kafka()
          .logWarning(
              record.topic(),
              String.format(
                  "중복 메시지 감지 - topic: %s, partition: %d, offset: %d",
                  record.topic(), record.partition(), record.offset()));
      if (acknowledgment != null) {
        acknowledgment.acknowledge();
      }
      return;
    }

    try {
      // 실제 처리 로직 실행
      processor.run();
      // 성공 시 수동 커밋
      if (acknowledgment != null) {
        acknowledgment.acknowledge();
      }
    } catch (Exception e) {
      // 실패 시 Redis 키 삭제하여 재시도 가능하도록 함
      redisTemplate.delete(idempotentKey);
      log.error("행동 로그 저장 중 오류 발생", e);
      throw e; // Spring-Kafka가 retry 하도록
    }
  }
}
