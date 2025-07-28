package com.dataracy.modules.project.application.port.in;

public interface IncreaseCommentCountUseCase {
    /**
 * 지정된 프로젝트의 댓글 수를 1 증가시킵니다.
 *
 * @param projectId 댓글 수를 증가시킬 프로젝트의 ID
 */
void increase(Long projectId);
}
