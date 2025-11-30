package com.dataracy.modules.dataset.adapter.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.request.metadata.ParseMetadataRequest;
import com.dataracy.modules.dataset.application.port.in.command.metadata.ParseMetadataUseCase;
import com.dataracy.modules.dataset.domain.model.event.DataUploadEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataKafkaConsumerAdapter {
  private final StringRedisTemplate redisTemplate;
  private final ParseMetadataUseCase parseMetadataUseCase;

  @Value("${spring.kafka.consumer.extract-metadata.topic:data-uploaded}")
  private String dataUploadedTopic;

  /**
   * Kafka에서 데이터 업로드 이벤트를 수신하여 해당 파일의 메타데이터를 파싱하고 저장합니다.
   *
   * <p>멱등성 보장: partition + offset 기반 중복 처리 방지
   *
   * @param record Kafka ConsumerRecord
   * @param acknowledgment 수동 커밋용 Acknowledgment
   * @throws Exception 메타데이터 파싱 또는 저장 중 오류가 발생하면 예외를 다시 던져 Kafka의 재시도 메커니즘을 활성화합니다.
   */
  @KafkaListener(
      topics = "${spring.kafka.consumer.extract-metadata.topic:data-uploaded}",
      groupId =
          "${spring.kafka.consumer.extract-metadata.group-id:data-upload-metadata-consumer-group}",
      containerFactory = "dataUploadEventKafkaListenerContainerFactory")
  public void consume(
      ConsumerRecord<String, DataUploadEvent> record, Acknowledgment acknowledgment) {
    processIdempotently(
        record,
        acknowledgment,
        () -> {
          DataUploadEvent event = record.value();
          LoggerFactory.kafka()
              .logConsume(dataUploadedTopic, "데이터셋 업로드 이벤트 수신됨: dataId=" + event.getDataId());
          parseMetadataUseCase.parseAndSaveMetadata(
              new ParseMetadataRequest(
                  event.getDataId(), event.getDataFileUrl(), event.getOriginalFilename()));
          LoggerFactory.kafka()
              .logConsume(dataUploadedTopic, "데이터셋 업로드 이벤트 처리 완료: dataId=" + event.getDataId());
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
      throw e;
    }
  }
}
