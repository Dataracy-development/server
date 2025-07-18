package com.dataracy.modules.project.adapter.persistence.impl;

import com.dataracy.modules.project.adapter.persistence.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.persistence.mapper.ProjectEntityMapper;
import com.dataracy.modules.project.adapter.persistence.repository.ProjectJpaRepository;
import com.dataracy.modules.project.application.port.out.ProjectRepositoryPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepositoryPort {
    private final ProjectJpaRepository projectJpaRepository;

    /**
     * 프로젝트 도메인 객체를 영속성 계층에 저장하고, 저장된 프로젝트 도메인 객체를 반환한다.
     *
     * @param project 저장할 프로젝트 도메인 객체
     * @return 저장된 프로젝트 도메인 객체
     * @throws ProjectException 프로젝트 저장에 실패한 경우 발생
     */
    @Override
    public Project saveProject(Project project) {
        try {
            ProjectEntity projectEntity = projectJpaRepository.save(ProjectEntityMapper.toEntity(project));
            return ProjectEntityMapper.toDomain(projectEntity);
        } catch (Exception e) {
            throw new ProjectException(ProjectErrorStatus.FAIL_SAVE_PROJECT);
        }
    }

    /**
     * 주어진 프로젝트 ID로 프로젝트를 조회하여, 존재할 경우 도메인 Project 객체를 Optional로 반환합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 프로젝트가 존재하면 Project 객체를 포함한 Optional, 존재하지 않으면 빈 Optional
     */
    @Override
    public Optional<Project> findProjectById(Long projectId) {
        return projectJpaRepository.findProjectById(projectId)
                .map(ProjectEntityMapper::toDomain);
    }

    /**
     * 주어진 프로젝트 ID에 해당하는 프로젝트의 파일 URL을 새로운 값으로 변경합니다.
     *
     * 프로젝트가 존재하지 않을 경우 {@code ProjectException}이 발생합니다.
     *
     * @param projectId 파일 URL을 변경할 프로젝트의 ID
     * @param fileUrl 새로 설정할 파일 URL
     */
    @Override
    public void updateFile(Long projectId, String fileUrl) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));
        projectEntity.updateFile(fileUrl);
    }
}
