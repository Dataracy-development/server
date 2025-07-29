package com.dataracy.modules.like.application.port.out;

public interface LikeKafkaProducerPort {
void sendProjectLikeIncreaseEvent(Long projectId);
void sendProjectLikeDecreaseEvent(Long projectId);
    void sendCommentLikeIncreaseEvent(Long commentId);
    void sendCommentLikeDecreaseEvent(Long commentId);
}
