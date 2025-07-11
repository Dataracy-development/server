package com.dataracy.modules.project.adapter.persistence.mapper;

import com.dataracy.modules.project.adapter.persistence.entity.ProjectEntity;
import com.dataracy.modules.project.domain.model.Project;

/**
 * 프로젝트 엔티티와 프로젝트 도메인 모델을 변환하는 매퍼
 */
public class ProjectEntityMapper {
    // 프로젝트 엔티티 -> 프로젝트 도메인 모델
    public static Project toDomain(ProjectEntity projectEntity) {
        return Project.toDomain(
                projectEntity.getId(),
                projectEntity.getTitle(),
                projectEntity.getTopicId(),
                projectEntity.getUserId(),
                projectEntity.getAnalysisPurposeId(),
                projectEntity.getDataSourceId(),
                projectEntity.getAuthorLevelId(),
                projectEntity.getIsContinue(),
                ProjectEntityMapper.toDomain(projectEntity.getParentProject()),
                projectEntity.getContent()
                );
    }

    // 프로젝트 도메인 모델 -> 프로젝트 엔티티
    public static ProjectEntity toEntity(Project project) {
        return ProjectEntity.toEntity(
                project.getId(),
                project.getTitle(),
                project.getTopicId(),
                project.getUserId(),
                project.getAnalysisPurposeId(),
                project.getDataSourceId(),
                project.getAuthorLevelId(),
                project.getIsContinue(),
                ProjectEntityMapper.toEntity(project.getParentProject()),
                project.getContent()
        );
    }
}
