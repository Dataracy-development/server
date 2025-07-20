package com.dataracy.modules.project.adapter.jpa.mapper;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.domain.model.Project;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ProjectEntityMapper {
    private ProjectEntityMapper() {}

    // 1단계: 아무 관계도 포함하지 않음 (parent, children, data 모두 제외)
    public static Project toMinimal(ProjectEntity entity) {
        return toDomain(entity, false, false, false);
    }

    // 2단계: 부모 + 데이터만 포함 (기본 정보) – 기존 toSummary
    public static Project toWithData(ProjectEntity entity) {
        return toDomain(entity, true, false, true);
    }

    // 3단계: 부모 + 자식 포함 (데이터 제외)
    public static Project toWithChildren(ProjectEntity entity) {
        return toDomain(entity, true, true, false);
    }

    // 4단계: 부모 + 자식 + 데이터 모두 포함 (전체 정보)
    public static Project toFull(ProjectEntity entity) {
        return toDomain(entity, true, true, true);
    }

    // 엔티티 -> 도메인 공통 내부 변환 메서드
    private static Project toDomain(ProjectEntity entity, boolean includeParent, boolean includeChildren, boolean includeData) {
        if (entity == null) return null;

        Project parent = includeParent
                ? Optional.ofNullable(entity.getParentProject())
                .map(p -> Project.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .build())
                .orElse(null)
                : null;

        List<Long> dataIds = includeData
                ? Optional.ofNullable(entity.getProjectDataEntities())
                .orElse(Collections.emptySet())
                .stream()
                .map(ProjectDataEntity::getDataId)
                .toList()
                : List.of();

        List<Project> childProjects = includeChildren
                ? Optional.ofNullable(entity.getChildProjects())
                .orElse(Collections.emptySet())
                .stream()
                .limit(2)
                .map(ProjectEntityMapper::toMinimal) // 재귀 방지
                .toList()
                : List.of();

        return Project.of(
                entity.getId(),
                entity.getTitle(),
                entity.getTopicId(),
                entity.getUserId(),
                entity.getAnalysisPurposeId(),
                entity.getDataSourceId(),
                entity.getAuthorLevelId(),
                entity.getIsContinue(),
                parent,
                entity.getContent(),
                entity.getFileUrl(),
                dataIds,
                entity.getCreatedAt(),
                entity.getCommentCount(),
                entity.getLikeCount(),
                entity.getViewCount(),
                entity.getIsDeleted(),
                childProjects
        );
    }

    // 도메인 → 엔티티 변환
    public static ProjectEntity toEntity(Project project) {
        if (project == null) return null;

        ProjectEntity parentProject = Optional.ofNullable(project.getParentProject())
                .map(parent -> ProjectEntity.builder()
                        .id(parent.getId())
                        .title(parent.getTitle())
                        .build())
                .orElse(null);

        ProjectEntity projectEntity = ProjectEntity.of(
                project.getTitle(),
                project.getTopicId(),
                project.getUserId(),
                project.getAnalysisPurposeId(),
                project.getDataSourceId(),
                project.getAuthorLevelId(),
                project.getIsContinue(),
                parentProject,
                project.getContent(),
                project.getFileUrl()
        );

        Optional.ofNullable(project.getDataIds())
                .orElse(Collections.emptyList())
                .stream()
                .map(dataId -> ProjectDataEntity.of(projectEntity, dataId))
                .forEach(projectEntity::addProjectData);

        return projectEntity;
    }
}
