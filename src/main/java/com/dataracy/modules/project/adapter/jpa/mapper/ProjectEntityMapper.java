package com.dataracy.modules.project.adapter.jpa.mapper;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.domain.model.Project;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ProjectEntityMapper {
    private ProjectEntityMapper() {}

    /**
     * ProjectEntity를 자식 프로젝트와 데이터 정보를 제외한 최소 정보만 포함하는 Project 도메인 객체로 변환합니다.
     *
     * @param entity 변환할 ProjectEntity 객체
     * @return 최소 정보만 포함된 Project 도메인 객체, 입력이 null이면 null을 반환합니다.
     */
    public static Project toMinimal(ProjectEntity entity) {
        return toDomain(entity, false, false, false, 0);
    }

    /**
     * ProjectEntity를 Project 도메인 객체로 변환하며, 부모 프로젝트 정보를 포함합니다.
     *
     * @param entity 변환할 ProjectEntity 객체
     * @return 부모 프로젝트 아이디가 포함된 Project 도메인 객체, 입력이 null이면 null 반환
     */
    public static Project toWithParent(ProjectEntity entity) {
        return toDomain(entity, true, false, false, 0);
    }

    /**
     * ProjectEntity를 Project 도메인 객체로 변환하며, 지정한 개수만큼의 자식 프로젝트 정보를 포함합니다.
     *
     * 데이터 정보는 포함하지 않으며, 자식 프로젝트 정보만 최소한으로 포함됩니다.
     *
     * @param entity 변환할 ProjectEntity 객체
     * @param childrenCount 포함할 자식 프로젝트의 최대 개수
     * @return 자식 프로젝트 정보가 포함된 Project 도메인 객체, 입력이 null이면 null 반환
     */
    public static Project toWithChildren(ProjectEntity entity, int childrenCount) {
        return toDomain(entity, false, true, false, childrenCount);
    }

    /**
     * ProjectEntity를 Project 도메인 객체로 변환하며, 연관된 프로젝트 데이터 ID 목록만 포함합니다.
     * 하위 프로젝트 정보는 포함하지 않습니다.
     *
     * @param entity 변환할 ProjectEntity 객체
     * @return 데이터 ID 목록이 포함된 Project 도메인 객체, 입력이 null이면 null 반환
     */
    public static Project toWithData(ProjectEntity entity) {
        return toDomain(entity, false, false, true, 0);
    }

    /**
     * ProjectEntity를 Project 도메인 객체로 변환합니다.
     *
     * @param entity 변환할 ProjectEntity 객체
     * @param includeParent 부모 프로젝트 포함할지 여부
     * @param includeChildren 자식 프로젝트를 최대 childrenCount개까지 최소 정보로 포함할지 여부
     * @param includeData 프로젝트 데이터 ID 목록을 포함할지 여부
     * @param childrenCount 포함할 자식 프로젝트의 최대 개수
     * @return 변환된 Project 도메인 객체. 입력이 null이면 null을 반환합니다.
     *
     * <p>
     * 자식 프로젝트는 최소 정보만 포함되며, 최대 childrenCount개까지 반환됩니다.<br>
     * 프로젝트 데이터 ID 목록은 includeData가 true일 때만 포함됩니다.<br>
     * 부모 프로젝트 정보는 포함되지 않습니다.
     * </p>
     */
    private static Project toDomain(ProjectEntity entity, boolean includeParent, boolean includeChildren, boolean includeData, int childrenCount) {
        if (entity == null) return null;

        Long parentProjectId = includeParent
                ? Optional.ofNullable(entity.getParentProject())
                .map(ProjectEntity::getId)
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
                .limit(childrenCount)
                .map(ProjectEntityMapper::toMinimal)
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
                parentProjectId,
                entity.getContent(),
                entity.getThumbnailUrl(),
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
     * 도메인 객체인 {@link Project}를 JPA 엔티티 {@link ProjectEntity}로 변환합니다.
     *
     * 프로젝트에 부모 프로젝트 ID가 있으면 해당 ID로 부모 엔티티를 생성하여 연관시킵니다.
     * 프로젝트에 연결된 데이터 ID 목록이 있으면 각각 {@link ProjectDataEntity}로 매핑하여 엔티티에 추가합니다.
     *
     * @param project 변환할 {@link Project} 도메인 객체
     * @return 변환된 {@link ProjectEntity} 객체, 입력이 null이면 null을 반환합니다.
     */
    public static ProjectEntity toEntity(Project project) {
        if (project == null) return null;

        ProjectEntity parentProject = Optional.ofNullable(project.getParentProjectId())
                .map(parentProjectId -> ProjectEntity.builder()
                        .id(parentProjectId)
                        .build()
                )
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
                project.getThumbnailUrl()
        );

        Optional.ofNullable(project.getDataIds())
                .orElse(Collections.emptyList())
                .stream()
                .map(dataId -> ProjectDataEntity.of(projectEntity, dataId))
                .forEach(projectEntity::addProjectData);

        return projectEntity;
    }
}
