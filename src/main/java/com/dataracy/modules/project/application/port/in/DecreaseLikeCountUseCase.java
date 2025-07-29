package com.dataracy.modules.project.application.port.in;

public interface DecreaseLikeCountUseCase {
/**
 * 지정된 프로젝트의 좋아요 수를 1 감소시킵니다.
 *
 * @param projectId 좋아요 수를 감소시킬 프로젝트의 ID
 */
void decreaseLike(Long projectId);
}
