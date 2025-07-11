package com.dataracy.modules.project.adapter.persistence.repositoryImpl;

import com.dataracy.modules.project.adapter.persistence.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.persistence.mapper.ProjectEntityMapper;
import com.dataracy.modules.project.adapter.persistence.repository.ProjectJpaRepository;
import com.dataracy.modules.project.application.port.out.ProjectRepositoryPort;
import com.dataracy.modules.project.domain.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepositoryPort {
    private final ProjectJpaRepository projectJpaRepository;

    /**
     * 프로젝트 저장
     * @param project 프로젝트
     * @return 저장된 프로젝트
     */
    @Override
    public Project saveProject(Project project) {
        ProjectEntity savedProject = projectJpaRepository.save(ProjectEntityMapper.toEntity(project));
        return ProjectEntityMapper.toDomain(savedProject);
    }
}
