package com.dataracy.modules.project.adapter.persistence.mapper;

import com.dataracy.modules.project.adapter.persistence.entity.ProjectEntity;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.user.adapter.persistence.mapper.reference.AuthorLevelEntityMapper;

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
                AnalysisPurposeEntityMapper.toDomain(projectEntity.getAnalysisPurpose()),
                DataSourceEntityMapper.toDomain(projectEntity.getDataSource()),
                AuthorLevelEntityMapper.toDomain(projectEntity.getAuthorLevel()),
                projectEntity.getIsNew(),
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
                AnalysisPurposeEntityMapper.toEntity(project.getAnalysisPurpose()),
                DataSourceEntityMapper.toEntity(project.getDataSource()),
                AuthorLevelEntityMapper.toEntity(project.getAuthorLevel()),
                project.getIsNew(),
                ProjectEntityMapper.toEntity(project.getParentProject()),
                project.getContent()
        );
    }
}
