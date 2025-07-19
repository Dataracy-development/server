package com.dataracy.modules.project.adapter.persistence.mapper;

import com.dataracy.modules.project.adapter.persistence.entity.ProjectDataEntity;
import com.dataracy.modules.project.adapter.persistence.entity.ProjectEntity;
import com.dataracy.modules.project.domain.model.Project;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 프로젝트 엔티티와 프로젝트 도메인 모델을 변환하는 매퍼
 */
public final class ProjectEntityMapper {
    private ProjectEntityMapper() {
    }

    /**
     * ProjectEntity 객체를 Project 도메인 모델로 변환합니다.
     *
     * 입력이 null이면 null을 반환하며, 순환 참조를 방지하기 위해 부모 프로젝트가 있을 경우 부모의 ID와 제목만 포함한 Project 객체로 매핑합니다. 프로젝트에 연결된 데이터 ID 목록, 생성 시각, 댓글 수, 좋아요 수, 조회 수, 삭제 여부 등 주요 속성도 함께 변환됩니다.
     *
     * @param projectEntity 변환할 ProjectEntity 객체
     * @return 변환된 Project 도메인 모델 객체 또는 입력이 null일 경우 null
     */
    public static Project toDomain(ProjectEntity projectEntity) {
        if (projectEntity == null) {
            return null;
        }

        // 재귀 방지를 위해 아이디, 제목만 추출하여 저장
        Project parentProject = projectEntity.getParentProject() != null
                ? Project
                .builder()
                .id(projectEntity.getParentProject().getId())
                .title(projectEntity.getParentProject().getTitle())
                .build()
                : null;

        List<Long> dataIds = projectEntity.getProjectDataEntities().stream()
                .map(ProjectDataEntity::getDataId)
                .toList();

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
                projectEntity.getContent(),
                projectEntity.getFileUrl(),
                dataIds,
                projectEntity.getCreatedAt(),
                projectEntity.getCommentCount(),
                projectEntity.getLikeCount(),
                projectEntity.getViewCount(),
                projectEntity.getIsDeleted()
                );
    }

    /**
     * Project 도메인 모델을 ProjectEntity로 변환합니다.
     *
     * 부모 프로젝트가 있을 경우, 순환 참조를 방지하기 위해 부모의 ID와 제목만 포함한 엔티티로 매핑합니다.
     * 도메인 모델의 데이터 ID 목록을 ProjectDataEntity로 변환하여 ProjectEntity에 연결합니다.
     *
     * @param project 변환할 Project 도메인 모델
     * @return 변환된 ProjectEntity 인스턴스. 입력이 null이면 null을 반환합니다.
     */
    public static ProjectEntity toEntity(Project project) {
        if (project == null) {
            return null;
        }

        // 재귀 방지를 위해 아이디, 제목만 추출하여 저장
        ProjectEntity parentProject = project.getParentProject() != null
                ? ProjectEntity
                .builder()
                .id(project.getParentProject().getId())
                .title(project.getParentProject().getTitle())
                .build()
                : null;

        ProjectEntity projectEntity = ProjectEntity.toEntity(
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

        // dataIds → projectDataEntities 변환 후 연결
        List<ProjectDataEntity> projectDataEntities = Optional.ofNullable(project.getDataIds())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(dataId -> ProjectDataEntity.of(projectEntity, dataId))
                .toList();
        projectDataEntities.forEach(projectEntity::addProjectData);

        return projectEntity;
    }
}
