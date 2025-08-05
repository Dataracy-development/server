package com.dataracy.modules.like.adapter.kafka.producer;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.like.application.port.out.command.SendLikeEventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeKafkaProducerAdapter implements SendLikeEventPort {
    private final KafkaTemplate<String, Long> kafkaTemplate;

    @Value("${spring.kafka.producer.project-like-increase.topic:project-like-increase-topic}")
    private String TOPIC_PROJECT_LIKE_INCREASE;

    @Value("${spring.kafka.producer.project-like-decrease.topic:project-like-decrease-topic}")
    private String TOPIC_PROJECT_LIKE_DECREASE;

    @Value("${spring.kafka.producer.comment-like-increase.topic:comment-like-increase-topic}")
    private String TOPIC_COMMENT_LIKE_INCREASE;

    @Value("${spring.kafka.producer.comment-like-decrease.topic:comment-like-decrease-topic}")
    private String TOPIC_COMMENT_LIKE_DECREASE;

    /**
     * 프로젝트에 대한 좋아요 증가 이벤트를 Kafka 토픽에 발행합니다.
     *
     * @param projectId 좋아요가 증가한 프로젝트의 ID
     */
    @Override
    public void sendProjectLikeIncreaseEvent(Long projectId) {
        kafkaTemplate.send(TOPIC_PROJECT_LIKE_INCREASE, String.valueOf(projectId), projectId)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        LoggerFactory.kafka().logError(TOPIC_PROJECT_LIKE_INCREASE, "프로젝트 좋아요 이벤트 발송 처리 실패: projectId=" + projectId, ex);
                    } else {
                        LoggerFactory.kafka().logProduce(TOPIC_PROJECT_LIKE_INCREASE, "프로젝트 좋아요 이벤트 발송됨: projectId=" + projectId);
                    }
                });
    }

    /**
     * 프로젝트에 대한 좋아요 취소 이벤트를 Kafka 토픽에 비동기적으로 발행합니다.
     *
     * @param projectId 좋아요 취소가 발생한 프로젝트의 ID
     */
    @Override
    public void sendProjectLikeDecreaseEvent(Long projectId) {
        kafkaTemplate.send(TOPIC_PROJECT_LIKE_DECREASE, String.valueOf(projectId), projectId)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        LoggerFactory.kafka().logError(TOPIC_PROJECT_LIKE_DECREASE, "프로젝트 좋아요 취소 이벤트 이벤트 발송 처리 실패: projectId=" + projectId, ex);
                    } else {
                        LoggerFactory.kafka().logProduce(TOPIC_PROJECT_LIKE_DECREASE, "프로젝트 좋아요 취소 이벤트 이벤트 발송됨: projectId=" + projectId);
                    }
                });
    }

    /**
     * 댓글에 대한 좋아요 증가 이벤트를 Kafka로 발행합니다.
     *
     * @param commentId 좋아요가 증가한 댓글의 ID
     */
    @Override
    public void sendCommentLikeIncreaseEvent(Long commentId) {
        kafkaTemplate.send(TOPIC_COMMENT_LIKE_INCREASE, String.valueOf(commentId), commentId)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        LoggerFactory.kafka().logError(TOPIC_COMMENT_LIKE_INCREASE, "댓글 좋아요 이벤트 이벤트 발송 처리 실패: commentId=" + commentId, ex);
                    } else {
                        LoggerFactory.kafka().logProduce(TOPIC_COMMENT_LIKE_INCREASE, "댓글 좋아요 이벤트 이벤트 발송됨: commentId=" + commentId);
                    }
                });
    }

    /**
     * 댓글에 대한 좋아요 취소 이벤트를 Kafka로 비동기 발행합니다.
     *
     * @param commentId 좋아요 취소가 발생한 댓글의 ID
     */
    @Override
    public void sendCommentLikeDecreaseEvent(Long commentId) {
        kafkaTemplate.send(TOPIC_COMMENT_LIKE_DECREASE, String.valueOf(commentId), commentId)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        LoggerFactory.kafka().logError(TOPIC_COMMENT_LIKE_DECREASE, "댓글 좋아요 취소 이벤트 이벤트 발송 처리 실패: commentId=" + commentId, ex);
                    } else {
                        LoggerFactory.kafka().logProduce(TOPIC_COMMENT_LIKE_DECREASE, "댓글 좋아요 취소 이벤트 이벤트 발송됨: commentId=" + commentId);
                    }
                });
    }
}
