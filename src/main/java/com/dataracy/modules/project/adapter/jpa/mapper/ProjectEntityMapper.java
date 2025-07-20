package com.dataracy.modules.project.adapter.jpa.mapper;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.domain.model.Project;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ProjectEntityMapper {
    /**
 * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private ProjectEntityMapper() {}

    /**
     * ProjectEntity를 최소 정보만 포함하는 Project 도메인 객체로 변환합니다.
     *
     * 부모, 자식, 데이터 정보는 포함하지 않습니다.
     *
     * @param entity 변환할 ProjectEntity 객체
     * @return 최소 정보만 포함된 Project 도메인 객체, 입력이 null이면 null 반환
     */
    public static Project toMinimal(ProjectEntity entity) {
        return toDomain(entity, false, false, false);
    }

    /**
     * ProjectEntity를 Project 도메인 객체로 변환하며, 부모 프로젝트와 데이터 정보만 포함합니다.
     *
     * @param entity 변환할 ProjectEntity 객체
     * @return 부모와 데이터가 포함된 Project 도메인 객체, 입력이 null이면 null 반환
     */
    public static Project toWithData(ProjectEntity entity) {
        return toDomain(entity, true, false, true);
    }

    /**
     * ProjectEntity를 Project 도메인 객체로 변환하며, 부모와 최대 2개의 자식 프로젝트 정보를 포함합니다.
     *
     * 데이터 정보는 포함하지 않습니다.
     *
     * @param entity 변환할 ProjectEntity 객체
     * @return 부모와 자식 정보가 포함된 Project 도메인 객체, 입력이 null이면 null 반환
     */
    public static Project toWithChildren(ProjectEntity entity) {
        return toDomain(entity, true, true, false);
    }

    /**
     * ProjectEntity를 Project 도메인 객체로 변환하며, 부모, 자식, 데이터 정보를 모두 포함합니다.
     *
     * @param entity 변환할 ProjectEntity 객체
     * @return 부모, 자식, 데이터가 모두 포함된 Project 도메인 객체. 입력이 null이면 null 반환.
     */
    public static Project toFull(ProjectEntity entity) {
        return toDomain(entity, true, true, true);
    }

    /**
     * ProjectEntity를 Project 도메인 객체로 변환합니다.
     *
     * @param entity 변환할 ProjectEntity 객체
     * @param includeParent 부모 프로젝트 정보를 포함할지 여부
     * @param includeChildren 자식 프로젝트 정보를 포함할지 여부 (최대 2개, 최소 정보만 포함)
     * @param includeData 프로젝트 데이터 ID 목록을 포함할지 여부
     * @return 변환된 Project 도메인 객체. 입력이 null이면 null을 반환합니다.
     */
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

    /**
     * 도메인 모델인 {@link Project} 객체를 JPA 엔티티인 {@link ProjectEntity}로 변환합니다.
     *
     * @param project 변환할 도메인 {@link Project} 객체
     * @return 변환된 {@link ProjectEntity} 객체, 입력이 null이면 null 반환
     */
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
