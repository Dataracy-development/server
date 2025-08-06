package com.dataracy.modules.comment.application.port.out.command.update;

import com.dataracy.modules.comment.application.dto.request.command.ModifyCommentRequest;

public interface UpdateCommentPort {
    /**
     * 지정된 프로젝트와 댓글 ID에 해당하는 댓글을 주어진 수정 요청 정보로 변경합니다.
     *
     * @param projectId 수정할 댓글이 속한 프로젝트의 ID
     * @param commentId 수정할 댓글의 ID
     * @param requestDto 댓글 수정에 필요한 정보가 담긴 요청 객체
     */
    void modifyComment(Long projectId, Long commentId, ModifyCommentRequest requestDto);
}
