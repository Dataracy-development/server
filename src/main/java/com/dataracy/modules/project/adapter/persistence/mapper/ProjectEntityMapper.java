package com.dataracy.modules.project.adapter.persistence.mapper;

import com.dataracy.modules.project.adapter.persistence.entity.ProjectEntity;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

/**
 * 프로젝트 엔티티와 프로젝트 도메인 모델을 변환하는 매퍼
 */
@Component
public class ProjectEntityMapper {
    // 프로젝트 엔티티 -> 프로젝트 도메인 모델
    public Project toDomain(ProjectEntity projectEntity) {
        // 재귀 방지를 위해 아이디, 제목만 추출하여 저장
        Project parentProject = projectEntity.getParentProject() != null
                ? Project
                .builder()
                .id(projectEntity.getParentProject().getId())
                .title(projectEntity.getParentProject().getTitle())
                .build()
                : null;

        return Project.toDomain(
                projectEntity.getId(),
                projectEntity.getTitle(),
                projectEntity.getTopicId(),
                projectEntity.getUserId(),
                projectEntity.getAnalysisPurposeId(),
                projectEntity.getDataSourceId(),
                projectEntity.getAuthorLevelId(),
                projectEntity.getIsContinue(),
                parentProject,
                projectEntity.getContent()
                );
    }

    // 프로젝트 도메인 모델 -> 프로젝트 엔티티
    public ProjectEntity toEntity(Project project) {
        // 재귀 방지를 위해 아이디, 제목만 추출하여 저장
        ProjectEntity parentProject = project.getParentProject() != null
                ? ProjectEntity
                .builder()
                .id(project.getParentProject().getId())
                .title(project.getParentProject().getTitle())
                .build()
                : null;

        return ProjectEntity.toEntity(
                project.getId(),
                project.getTitle(),
                project.getTopicId(),
                project.getUserId(),
                project.getAnalysisPurposeId(),
                project.getDataSourceId(),
                project.getAuthorLevelId(),
                project.getIsContinue(),
                parentProject,
                project.getContent()
        );
    }
}
