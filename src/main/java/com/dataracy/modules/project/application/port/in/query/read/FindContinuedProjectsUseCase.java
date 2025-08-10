package com.dataracy.modules.project.application.port.in.query.read;

import com.dataracy.modules.project.application.dto.response.read.ContinuedProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindContinuedProjectsUseCase {
    /**
     * 지정된 프로젝트 ID를 기준으로 연속 프로젝트 목록을 페이징하여 조회합니다.
     *
     * @param projectId 연속 프로젝트를 조회할 기준 프로젝트의 ID
     * @param pageable 페이징 및 정렬 정보를 포함하는 객체
     * @return 연속 프로젝트 응답 객체의 페이지 결과
     */
    Page<ContinuedProjectResponse> findContinuedProjects(Long projectId, Pageable pageable);
}
