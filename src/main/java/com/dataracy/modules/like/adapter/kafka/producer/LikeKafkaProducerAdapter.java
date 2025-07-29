package com.dataracy.modules.like.adapter.kafka.producer;

import com.dataracy.modules.like.application.port.out.LikeKafkaProducerPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeKafkaProducerAdapter implements LikeKafkaProducerPort {
    private final KafkaTemplate<String, Long> kafkaTemplate;

    @Value("${spring.kafka.producer.project-like-increase.topic:project-like-increase-topic}")
    private String TOPIC_PROJECT_LIKE_INCREASE;

    @Value("${spring.kafka.producer.project-like-decrease.topic:project-like-decrease-topic}")
    private String TOPIC_PROJECT_LIKE_DECREASE;

    @Value("${spring.kafka.producer.comment-like-increase.topic:comment-like-increase-topic}")
    private String TOPIC_COMMENT_LIKE_INCREASE;

    @Value("${spring.kafka.producer.comment-like-decrease.topic:comment-like-decrease-topic}")
    private String TOPIC_COMMENT_LIKE_DECREASE;

    @Override
    public void sendProjectLikeIncreaseEvent(Long projectId) {
        log.info("Kafka 발행: 프로젝트에 대한 좋아요, projectId={}", projectId);
        kafkaTemplate.send(TOPIC_PROJECT_LIKE_INCREASE, String.valueOf(projectId), projectId);
    }

    @Override
    public void sendProjectLikeDecreaseEvent(Long projectId) {
        log.info("Kafka 발행: 프로젝트에 대한 좋아요 취소, projectId={}", projectId);
        kafkaTemplate.send(TOPIC_PROJECT_LIKE_DECREASE, String.valueOf(projectId), projectId);
    }

    @Override
    public void sendCommentLikeIncreaseEvent(Long commentId) {
        log.info("Kafka 발행: 댓글에 대한 좋아요, commentId={}", commentId);
        kafkaTemplate.send(TOPIC_COMMENT_LIKE_INCREASE, String.valueOf(commentId), commentId);
    }

    @Override
    public void sendCommentLikeDecreaseEvent(Long commentId) {
        log.info("Kafka 발행: 댓글에 대한 좋아요 취소, commentId={}", commentId);
        kafkaTemplate.send(TOPIC_COMMENT_LIKE_DECREASE, String.valueOf(commentId), commentId);
    }

}
