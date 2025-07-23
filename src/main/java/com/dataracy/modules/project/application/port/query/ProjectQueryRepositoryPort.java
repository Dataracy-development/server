package com.dataracy.modules.project.application.port.query;

import com.dataracy.modules.project.application.dto.request.ProjectFilterRequest;
import com.dataracy.modules.project.domain.enums.ProjectSortType;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProjectQueryRepositoryPort {
    /**
 * 주어진 프로젝트 ID로 프로젝트를 조회합니다.
 *
 * @param projectId 조회할 프로젝트의 ID
 * @return 프로젝트가 존재하면 해당 프로젝트를 포함한 Optional, 존재하지 않으면 빈 Optional
 */
Optional<Project> findProjectById(Long projectId);

/**
 * 지정한 개수만큼 인기 프로젝트 목록을 조회합니다.
 *
 * @param size 반환할 인기 프로젝트의 최대 개수
 * @return 인기 프로젝트의 리스트
 */
List<Project> findPopularProjects(int size);

    /**
 * 필터 조건, 페이지네이션, 정렬 기준에 따라 프로젝트를 검색하여 페이지 형태로 반환합니다.
 *
 * @param request 프로젝트 필터링 조건을 담은 요청 객체
 * @param pageable 페이지네이션 정보
 * @param sortType 프로젝트 정렬 기준
 * @return 검색 조건에 부합하는 프로젝트의 페이지 결과
 */
Page<Project> searchByFilters(ProjectFilterRequest request, Pageable pageable, ProjectSortType sortType);

    /**
 * 지정된 프로젝트 ID를 부모로 갖는 프로젝트가 존재하는지 여부를 반환합니다.
 *
 * @param projectId 부모 프로젝트의 ID
 * @return 해당 부모 프로젝트 ID를 가진 프로젝트가 하나 이상 존재하면 true, 아니면 false
 */
boolean existsByParentProjectId(Long projectId);

    /**
 * 지정된 프로젝트 ID에 해당하는 프로젝트 데이터가 존재하는지 여부를 반환합니다.
 *
 * @param projectId 존재 여부를 확인할 프로젝트의 ID
 * @return 프로젝트 데이터가 존재하면 true, 그렇지 않으면 false
 */
boolean existsProjectDataByProjectId(Long projectId);

    Page<Project> findContinueProjects(Long projectId, Pageable pageable);
}
