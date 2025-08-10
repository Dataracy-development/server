package com.dataracy.modules.project.application.port.out.query.read;

import com.dataracy.modules.project.application.dto.response.support.ProjectWithDataIdsResponse;
import com.dataracy.modules.project.domain.model.Project;

import java.util.Optional;

public interface FindProjectPort {
    /**
     * 주어진 프로젝트 ID로 프로젝트를 조회합니다.
     *
     * @param projectId 조회할 프로젝트의 고유 ID
     * @return 프로젝트가 존재하면 해당 프로젝트를 포함한 Optional, 없으면 빈 Optional
     */
    Optional<Project> findProjectById(Long projectId);

    /**
     * 지정된 프로젝트 ID에 해당하는 프로젝트와 연관된 데이터 ID 목록을 조회합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 프로젝트와 데이터 ID 목록이 포함된 응답 객체를 Optional로 반환하며, 해당 프로젝트가 없으면 빈 Optional을 반환합니다.
     */
    Optional<ProjectWithDataIdsResponse> findProjectWithDataById(Long projectId);
}
