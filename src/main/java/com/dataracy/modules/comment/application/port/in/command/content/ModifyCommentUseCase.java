package com.dataracy.modules.comment.application.port.in.command.content;

import com.dataracy.modules.comment.application.dto.request.command.ModifyCommentRequest;

public interface ModifyCommentUseCase {
    /**
 * 지정된 프로젝트 내에서 특정 댓글을 수정합니다.
 *
 * @param projectId 댓글이 속한 프로젝트의 식별자
 * @param commentId 수정할 댓글의 식별자
 * @param requestDto 댓글 수정에 필요한 정보가 담긴 요청 객체
 */
void modifyComment(Long projectId, Long commentId, ModifyCommentRequest requestDto);
}
