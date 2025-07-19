package com.dataracy.modules.project.application.port.query;

import com.dataracy.modules.project.domain.model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectQueryRepositoryPort {
    /**
 * 주어진 프로젝트 ID로 프로젝트를 조회합니다.
 *
 * @param projectId 조회할 프로젝트의 ID
 * @return 해당 ID에 해당하는 프로젝트가 존재하면 Optional에 담아 반환하며, 없으면 빈 Optional을 반환합니다.
 */
Optional<Project> findProjectById(Long projectId);

List<Project> findPopularProjects(int size);
}
