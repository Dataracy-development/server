package com.dataracy.modules.project.application.port.in.command.content;

public interface DeleteProjectUseCase {
    /**
     * 지정된 프로젝트를 삭제 상태로 표시합니다.
     *
     * @param projectId 삭제 상태로 표시할 프로젝트의 식별자
     */
    void deleteProject(Long projectId);
}
