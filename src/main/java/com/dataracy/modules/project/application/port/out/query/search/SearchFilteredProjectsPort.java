package com.dataracy.modules.project.application.port.out.query.search;

import com.dataracy.modules.project.application.dto.request.search.FilteringProjectRequest;
import com.dataracy.modules.project.domain.enums.ProjectSortType;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchFilteredProjectsPort {
    /**
     * 주어진 필터 조건, 페이지네이션, 정렬 기준에 따라 프로젝트 목록을 검색하여 페이지 형태로 반환합니다.
     *
     * @param request 프로젝트 검색에 사용할 필터링 조건이 포함된 요청 객체
     * @param pageable 결과 페이지네이션 정보
     * @param sortType 프로젝트 정렬 기준
     * @return 조건에 부합하는 프로젝트의 페이지 결과
     */
    Page<Project> searchByFilters(FilteringProjectRequest request, Pageable pageable, ProjectSortType sortType);
}
