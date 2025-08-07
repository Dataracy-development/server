package com.dataracy.modules.comment.application.port.out.command.event;

public interface SendCommentEventPort {
    /**
 * 지정된 프로젝트에 대한 댓글 업로드 이벤트를 전송합니다.
 *
 * @param projectId 이벤트를 전송할 대상 프로젝트의 ID
 */
    void sendCommentUploadedEvent(Long projectId);

    /**
 * 지정된 프로젝트 ID에 대한 댓글 삭제 이벤트를 전송합니다.
 *
 * @param projectId 이벤트를 전송할 대상 프로젝트의 ID
 */
    void sendCommentDeletedEvent(Long projectId);
}
