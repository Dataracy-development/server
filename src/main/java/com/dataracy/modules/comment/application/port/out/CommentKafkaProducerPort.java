package com.dataracy.modules.comment.application.port.out;

public interface CommentKafkaProducerPort {
    void sendCommentUploadedEvent(Long projectId);
    void sendCommentDeletedEvent(Long projectId);
}
