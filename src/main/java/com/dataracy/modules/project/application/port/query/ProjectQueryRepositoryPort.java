package com.dataracy.modules.project.application.port.query;

import com.dataracy.modules.project.application.dto.request.ProjectFilterRequest;
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
 * 지정된 개수만큼 인기 프로젝트 목록을 반환합니다.
 *
 * @param size 반환할 인기 프로젝트의 최대 개수
 * @return 인기 프로젝트 리스트
 */
List<Project> findPopularProjects(int size);

    Page<Project> searchByFilters(ProjectFilterRequest request, Pageable pageable);
}
