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
     * 프로젝트 도메인 객체를 영속성 계층에 저장한다.
     *
     * @param project 저장할 프로젝트 도메인 객체
     * @throws ProjectException 프로젝트 저장에 실패한 경우 발생
     * return 업로드된 프로젝트 아이디
     */
    @Override
    public Long saveProject(Project project) {
        try {
            ProjectEntity projectEntity = projectJpaRepository.save(projectEntityMapper.toEntity(project));
            return projectEntity.getId();
        } catch (Exception e) {
            throw new ProjectException(ProjectErrorStatus.FAIL_SAVE_PROJECT);
        }
    }

    /**
     * 주어진 프로젝트 ID로 프로젝트를 조회하여 도메인 객체로 반환합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 프로젝트가 존재하면 Project 도메인 객체, 없으면 null
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

    @Override
    public void updateFile(Long projectId, String imageUrl) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElse(null);
        projectEntity.updateFile(imageUrl);
    }
}
