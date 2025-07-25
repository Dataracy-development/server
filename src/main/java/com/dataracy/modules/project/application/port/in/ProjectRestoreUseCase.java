package com.dataracy.modules.project.application.port.in;

public interface ProjectRestoreUseCase {
    /**
 * 지정된 프로젝트를 복구 상태로 표시합니다.
 *
 * @param projectId 복구할 프로젝트의 ID
 */
void markAsRestore(Long projectId);
}
