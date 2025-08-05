package com.dataracy.modules.like.application.port.out.command;

public interface SendLikeEventPort {
/**
 * 지정된 프로젝트의 좋아요 증가 이벤트를 Kafka로 전송합니다.
 *
 * @param projectId 좋아요가 증가한 프로젝트의 ID
 */
void sendProjectLikeIncreaseEvent(Long projectId);
/**
 * 지정된 프로젝트의 좋아요 감소 이벤트를 Kafka로 전송합니다.
 *
 * @param projectId 좋아요 감소 이벤트를 전송할 프로젝트의 ID
 */
void sendProjectLikeDecreaseEvent(Long projectId);
    /**
 * 지정된 댓글에 대한 좋아요 증가 이벤트를 Kafka로 전송합니다.
 *
 * @param commentId 좋아요가 증가한 댓글의 ID
 */
void sendCommentLikeIncreaseEvent(Long commentId);
    /**
 * 지정된 댓글의 좋아요 감소 이벤트를 Kafka로 전송합니다.
 *
 * @param commentId 좋아요 감소 이벤트를 전송할 댓글의 ID
 */
void sendCommentLikeDecreaseEvent(Long commentId);
}
