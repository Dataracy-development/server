package com.dataracy.modules.comment.adapter.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.dataracy.modules.comment.application.port.out.command.event.SendCommentEventPort;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommentKafkaProducerAdapter implements SendCommentEventPort {
  private final KafkaTemplate<String, Long> kafkaTemplate;

  @Value("${spring.kafka.producer.comment-upload.topic:comment-uploaded-topic}")
  private String topicUpload;

  @Value("${spring.kafka.producer.comment-delete.topic:comment-deleted-topic}")
  private String topicDelete;

  /**
   * 주어진 프로젝트 ID로 댓글 작성 이벤트를 비동기적으로 Kafka에 발행합니다.
   *
   * <p>이 메서드는 토픽 {@code topicUpload}에 키는 {@code String.valueOf(projectId)}, 값은 {@code projectId}로
   * 메시지를 전송합니다. 전송 완료 시 콜백에서 실패하면 {@link CommonException}을 던지고, 성공하면 생산 로그를 남깁니다.
   *
   * @param projectId 댓글이 작성된 프로젝트의 ID
   * @throws CommonException 전송 완료 콜백에서 예외가 발생한 경우 Kafka 전송 실패 예외를 던집니다.
   */
  @Override
  public void sendCommentUploadedEvent(Long projectId) {
    kafkaTemplate
        .send(topicUpload, String.valueOf(projectId), projectId)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                LoggerFactory.kafka()
                    .logError(topicUpload, "댓글 작성 이벤트 발송 처리 실패: projectId=" + projectId, ex);
                throw new CommonException(CommonErrorStatus.KAFKA_SEND_FAILURE);
              } else {
                LoggerFactory.kafka()
                    .logProduce(topicUpload, "댓글 작성 이벤트 발송됨: projectId=" + projectId);
              }
            });
  }

  /**
   * 프로젝트 ID를 기반으로 댓글 삭제 이벤트를 Kafka 삭제 토픽에 비동기 발행합니다.
   *
   * <p>비동기적으로 topicDelete 토픽에 메시지를 전송하며, Kafka 메시지의 키는 projectId의 문자열, 값은 projectId(Long)입니다. 전송 완료
   * 시 콜백에서 실패가 발생하면 로그 기록 후 CommonException으로 재발생시켜 호출자에게 전파합니다; 성공 시에는 생산 로그를 남깁니다.
   *
   * @param projectId 댓글이 삭제된 프로젝트의 ID
   * @throws CommonException 전송 완료 콜백에서 예외가 발생한 경우 Kafka 전송 실패 예외를 던집니다.
   */
  @Override
  public void sendCommentDeletedEvent(Long projectId) {
    kafkaTemplate
        .send(topicDelete, String.valueOf(projectId), projectId)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                LoggerFactory.kafka()
                    .logError(topicDelete, "댓글 삭제 이벤트 발송 처리 실패: projectId=" + projectId, ex);
                throw new CommonException(CommonErrorStatus.KAFKA_SEND_FAILURE);
              } else {
                LoggerFactory.kafka()
                    .logProduce(topicDelete, "댓글 삭제 이벤트 발송됨: projectId=" + projectId);
              }
            });
  }
}
