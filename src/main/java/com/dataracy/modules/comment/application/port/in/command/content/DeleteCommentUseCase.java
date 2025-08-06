package com.dataracy.modules.comment.application.port.in.command.content;

public interface DeleteCommentUseCase {
    /**
 * 지정된 프로젝트 내에서 주어진 댓글 ID에 해당하는 댓글을 삭제합니다.
 *
 * @param projectId 댓글이 속한 프로젝트의 ID
 * @param commentId 삭제할 댓글의 ID
 */
void deleteComment(Long projectId, Long commentId);
}
