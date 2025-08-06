package com.dataracy.modules.comment.application.port.out.command.delete;

public interface DeleteCommentPort {
    /**
     * 지정된 프로젝트 ID와 댓글 ID에 해당하는 댓글을 삭제합니다.
     *
     * @param projectId 댓글이 속한 프로젝트의 ID
     * @param commentId 삭제할 댓글의 ID
     */
    void deleteComment(Long projectId, Long commentId);
}
