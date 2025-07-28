package com.dataracy.modules.project.application.port.elasticsearch;

public interface ProjectDeletePort {
    /**
 * 지정된 프로젝트를 삭제된 상태로 변경합니다.
 *
 * @param projectId 삭제 상태로 변경할 프로젝트의 ID
 */
    void markAsDeleted(Long projectId);
    /**
 * 지정된 프로젝트를 삭제 상태에서 복구 상태로 변경합니다.
 *
 * @param projectId 복구할 프로젝트의 고유 식별자
 */
    void markAsRestore(Long projectId);
}
