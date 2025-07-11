package com.dataracy.modules.project.application.port.out;

import com.dataracy.modules.project.domain.model.Project;

/**
 * 프로젝트 db 포트
 */
public interface ProjectRepositoryPort {
    Project saveProject(Project project);
}
