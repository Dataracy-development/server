package com.dataracy.modules.project.application.port.in.command.count;

public interface DecreaseCommentCountUseCase {
    /**
     * 지정된 프로젝트의 댓글 수를 감소시킵니다.
     *
     * @param projectId 댓글 수를 감소시킬 프로젝트의 ID
     */
    void decreaseCommentCount(Long projectId);
}
