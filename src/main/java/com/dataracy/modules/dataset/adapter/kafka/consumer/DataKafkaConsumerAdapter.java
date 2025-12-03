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
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateMetadataParsingStatusPort;
import com.dataracy.modules.dataset.application.port.out.query.read.FindDataPort;
import com.dataracy.modules.dataset.domain.enums.MetadataParsingStatus;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.email.application.port.out.command.SendEmailPort;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataKafkaConsumerAdapter {
  private final StringRedisTemplate redisTemplate;
  private final ParseMetadataUseCase parseMetadataUseCase;
  private final UpdateMetadataParsingStatusPort updateMetadataParsingStatusPort;
  private final FindDataPort findDataPort;
  private final UserQueryPort userQueryPort;
  private final SendEmailPort sendEmailPort;

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

  /**
   * Dead Letter Topic (DLT)에서 모든 재시도 실패한 메시지를 처리합니다.
   *
   * <p>모든 재시도가 실패하여 DLT로 이동한 경우, 파싱 상태를 FAILED로 업데이트하고 사용자에게 완전 실패 알림 이메일을 전송합니다.
   *
   * @param record DLT에서 수신한 ConsumerRecord
   * @param acknowledgment 수동 커밋용 Acknowledgment
   */
  @KafkaListener(
      topics = "${spring.kafka.consumer.extract-metadata.topic:data-uploaded}-dlt",
      groupId =
          "${spring.kafka.consumer.extract-metadata.group-id:data-upload-metadata-consumer-group}-dlt",
      containerFactory = "dataUploadEventKafkaListenerContainerFactory")
  public void consumeDlt(
      ConsumerRecord<String, DataUploadEvent> record, Acknowledgment acknowledgment) {
    try {
      DataUploadEvent event = record.value();
      Long dataId = event.getDataId();

      LoggerFactory.kafka()
          .logError(
              dataUploadedTopic + "-dlt",
              "DLT 메시지 수신 - 모든 재시도 실패: dataId=" + dataId,
              null);

      // 파싱 상태를 FAILED로 업데이트
      updateMetadataParsingStatusPort.updateParsingStatus(dataId, MetadataParsingStatus.FAILED);

      // 완전 실패 이메일 전송
      sendFinalFailureEmail(dataId);

      // 수동 커밋
      if (acknowledgment != null) {
        acknowledgment.acknowledge();
      }
    } catch (Exception e) {
      LoggerFactory.kafka()
          .logError(
              dataUploadedTopic + "-dlt",
              "DLT 메시지 처리 중 오류 발생: dataId=" + (record.value() != null ? record.value().getDataId() : "unknown"),
              e);
      // DLT 처리 실패는 로그만 남기고 커밋 (무한 루프 방지)
      if (acknowledgment != null) {
        acknowledgment.acknowledge();
      }
    }
  }

  /**
   * 모든 재시도 실패 후 완전 실패 시 사용자에게 이메일을 전송합니다.
   *
   * @param dataId 데이터셋 ID
   */
  private void sendFinalFailureEmail(Long dataId) {
    try {
      Data data = findDataPort.findDataById(dataId).orElse(null);
      if (data == null) {
        LoggerFactory.kafka()
            .logWarning(
                dataUploadedTopic + "-dlt",
                "완전 실패 이메일 전송 실패 - 데이터셋을 찾을 수 없음 dataId=" + dataId);
        return;
      }

      userQueryPort
          .findUserById(data.getUserId())
          .ifPresent(
              user -> {
                String email = user.getEmail();
                if (email != null && !email.isBlank()) {
                  String title = "[Dataracy] 데이터셋 메타데이터 파싱 완전 실패";
                  String body =
                      String.format(
                          "안녕하세요.\n\n"
                              + "업로드하신 데이터셋 '%s'의 메타데이터 파싱이 모든 재시도 후에도 실패했습니다.\n\n"
                              + "데이터셋 ID: %d\n\n"
                              + "시스템이 여러 번 시도했으나 파싱에 실패했습니다.\n"
                              + "파일 형식이나 내용을 확인해주시고, 문제가 지속되면 고객지원으로 문의해주세요.\n\n"
                              + "감사합니다.",
                          data.getTitle(), dataId);
                  sendEmailPort.send(email, title, body);
                  LoggerFactory.kafka()
                      .logConsume(
                          dataUploadedTopic + "-dlt",
                          "완전 실패 이메일 전송 완료 - userId=" + data.getUserId() + ", dataId=" + dataId);
                }
              });
    } catch (Exception e) {
      // 이메일 전송 실패는 로그만 남김
      LoggerFactory.kafka()
          .logError(
              dataUploadedTopic + "-dlt", "완전 실패 이메일 전송 실패 - dataId=" + dataId, e);
    }
  }
}
