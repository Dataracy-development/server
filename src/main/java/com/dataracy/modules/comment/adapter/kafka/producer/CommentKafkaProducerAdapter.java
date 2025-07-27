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

    @Override
    public void sendCommentUploadedEvent(Long commentId) {
        log.info("Kafka 발행: 댓글 작성됨, commentId={}", commentId);
        kafkaTemplate.send(TOPIC_UPLOAD, String.valueOf(commentId), commentId);
    }

    @Override
    public void sendCommentDeletedEvent(Long commentId) {
        log.info("Kafka 발행: 댓글 삭제됨, commentId={}", commentId);
        kafkaTemplate.send(TOPIC_DELETE, String.valueOf(commentId), commentId);
    }
}
