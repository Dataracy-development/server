package com.dataracy.modules.like.adapter.kafka.producer;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.like.application.port.out.command.SendLikeEventPort;
import com.dataracy.modules.like.domain.enums.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

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
     * 프로젝트 또는 댓글에 대한 좋아요 또는 좋아요 취소 이벤트를 Kafka로 비동기 전송합니다.
     *
     * @param targetType      이벤트 대상의 유형(PROJECT 또는 COMMENT)
     * @param targetId        이벤트 대상의 고유 식별자
     * @param previouslyLiked true이면 좋아요 취소 이벤트, false이면 좋아요 이벤트를 전송합니다.
     */
    @Override
    public void sendLikeEvent(TargetType targetType, Long targetId, boolean previouslyLiked) {
        switch (targetType) {
            case PROJECT -> {
                if (previouslyLiked) {
                    kafkaTemplate.send(TOPIC_PROJECT_LIKE_DECREASE, String.valueOf(targetId), targetId)
                            .whenComplete((result, ex) -> {
                                if (ex == null) {
                                    LoggerFactory.kafka().logProduce(TOPIC_PROJECT_LIKE_DECREASE, "프로젝트 좋아요 취소 이벤트 발송됨: projectId=" + targetId);
                                } else {
                                    LoggerFactory.kafka().logError(TOPIC_PROJECT_LIKE_DECREASE, "프로젝트 좋아요 취소 이벤트 발송 처리 실패: projectId=" + targetId, ex);
                                }
                            });
                } else {
                    kafkaTemplate.send(TOPIC_PROJECT_LIKE_INCREASE, String.valueOf(targetId), targetId)
                            .whenComplete((result, ex) -> {
                                if (ex == null) {
                                    LoggerFactory.kafka().logProduce(TOPIC_PROJECT_LIKE_INCREASE, "프로젝트 좋아요 이벤트 발송됨: projectId=" + targetId);
                                } else {
                                    LoggerFactory.kafka().logError(TOPIC_PROJECT_LIKE_INCREASE, "프로젝트 좋아요 이벤트 발송 처리 실패: projectId=" + targetId, ex);
                                }
                            });
                }
            }
            case COMMENT -> {
                if (previouslyLiked) {
                    kafkaTemplate.send(TOPIC_COMMENT_LIKE_DECREASE, String.valueOf(targetId), targetId)
                            .whenComplete((result, ex) -> {
                                if (ex == null) {
                                    LoggerFactory.kafka().logProduce(TOPIC_COMMENT_LIKE_DECREASE, "댓글 좋아요 취소 이벤트 발송됨: commentId=" + targetId);
                                } else {
                                    LoggerFactory.kafka().logError(TOPIC_COMMENT_LIKE_DECREASE, "댓글 좋아요 취소 이벤트 발송 처리 실패: commentId=" + targetId, ex);
                                }
                            });
                } else {
                    kafkaTemplate.send(TOPIC_COMMENT_LIKE_INCREASE, String.valueOf(targetId), targetId)
                            .whenComplete((result, ex) -> {
                                if (ex == null) {
                                    LoggerFactory.kafka().logProduce(TOPIC_COMMENT_LIKE_INCREASE, "댓글 좋아요 이벤트 발송됨: commentId=" + targetId);
                                } else {
                                    LoggerFactory.kafka().logError(TOPIC_COMMENT_LIKE_INCREASE, "댓글 좋아요 이벤트 발송 처리 실패: commentId=" + targetId, ex);
                                }
                            });
                }
            }
        }
    }
}
