package com.dataracy.modules.comment.application.port.in.command.content;

import com.dataracy.modules.comment.application.dto.request.command.ModifyCommentRequest;

public interface ModifyCommentUseCase {
    /**
 * 지정된 프로젝트에서 특정 댓글을 수정합니다.
 *
 * @param projectId 댓글이 속한 프로젝트의 ID
 * @param commentId 수정할 댓글의 ID
 * @param requestDto 댓글 수정에 필요한 데이터가 포함된 요청 객체
 */
void modifyComment(Long projectId, Long commentId, ModifyCommentRequest requestDto);
}
