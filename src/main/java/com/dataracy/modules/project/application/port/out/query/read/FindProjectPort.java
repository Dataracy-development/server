package com.dataracy.modules.project.application.port.out.query.read;

import com.dataracy.modules.project.domain.model.Project;

import java.util.Optional;

public interface FindProjectPort {
    /**
     * 주어진 프로젝트 ID로 프로젝트를 조회합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 프로젝트가 존재하면 해당 프로젝트를 포함한 Optional, 존재하지 않으면 빈 Optional
     */
    Optional<Project> findProjectById(Long projectId);
}
