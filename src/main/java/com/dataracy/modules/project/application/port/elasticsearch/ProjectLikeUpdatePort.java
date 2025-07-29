package com.dataracy.modules.project.application.port.elasticsearch;

public interface ProjectLikeUpdatePort {
    /**
 * 지정된 프로젝트의 좋아요 수를 1 증가시킵니다.
 *
 * @param projectId 좋아요 수를 증가시킬 프로젝트의 식별자
 */
void increaseLikeCount(Long projectId);
    /**
 * 지정된 프로젝트의 좋아요 수를 1 감소시킵니다.
 *
 * @param projectId 좋아요 수를 감소시킬 프로젝트의 식별자
 */
void decreaseLikeCount(Long projectId);
}
