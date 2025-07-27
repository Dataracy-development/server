package com.dataracy.modules.project.application.port.elasticsearch;

public interface ProjectDeletePort {
    /**
     * 지정된 프로젝트를 삭제된 상태로 표시합니다.
     *
     * @param projectId 삭제 상태로 표시할 프로젝트의 ID
     */
    void markAsDeleted(Long projectId);
    /**
     * 삭제된 프로젝트를 복구 상태로 표시합니다.
     *
     * @param projectId 복구할 프로젝트의 고유 식별자
     */
    void markAsRestore(Long projectId);
}
