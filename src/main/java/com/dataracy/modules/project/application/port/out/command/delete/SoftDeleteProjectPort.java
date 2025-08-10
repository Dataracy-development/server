package com.dataracy.modules.project.application.port.out.command.delete;

public interface SoftDeleteProjectPort {
    /**
     * 지정된 프로젝트를 소프트 삭제 상태로 변경합니다.
     *
     * @param projectId 삭제 처리할 프로젝트의 고유 식별자
     */
    void deleteProject(Long projectId);

    /**
     * 삭제된 프로젝트를 복구 상태로 전환합니다.
     *
     * @param projectId 복구할 프로젝트의 고유 식별자
     */
    void restoreProject(Long projectId);
}
