package com.dataracy.modules.comment.application.port.in.command.content;

public interface DeleteCommentUseCase {
    /**
     * 특정 프로젝트에서 지정한 댓글을 삭제합니다.
     *
     * @param projectId 댓글이 속한 프로젝트의 식별자
     * @param commentId 삭제할 댓글의 식별자
     */
    void deleteComment(Long projectId, Long commentId);
}
