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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
     * 지정한 프로젝트의 파일 URL을 새로운 값으로 변경합니다.
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
        projectJpaRepository.save(projectEntity);
    }

    /**
     * 주어진 프로젝트 ID에 해당하는 프로젝트의 존재 여부를 반환합니다.
     *
     * @param projectId 확인할 프로젝트의 ID
     * @return 프로젝트가 존재하면 true, 존재하지 않으면 false
     */
    @Override
    public boolean existsProjectById(Long projectId) {
        return projectJpaRepository.existsById(projectId);
    }

    /**
     * 주어진 프로젝트 ID에 해당하는 프로젝트의 사용자 ID를 반환합니다.
     *
     * 프로젝트가 존재하지 않을 경우 {@code ProjectException}이 발생합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 프로젝트에 연결된 사용자 ID
     */
    @Override
    public Long findUserIdByProjectId(Long projectId) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));
        return projectEntity.getUserId();
    }

    /**
     * 주어진 프로젝트 ID에 해당하는(삭제된 프로젝트 포함) 프로젝트의 사용자 ID를 반환합니다.
     *
     * @param projectId 사용자 ID를 조회할 프로젝트의 ID
     * @return 프로젝트에 연결된 사용자 ID
     * @throws ProjectException 프로젝트가 존재하지 않을 경우 발생
     */
    @Override
    public Long findUserIdIncludingDeleted(Long projectId) {
        ProjectEntity projectEntity = projectJpaRepository.findIncludingDeleted(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));
        return projectEntity.getUserId();
    }

    /**
     * 프로젝트 정보를 수정하고 프로젝트-데이터 연관 관계를 동기화합니다.
     *
     * 프로젝트 ID에 해당하는 프로젝트를 조회하여 요청 정보와 부모 프로젝트(필요 시)를 반영해 수정합니다.
     * 기존 프로젝트-데이터 연결과 요청된 데이터 ID 목록을 비교하여, 불필요한 연결을 삭제하고 새로운 연결을 추가합니다.
     * 모든 변경 사항은 저장소에 반영됩니다.
     *
     * @param projectId 수정할 프로젝트의 ID
     * @param requestDto 프로젝트 수정 요청 정보
     * @throws ProjectException 프로젝트 또는 부모 프로젝트가 존재하지 않을 경우 발생
     */
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

        // 기존 연결과 새로운 연결 비교 후 필요한 것만 추가/삭제
        Set<Long> existingDataIds = projectDataJpaRepository.findDataIdsByProjectId(projectId);
        Set<Long> newDataIds = new HashSet<>(requestDto.dataIds());
        // 삭제할 연결
        Set<Long> toDelete = existingDataIds.stream()
                .filter(id -> !newDataIds.contains(id))
                .collect(Collectors.toSet());
        if (!toDelete.isEmpty()) {
            projectDataJpaRepository.deleteByProjectIdAndDataIdIn(projectId, toDelete);
        }

        // 추가할 연결
        Set<Long> toAdd = newDataIds.stream()
                .filter(id -> !existingDataIds.contains(id))
                .collect(Collectors.toSet());
        if (!toAdd.isEmpty()) {
            List<ProjectDataEntity> newLinks = toAdd.stream()
                    .map(dataId -> ProjectDataEntity.of(projectEntity, dataId))
                    .toList();
            projectDataJpaRepository.saveAll(newLinks);
        }
        projectJpaRepository.save(projectEntity);
    }

    /**
     * 프로젝트를 논리적으로 삭제하고, 모든 자식 프로젝트의 부모 프로젝트 참조를 제거합니다.
     *
     * 주어진 프로젝트 ID에 해당하는 프로젝트를 찾지 못하면 {@code ProjectException}이 발생합니다.
     */
    @Override
    public void delete(Long projectId) {
        ProjectEntity project = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));
        Set<ProjectEntity> childProjects = project.getChildProjects();
        childProjects.forEach(ProjectEntity::deleteParentProject);
        projectJpaRepository.saveAll(childProjects);

        project.delete();
        projectJpaRepository.save(project);
    }

    /**
     * 논리적으로 삭제된 프로젝트를 복구합니다.
     *
     * @param projectId 복구할 프로젝트의 ID
     * @throws ProjectException 해당 ID의 프로젝트가 존재하지 않을 경우 발생합니다.
     */
    @Override
    public void restore(Long projectId) {
        ProjectEntity project = projectJpaRepository.findIncludingDeleted(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));
        project.restore();
        projectJpaRepository.save(project);
    }

    @Override
    public void increaseViewCount(Long projectId, Long count) {
        projectJpaRepository.increaseViewCount(projectId, count);
    }

    @Override
    public void increase(Long projectId) {
        projectJpaRepository.increaseCommentCount(projectId);
    }

    @Override
    public void decrease(Long projectId) {
        projectJpaRepository.decreaseCommentCount(projectId);
    }
}
