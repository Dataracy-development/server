package com.dataracy.modules.project.adapter.persistence.repositoryImpl;

import com.dataracy.modules.project.adapter.persistence.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.persistence.mapper.ProjectEntityMapper;
import com.dataracy.modules.project.adapter.persistence.repository.ProjectJpaRepository;
import com.dataracy.modules.project.application.port.out.ProjectRepositoryPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepositoryPort {
    private final ProjectEntityMapper projectEntityMapper;
    private final ProjectJpaRepository projectJpaRepository;

    /**
     * 프로젝트 저장
     * @param project 프로젝트
     * @return 저장된 프로젝트
     */
    @Override
    public void saveProject(Project project) {
        try {
            projectJpaRepository.save(projectEntityMapper.toEntity(project));
        } catch (Exception e) {
            throw new ProjectException(ProjectErrorStatus.FAIL_SAVE_PROJECT);
        }
    }

    /**
     * 프로젝트 아이디를 통해 프로젝트 조회
     * @param projectId 프로젝트 아이디
     * @return 조회된 프로젝트
     */
    @Override
    public Project findProjectById(Long projectId) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElse(null);
        if (projectEntity == null) {
            return null;
        }
        return projectEntityMapper.toDomain(projectEntity);
    }
}
