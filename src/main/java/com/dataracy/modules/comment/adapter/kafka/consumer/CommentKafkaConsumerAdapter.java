package com.dataracy.modules.comment.adapter.kafka.consumer;

import com.dataracy.modules.comment.application.port.in.command.count.DecreaseLikeCountUseCase;
import com.dataracy.modules.comment.application.port.in.command.count.IncreaseLikeCountUseCase;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentKafkaConsumerAdapter {

    private final IncreaseLikeCountUseCase increaseLikeCountUseCase;
    private final DecreaseLikeCountUseCase decreaseLikeCountUseCase;

    @Value("${spring.kafka.consumer.comment-like-increase.topic:comment-like-increase-topic}")
    private String COMMENT_LIKE_INCREASE_TOPIC;

    @Value("${spring.kafka.consumer.comment-like-decrease.topic:comment-like-decrease-topic}")
    private String COMMENT_LIKE_DECREASE_TOPIC;

    /**
     * Kafka에서 댓글 좋아요 증가 이벤트를 수신하여 해당 댓글의 좋아요 수를 증가시킵니다.
     *
     * @param commentId 좋아요 수를 증가시킬 댓글의 ID
     */
    @KafkaListener(
            topics = "${spring.kafka.consumer.comment-like-increase.topic:comment-like-increase-topic}",
            groupId = "${spring.kafka.consumer.comment-like-increase.group-id:comment-like-increase-consumer-group}",
            containerFactory = "longKafkaListenerContainerFactory"
    )
    public void consumeLikeIncrease(Long commentId) {
        try {
            LoggerFactory.kafka().logConsume(COMMENT_LIKE_INCREASE_TOPIC, "댓글 좋아요 이벤트 수신됨: commentId=" + commentId);
            increaseLikeCountUseCase.increaseLikeCount(commentId);
            LoggerFactory.kafka().logConsume(COMMENT_LIKE_INCREASE_TOPIC, "댓글 좋아요 이벤트 처리 완료: commentId=" + commentId);
        } catch (Exception e) {
            LoggerFactory.kafka().logError(COMMENT_LIKE_INCREASE_TOPIC, "댓글 좋아요 이벤트 처리 실패: commentId=" + commentId, e);
            throw e; // 재시도를 위해 예외 재던지기
        }
    }

    /**
     * Kafka에서 댓글 좋아요 취소 이벤트를 수신하여 해당 댓글의 좋아요 수를 감소시킵니다.
     *
     * @param commentId 좋아요 수를 감소시킬 대상 댓글의 ID
     */
    @KafkaListener(
            topics = "${spring.kafka.consumer.comment-like-decrease.topic:comment-like-decrease-topic}",
            groupId = "${spring.kafka.consumer.comment-like-decrease.group-id:comment-like-decrease-consumer-group}",
            containerFactory = "longKafkaListenerContainerFactory"
    )
    public void consumeLikeDecrease(Long commentId) {
        try {
            LoggerFactory.kafka().logConsume(COMMENT_LIKE_DECREASE_TOPIC, "댓글 좋아요 취소 이벤트 수신됨: commentId=" + commentId);
            decreaseLikeCountUseCase.decreaseLikeCount(commentId);
            LoggerFactory.kafka().logConsume(COMMENT_LIKE_DECREASE_TOPIC, "댓글 좋아요 취소 이벤트 처리 완료: commentId=" + commentId);
        } catch (Exception e) {
            LoggerFactory.kafka().logError(COMMENT_LIKE_DECREASE_TOPIC, "댓글 좋아요 취소 이벤트 처리 실패: commentId=" + commentId, e);
            throw e; // 재시도를 위해 예외 재던지기
        }
    }
}
