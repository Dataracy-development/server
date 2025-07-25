package com.dataracy.modules.project.application.port.in;

public interface ProjectDeleteUseCase {
    /**
 * 지정된 프로젝트를 삭제 상태로 표시합니다.
 *
 * @param projectId 삭제 상태로 표시할 프로젝트의 식별자
 */
void markAsDelete(Long projectId);
}
