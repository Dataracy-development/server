package com.dataracy.modules.filestorage.adapter.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileDeleteConsumer {
  private final StringRedisTemplate redisTemplate;
  private final FileStoragePort fileStoragePort;

  @Value("${spring.kafka.consumer.file-delete.topic:file-delete-topic}")
  private String fileDeletedTopic;

  /**
   * Kafka 메시지로 전달된 파일 URL을 받아 해당 파일을 삭제합니다.
   *
   * <p>멱등성 보장: partition + offset 기반 중복 처리 방지
   *
   * @param record Kafka ConsumerRecord
   * @param acknowledgment 수동 커밋용 Acknowledgment
   */
  @KafkaListener(
      topics = "${spring.kafka.consumer.file-delete.topic:file-delete-topic}",
      groupId = "${spring.kafka.consumer.file-delete.group-id:file-delete-consumer-group}")
  public void consume(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    processIdempotently(
        record,
        acknowledgment,
        () -> {
          String fileUrl = record.value();
          LoggerFactory.kafka().logConsume(fileDeletedTopic, "파일 삭제 이벤트 수신됨: " + fileUrl);
          fileStoragePort.delete(fileUrl);
          LoggerFactory.kafka().logConsume(fileDeletedTopic, "파일 삭제 이벤트 처리 완료: " + fileUrl);
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
      LoggerFactory.kafka()
          .logError(
              record.topic(),
              String.format(
                  "메시지 처리 실패 - topic: %s, partition: %d, offset: %d",
                  record.topic(), record.partition(), record.offset()),
              e);
      // 파일 삭제는 실패해도 재시도하지 않음 (DLQ로 이동)
    }
  }
}
