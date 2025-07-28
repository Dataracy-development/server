package com.dataracy.modules.project.adapter.kafka.consumer;

import com.dataracy.modules.project.application.port.in.DecreaseCommentCountUseCase;
import com.dataracy.modules.project.application.port.in.IncreaseCommentCountUseCase;
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

    /**
     * 프로젝트에 대한 댓글 작성 이벤트를 수신하여 해당 프로젝트의 댓글 수를 증가시킵니다.
     *
     * @param projectId 댓글이 작성된 프로젝트의 식별자
     */
    @KafkaListener(
            topics = "${spring.kafka.consumer.comment-upload.topic.topic:comment-uploaded-topic}",
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
     * @param projectId 댓글이 삭제된 프로젝트의 식별자
     */
    @KafkaListener(
            topics = "${spring.kafka.consumer.comment-delete.topic.topic:comment-deleted-topic}",
            groupId = "${spring.kafka.consumer.comment-delete.group-id:project-comment-delete-consumer-group}",
            containerFactory = "longKafkaListenerContainerFactory"
    )
    public void consumeCommentDelete(Long projectId) {
        log.info("[Kafka] 댓글 삭제 이벤트 수신됨: projectId:{}", projectId);
        decreaseCommentCountUseCase.decrease(projectId);
    }
}
