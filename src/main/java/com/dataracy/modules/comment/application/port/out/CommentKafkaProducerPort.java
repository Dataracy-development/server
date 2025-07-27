package com.dataracy.modules.comment.application.port.out;

public interface CommentKafkaProducerPort {
    void sendCommentUploadedEvent(Long commentId);
    void sendCommentDeletedEvent(Long commentId);
}
