package com.dataracy.modules.project.adapter.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.port.in.command.count.DecreaseCommentCountUseCase;
import com.dataracy.modules.project.application.port.in.command.count.DecreaseLikeCountUseCase;
import com.dataracy.modules.project.application.port.in.command.count.IncreaseCommentCountUseCase;
import com.dataracy.modules.project.application.port.in.command.count.IncreaseLikeCountUseCase;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectKafkaConsumerAdapter {
  private final StringRedisTemplate redisTemplate;
  private final IncreaseCommentCountUseCase increaseCommentCountUseCase;
  private final DecreaseCommentCountUseCase decreaseCommentCountUseCase;
  private final IncreaseLikeCountUseCase increaseLikeCountUseCase;
  private final DecreaseLikeCountUseCase decreaseLikeCountUseCase;

  @Value("${spring.kafka.consumer.comment-upload.topic:comment-uploaded-topic}")
  private String commentUploadedTopic;

  @Value("${spring.kafka.consumer.comment-delete.topic:comment-deleted-topic}")
  private String commentDeletedTopic;

  @Value("${spring.kafka.consumer.project-like-increase.topic:project-like-increase-topic}")
  private String likeIncreaseTopic;

  @Value("${spring.kafka.consumer.project-like-decrease.topic:project-like-decrease-topic}")
  private String likeDecreaseTopic;

  /**
   * 프로젝트 댓글 작성 이벤트를 수신하여 해당 프로젝트의 댓글 수를 1 증가시킵니다.
   *
   * <p>멱등성 보장: partition + offset 기반 중복 처리 방지
   *
   * @param record Kafka ConsumerRecord
   * @param acknowledgment 수동 커밋용 Acknowledgment
   */
  @KafkaListener(
      topics = "${spring.kafka.consumer.comment-upload.topic:comment-uploaded-topic}",
      groupId =
          "${spring.kafka.consumer.comment-upload.group-id:project-comment-upload-consumer-group}",
      containerFactory = "longKafkaListenerContainerFactory")
  public void consumeCommentUpload(
      ConsumerRecord<String, Long> record, Acknowledgment acknowledgment) {
    processIdempotently(
        record,
        acknowledgment,
        () -> {
          Long projectId = record.value();
          LoggerFactory.kafka()
              .logConsume(commentUploadedTopic, "댓글 작성 이벤트 수신됨: projectId=" + projectId);
          increaseCommentCountUseCase.increaseCommentCount(projectId);
          LoggerFactory.kafka()
              .logConsume(commentUploadedTopic, "댓글 작성 이벤트 처리 완료: projectId=" + projectId);
        });
  }

  /**
   * Kafka에서 프로젝트의 댓글 삭제 이벤트를 수신하여 해당 프로젝트의 댓글 수를 감소시킵니다.
   *
   * <p>멱등성 보장: partition + offset 기반 중복 처리 방지
   *
   * @param record Kafka ConsumerRecord
   * @param acknowledgment 수동 커밋용 Acknowledgment
   */
  @KafkaListener(
      topics = "${spring.kafka.consumer.comment-delete.topic:comment-deleted-topic}",
      groupId =
          "${spring.kafka.consumer.comment-delete.group-id:project-comment-delete-consumer-group}",
      containerFactory = "longKafkaListenerContainerFactory")
  public void consumeCommentDelete(
      ConsumerRecord<String, Long> record, Acknowledgment acknowledgment) {
    processIdempotently(
        record,
        acknowledgment,
        () -> {
          Long projectId = record.value();
          LoggerFactory.kafka()
              .logConsume(commentDeletedTopic, "댓글 삭제 이벤트 수신됨: projectId=" + projectId);
          decreaseCommentCountUseCase.decreaseCommentCount(projectId);
          LoggerFactory.kafka()
              .logConsume(commentDeletedTopic, "댓글 삭제 이벤트 처리 완료: projectId=" + projectId);
        });
  }

  /**
   * Kafka에서 프로젝트 좋아요 증가 이벤트를 수신하여 해당 프로젝트의 좋아요 수를 1 증가시킵니다.
   *
   * <p>멱등성 보장: partition + offset 기반 중복 처리 방지
   *
   * @param record Kafka ConsumerRecord
   * @param acknowledgment 수동 커밋용 Acknowledgment
   */
  @KafkaListener(
      topics = "${spring.kafka.consumer.project-like-increase.topic:project-like-increase-topic}",
      groupId =
          "${spring.kafka.consumer.project-like-increase.group-id:project-like-increase-consumer-group}",
      containerFactory = "longKafkaListenerContainerFactory")
  public void consumeLikeIncrease(
      ConsumerRecord<String, Long> record, Acknowledgment acknowledgment) {
    processIdempotently(
        record,
        acknowledgment,
        () -> {
          Long projectId = record.value();
          LoggerFactory.kafka()
              .logConsume(likeIncreaseTopic, "프로젝트 좋아요 이벤트 수신됨: projectId=" + projectId);
          increaseLikeCountUseCase.increaseLikeCount(projectId);
          LoggerFactory.kafka()
              .logConsume(likeIncreaseTopic, "프로젝트 좋아요 이벤트 처리 완료: projectId=" + projectId);
        });
  }

  /**
   * Kafka에서 프로젝트 좋아요 취소 이벤트를 수신하여 해당 프로젝트의 좋아요 수를 감소시킵니다.
   *
   * <p>멱등성 보장: partition + offset 기반 중복 처리 방지
   *
   * @param record Kafka ConsumerRecord
   * @param acknowledgment 수동 커밋용 Acknowledgment
   */
  @KafkaListener(
      topics = "${spring.kafka.consumer.project-like-decrease.topic:project-like-decrease-topic}",
      groupId =
          "${spring.kafka.consumer.project-like-decrease.group-id:project-like-decrease-consumer-group}",
      containerFactory = "longKafkaListenerContainerFactory")
  public void consumeLikeDecrease(
      ConsumerRecord<String, Long> record, Acknowledgment acknowledgment) {
    processIdempotently(
        record,
        acknowledgment,
        () -> {
          Long projectId = record.value();
          LoggerFactory.kafka()
              .logConsume(likeDecreaseTopic, "프로젝트 좋아요 취소 이벤트 수신됨: projectId=" + projectId);
          decreaseLikeCountUseCase.decreaseLikeCount(projectId);
          LoggerFactory.kafka()
              .logConsume(likeDecreaseTopic, "프로젝트 좋아요 취소 이벤트 처리 완료: projectId=" + projectId);
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
