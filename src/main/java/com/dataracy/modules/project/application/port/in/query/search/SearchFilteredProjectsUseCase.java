package com.dataracy.modules.project.application.port.in.query.search;

import com.dataracy.modules.project.application.dto.request.search.FilteringProjectRequest;
import com.dataracy.modules.project.application.dto.response.search.FilteredProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchFilteredProjectsUseCase {
    /**
     * 프로젝트 필터링 조건과 페이지 정보를 기반으로 프로젝트 목록을 페이징하여 조회합니다.
     *
     * @param requestDto 프로젝트 필터링 조건이 담긴 요청 객체
     * @param pageable 페이지네이션 정보
     * @return 필터링된 프로젝트 목록의 페이지 객체
     */
    Page<FilteredProjectResponse> searchByFilters(FilteringProjectRequest requestDto, Pageable pageable);
}
