package com.dataracy.modules.project.adapter.jpa.impl.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import com.dataracy.modules.project.application.port.out.command.delete.SoftDeleteProjectPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository("softDeleteProjectDbAdapter")
@RequiredArgsConstructor
public class SoftDeleteProjectDbAdapter implements SoftDeleteProjectPort {

    private final ProjectJpaRepository projectJpaRepository;

    /**
     * 프로젝트를 논리적으로 삭제하고, 모든 자식 프로젝트의 부모 프로젝트 참조를 제거합니다.
     *
     * 주어진 프로젝트 ID에 해당하는 프로젝트를 찾지 못하면 {@code ProjectException}이 발생합니다.
     */
    @Override
    public void deleteProject(Long projectId) {
        ProjectEntity project = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning("ProjectEntity", "해당 프로젝트가 존재하지 않습니다. projectId=" + projectId);
                    return new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
                });
        Set<ProjectEntity> childProjects = project.getChildProjects();
        childProjects.forEach(ProjectEntity::deleteParentProject);
        projectJpaRepository.saveAll(childProjects);

        project.delete();
        projectJpaRepository.save(project);
        LoggerFactory.db().logUpdate("ProjectEntity", String.valueOf(projectId), "프로젝트 소프트 delete를 true로 탈퇴 유저 처리가 완료되었습니다.");
    }

    /**
     * 지정한 ID의 논리적으로 삭제된 프로젝트를 복구합니다.
     *
     * @param projectId 복구할 프로젝트의 ID
     * @throws ProjectException 해당 ID의 프로젝트가 존재하지 않을 경우 발생합니다.
     */
    @Override
    public void restoreProject(Long projectId) {
        ProjectEntity project = projectJpaRepository.findIncludingDeleted(projectId)
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning("ProjectEntity", "해당 프로젝트가 존재하지 않습니다. projectId=" + projectId);
                    return new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
                });
        project.restore();
        projectJpaRepository.save(project);
        LoggerFactory.db().logUpdate("ProjectEntity", String.valueOf(projectId), "프로젝트 소프트 delete를 false로 탈퇴 유저 복구 처리가 완료되었습니다.");
    }
}
