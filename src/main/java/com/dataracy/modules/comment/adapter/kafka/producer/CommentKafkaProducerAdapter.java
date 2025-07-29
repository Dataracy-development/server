package com.dataracy.modules.comment.adapter.kafka.producer;

import com.dataracy.modules.comment.application.port.out.CommentKafkaProducerPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentKafkaProducerAdapter implements CommentKafkaProducerPort {
    private final KafkaTemplate<String, Long> kafkaTemplate;

    @Value("${spring.kafka.producer.comment-upload.topic:comment-uploaded-topic}")
    private String TOPIC_UPLOAD;

    @Value("${spring.kafka.producer.comment-delete.topic:comment-deleted-topic}")
    private String TOPIC_DELETE;

    /**
     * 댓글 작성 이벤트를 지정된 Kafka 토픽에 비동기적으로 발행합니다.
     *
     * @param projectId 댓글이 작성된 프로젝트의 ID
     */
    @Override
    public void sendCommentUploadedEvent(Long projectId) {
        log.info("Kafka 발행: 댓글 작성됨, projectId={}", projectId);
        kafkaTemplate.send(TOPIC_UPLOAD, String.valueOf(projectId), projectId)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[Kafka] 댓글 작성 이벤트 발송 실패: projectId={}", projectId, ex);
                        // 필요시 재시도 로직 또는 예외 처리
                    } else {
                        log.trace("[Kafka] 댓글 작성 이벤트 발송 성공: projectId={}", projectId);
                    }
                });
    }

    /**
     * 주어진 프로젝트 ID에 대한 댓글 삭제 이벤트를 Kafka 토픽에 비동기적으로 발행합니다.
     *
     * @param projectId 댓글이 삭제된 프로젝트의 ID
     */
    @Override
    public void sendCommentDeletedEvent(Long projectId) {
        log.info("Kafka 발행: 댓글 삭제됨, projectId={}", projectId);
        kafkaTemplate.send(TOPIC_DELETE, String.valueOf(projectId), projectId)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[Kafka] 댓글 삭제 이벤트 발송 실패: projectId={}", projectId, ex);
                        // 필요시 재시도 로직 또는 예외 처리
                    } else {
                        log.trace("[Kafka] 댓글 삭제 이벤트 발송 성공: projectId={}", projectId);
                    }
                });
    }
}
