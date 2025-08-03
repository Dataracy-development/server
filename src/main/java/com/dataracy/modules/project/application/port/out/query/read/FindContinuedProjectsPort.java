package com.dataracy.modules.project.application.port.out.query.read;

import com.dataracy.modules.project.domain.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindContinuedProjectsPort {
    /**
     * 지정된 프로젝트 ID를 기준으로 이어지는 프로젝트 목록을 페이지 단위로 조회합니다.
     *
     * @param projectId 기준이 되는 프로젝트의 ID
     * @param pageable 페이지네이션 정보
     * @return 이어지는 프로젝트의 페이지 결과
     */
    Page<Project> findContinuedProjects(Long projectId, Pageable pageable);
}
