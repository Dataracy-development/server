package com.dataracy.modules.project.application.port.in.query.read;

import com.dataracy.modules.project.application.dto.response.read.ContinuedProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindContinuedProjectsUseCase {
    /**
     * 지정된 프로젝트 ID와 페이지 정보를 기반으로 연속 프로젝트 목록을 페이징하여 반환합니다.
     *
     * @param projectId 기준이 되는 프로젝트의 ID
     * @param pageable 페이징 및 정렬 정보를 담은 객체
     * @return 연속 프로젝트 응답 객체의 페이지
     */
    Page<ContinuedProjectResponse> findContinuedProjects(Long projectId, Pageable pageable);
}
