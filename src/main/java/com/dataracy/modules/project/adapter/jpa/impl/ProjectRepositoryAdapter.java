package com.dataracy.modules.project.adapter.jpa.impl;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.mapper.ProjectEntityMapper;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import com.dataracy.modules.project.application.port.out.ProjectRepositoryPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepositoryPort {
    private final ProjectJpaRepository projectJpaRepository;

    /**
     * 프로젝트 도메인 객체를 저장한 후, 저장된 객체를 반환한다.
     *
     * @param project 저장할 프로젝트 도메인 객체
     * @return 저장된 프로젝트 도메인 객체
     * @throws ProjectException 저장에 실패할 경우 발생
     */
    @Override
    public Project saveProject(Project project) {
        try {
            ProjectEntity projectEntity = projectJpaRepository.save(ProjectEntityMapper.toEntity(project));
            return ProjectEntityMapper.toMinimal(projectEntity);
        } catch (Exception e) {
            throw new ProjectException(ProjectErrorStatus.FAIL_SAVE_PROJECT);
        }
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
