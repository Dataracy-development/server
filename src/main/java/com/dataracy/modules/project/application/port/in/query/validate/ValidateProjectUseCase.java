package com.dataracy.modules.project.application.port.in.query.validate;

public interface ValidateProjectUseCase {
    /**
 * 지정된 프로젝트 ID에 해당하는 프로젝트의 유효성을 검사합니다.
 *
 * @param projectId 유효성 검사를 수행할 프로젝트의 ID
 */
    void validateProject(Long projectId);
}
