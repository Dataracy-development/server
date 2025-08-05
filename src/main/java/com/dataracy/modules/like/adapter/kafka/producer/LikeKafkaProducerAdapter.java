package com.dataracy.modules.like.adapter.kafka.producer;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.like.application.port.out.command.SendLikeEventPort;
import com.dataracy.modules.like.domain.enums.TargetType;
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

    @Override
    public void sendLikeEvent(TargetType targetType, Long targetId, boolean previouslyLiked) {
        switch (targetType) {
            case PROJECT -> {
                if (previouslyLiked) {
                    kafkaTemplate.send(TOPIC_PROJECT_LIKE_DECREASE, String.valueOf(targetId), targetId)
                            .whenComplete((result, ex) -> {
                                if (ex == null) {
                                    LoggerFactory.kafka().logError(TOPIC_PROJECT_LIKE_DECREASE, "프로젝트 좋아요 취소 이벤트 이벤트 발송 처리 실패: projectId=" + targetId, ex);
                                } else {
                                    LoggerFactory.kafka().logProduce(TOPIC_PROJECT_LIKE_DECREASE, "프로젝트 좋아요 취소 이벤트 이벤트 발송됨: projectId=" + targetId);
                                }
                            });
                } else {
                    kafkaTemplate.send(TOPIC_PROJECT_LIKE_INCREASE, String.valueOf(targetId), targetId)
                            .whenComplete((result, ex) -> {
                                if (ex == null) {
                                    LoggerFactory.kafka().logError(TOPIC_PROJECT_LIKE_INCREASE, "프로젝트 좋아요 이벤트 발송 처리 실패: projectId=" + targetId, ex);
                                } else {
                                    LoggerFactory.kafka().logProduce(TOPIC_PROJECT_LIKE_INCREASE, "프로젝트 좋아요 이벤트 발송됨: projectId=" + targetId);
                                }
                            });
                }
            }
            case COMMENT -> {
                if (previouslyLiked) {
                    kafkaTemplate.send(TOPIC_COMMENT_LIKE_DECREASE, String.valueOf(targetId), targetId)
                            .whenComplete((result, ex) -> {
                                if (ex == null) {
                                    LoggerFactory.kafka().logError(TOPIC_COMMENT_LIKE_DECREASE, "댓글 좋아요 취소 이벤트 이벤트 발송 처리 실패: commentId=" + targetId, ex);
                                } else {
                                    LoggerFactory.kafka().logProduce(TOPIC_COMMENT_LIKE_DECREASE, "댓글 좋아요 취소 이벤트 이벤트 발송됨: commentId=" + targetId);
                                }
                            });
                }
                else {
                    kafkaTemplate.send(TOPIC_COMMENT_LIKE_INCREASE, String.valueOf(targetId), targetId)
                            .whenComplete((result, ex) -> {
                                if (ex == null) {
                                    LoggerFactory.kafka().logError(TOPIC_COMMENT_LIKE_INCREASE, "댓글 좋아요 이벤트 이벤트 발송 처리 실패: commentId=" + targetId, ex);
                                } else {
                                    LoggerFactory.kafka().logProduce(TOPIC_COMMENT_LIKE_INCREASE, "댓글 좋아요 이벤트 이벤트 발송됨: commentId=" + targetId);
                                }
                            });
                }
            }
        }
    }
}
