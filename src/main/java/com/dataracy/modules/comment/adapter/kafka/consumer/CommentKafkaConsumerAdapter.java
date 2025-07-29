package com.dataracy.modules.comment.adapter.kafka.consumer;

import com.dataracy.modules.comment.application.port.in.DecreaseLikeCountUseCase;
import com.dataracy.modules.comment.application.port.in.IncreaseLikeCountUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentKafkaConsumerAdapter {

    private final IncreaseLikeCountUseCase increaseLikeCountUseCase;
    private final DecreaseLikeCountUseCase decreaseLikeCountUseCase;

    @KafkaListener(
            topics = "${spring.kafka.consumer.comment-like-increase.topic:comment-like-increase-topic}",
            groupId = "${spring.kafka.consumer.comment-like-increase.group-id:comment-like-increase-consumer-group}",
            containerFactory = "longKafkaListenerContainerFactory"
    )
    public void consumeLikeIncrease(Long commentId) {
        log.info("[Kafka] 댓글 좋아요 이벤트 수신됨: commentId:{}", commentId);
        increaseLikeCountUseCase.increaseLike(commentId);
    }

    @KafkaListener(
            topics = "${spring.kafka.consumer.comment-like-decrease.topic:comment-like-decrease-topic}",
            groupId = "${spring.kafka.consumer.comment-like-decrease.group-id:comment-like-decrease-consumer-group}",
            containerFactory = "longKafkaListenerContainerFactory"
    )
    public void consumeLikeDecrease(Long commentId) {
        log.info("[Kafka] 댓글 좋아요 취소 이벤트 수신됨: commentId:{}", commentId);
        decreaseLikeCountUseCase.decreaseLike(commentId);
    }
}
