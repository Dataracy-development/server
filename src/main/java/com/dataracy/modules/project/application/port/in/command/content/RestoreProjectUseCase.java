package com.dataracy.modules.project.application.port.in.command.content;

public interface RestoreProjectUseCase {
    /**
     * 지정된 프로젝트를 복구 상태로 표시합니다.
     *
     * @param projectId 복구할 프로젝트의 ID
     */
    void restoreProject(Long projectId);
}
