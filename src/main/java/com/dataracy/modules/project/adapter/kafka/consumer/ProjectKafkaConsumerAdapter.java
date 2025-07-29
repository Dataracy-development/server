package com.dataracy.modules.project.adapter.kafka.consumer;

import com.dataracy.modules.project.application.port.in.DecreaseCommentCountUseCase;
import com.dataracy.modules.project.application.port.in.DecreaseLikeCountUseCase;
import com.dataracy.modules.project.application.port.in.IncreaseCommentCountUseCase;
import com.dataracy.modules.project.application.port.in.IncreaseLikeCountUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectKafkaConsumerAdapter {

    private final IncreaseCommentCountUseCase increaseCommentCountUseCase;
    private final DecreaseCommentCountUseCase decreaseCommentCountUseCase;
    private final IncreaseLikeCountUseCase increaseLikeCountUseCase;
    private final DecreaseLikeCountUseCase decreaseLikeCountUseCase;

    /**
     * 프로젝트 댓글 작성 이벤트를 수신하여 해당 프로젝트의 댓글 수를 1 증가시킵니다.
     *
     * @param projectId 댓글이 추가된 프로젝트의 ID
     */
    @KafkaListener(
            topics = "${spring.kafka.consumer.comment-upload.topic:comment-uploaded-topic}",
            groupId = "${spring.kafka.consumer.comment-upload.group-id:project-comment-upload-consumer-group}",
            containerFactory = "longKafkaListenerContainerFactory"
    )
    public void consumeCommentUpload(Long projectId) {
        log.info("[Kafka] 댓글 작성 이벤트 수신됨: projectId:{}", projectId);
        increaseCommentCountUseCase.increase(projectId);
    }

    /**
     * 프로젝트의 댓글 삭제 이벤트를 수신하여 해당 프로젝트의 댓글 수를 감소시킵니다.
     *
     * @param projectId 댓글이 삭제된 프로젝트의 ID
     */
    @KafkaListener(
            topics = "${spring.kafka.consumer.comment-delete.topic:comment-deleted-topic}",
            groupId = "${spring.kafka.consumer.comment-delete.group-id:project-comment-delete-consumer-group}",
            containerFactory = "longKafkaListenerContainerFactory"
    )
    public void consumeCommentDelete(Long projectId) {
        log.info("[Kafka] 댓글 삭제 이벤트 수신됨: projectId:{}", projectId);
        decreaseCommentCountUseCase.decrease(projectId);
    }

    /**
     * Kafka 이벤트를 수신하여 지정된 프로젝트의 좋아요 수를 증가시킵니다.
     *
     * @param projectId 좋아요 수를 증가시킬 프로젝트의 ID
     */
    @KafkaListener(
            topics = "${spring.kafka.consumer.project-like-increase.topic:project-like-increase-topic}",
            groupId = "${spring.kafka.consumer.project-like-increase.group-id:project-like-increase-consumer-group}",
            containerFactory = "longKafkaListenerContainerFactory"
    )
    public void consumeLikeIncrease(Long projectId) {
        log.info("[Kafka] 프로젝트 좋아요 이벤트 수신됨: projectId:{}", projectId);
        increaseLikeCountUseCase.increaseLike(projectId);
    }

    /**
     * Kafka에서 프로젝트 좋아요 취소 이벤트를 수신하여 해당 프로젝트의 좋아요 수를 감소시킵니다.
     *
     * @param projectId 좋아요 수를 감소시킬 프로젝트의 ID
     */
    @KafkaListener(
            topics = "${spring.kafka.consumer.project-like-decrease.topic:project-like-decrease-topic}",
            groupId = "${spring.kafka.consumer.project-like-decrease.group-id:project-like-decrease-consumer-group}",
            containerFactory = "longKafkaListenerContainerFactory"
    )
    public void consumeLikeDecrease(Long projectId) {
        log.info("[Kafka] 프로젝트 좋아요 취소 이벤트 수신됨: projectId:{}", projectId);
        decreaseLikeCountUseCase.decreaseLike(projectId);
    }
}
