package com.dataracy.modules.project.application.port.in;

public interface IncreaseLikeCountUseCase {
/**
 * 지정된 프로젝트의 좋아요 수를 1 증가시킵니다.
 *
 * @param projectId 좋아요 수를 증가시킬 프로젝트의 식별자
 */
void increaseLike(Long projectId);
}
