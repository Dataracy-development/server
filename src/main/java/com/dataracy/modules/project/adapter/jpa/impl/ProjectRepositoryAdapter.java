package com.dataracy.modules.project.adapter.jpa.impl;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.mapper.ProjectEntityMapper;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectDataJpaRepository;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import com.dataracy.modules.project.application.dto.request.ProjectModifyRequest;
import com.dataracy.modules.project.application.port.out.ProjectRepositoryPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepositoryPort {
    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectDataJpaRepository projectDataJpaRepository;

    /**
     * 프로젝트 도메인 객체를 저장하고, 저장된 최소 정보의 프로젝트 객체를 반환한다.
     *
     * @param project 저장할 프로젝트 도메인 객체
     * @return 저장된 프로젝트의 최소 정보 도메인 객체
     * @throws ProjectException 저장에 실패한 경우 발생
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
     * 프로젝트의 파일 URL을 새로운 값으로 업데이트합니다.
     *
     * 지정한 ID의 프로젝트가 존재하지 않으면 {@code ProjectException}이 발생합니다.
     *
     * @param projectId 파일 URL을 변경할 프로젝트의 ID
     * @param fileUrl 새로 설정할 파일 URL
     */
    @Override
    public void updateFile(Long projectId, String fileUrl) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));
        projectEntity.updateFile(fileUrl);
        projectJpaRepository.save(projectEntity);
    }

    /**
     * 주어진 프로젝트 ID에 해당하는 프로젝트가 존재하는지 여부를 반환합니다.
     *
     * @param projectId 존재 여부를 확인할 프로젝트의 ID
     * @return 프로젝트가 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean existsProjectById(Long projectId) {
        return projectJpaRepository.existsById(projectId);
    }

    @Override
    public Long findUserIdByProjectId(Long projectId) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));
        return projectEntity.getUserId();
    }

    @Override
    public Long findUserIdIncludingDeleted(Long projectId) {
        ProjectEntity projectEntity = projectJpaRepository.findIncludingDeleted(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));
        return projectEntity.getUserId();
    }

    @Override
    public void modify(Long projectId, ProjectModifyRequest requestDto) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));

        ProjectEntity parentProject = null;
        if (requestDto.parentProjectId() != null) {
            parentProject = projectJpaRepository.findById(requestDto.parentProjectId())
                    .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PARENT_PROJECT));
        }

        projectEntity.modify(requestDto, parentProject);

        // 기존 연결 제거 (DB 레벨에서)
        projectDataJpaRepository.deleteAllByProjectId(projectId);

        // 새로운 연결 생성
        List<ProjectDataEntity> newLinks = requestDto.dataIds().stream()
                .map(dataId -> ProjectDataEntity.of(projectEntity, dataId))
                .toList();

        // 저장
        projectDataJpaRepository.saveAll(newLinks);
        projectJpaRepository.save(projectEntity);
    }

    @Override
    public void delete(Long projectId) {
        ProjectEntity project = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));
        project.getChildProjects()
                .forEach(ProjectEntity::deleteParentProject);
        project.delete();
    }

    @Override
    public void restore(Long projectId) {
        ProjectEntity project = projectJpaRepository.findIncludingDeleted(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));
        project.restore();
    }
}
