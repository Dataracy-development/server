package com.dataracy.modules.comment.application.port.out.command.delete;

public interface DeleteCommentPort {
    /**
 * 지정된 프로젝트의 특정 댓글을 삭제합니다.
 *
 * @param projectId 댓글이 속한 프로젝트의 ID입니다.
 * @param commentId 삭제할 댓글의 ID입니다.
 */
    void deleteComment(Long projectId, Long commentId);
}
