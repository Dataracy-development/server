package com.dataracy.modules.project.adapter.persistence.mapper;

import com.dataracy.modules.project.adapter.persistence.entity.ProjectEntity;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

/**
 * 프로젝트 엔티티와 프로젝트 도메인 모델을 변환하는 매퍼
 */
@Component
public class ProjectEntityMapper {
    /**
     * ProjectEntity 객체를 Project 도메인 모델로 변환합니다.
     *
     * 입력이 null이면 null을 반환합니다. 부모 프로젝트가 존재할 경우, 순환 참조를 방지하기 위해 부모의 ID와 제목만 포함한 Project 객체로 매핑합니다. 프로젝트의 콘텐츠와 썸네일 URL 정보도 함께 변환됩니다.
     *
     * @param projectEntity 변환할 ProjectEntity 객체
     * @return 변환된 Project 도메인 모델 객체 또는 입력이 null일 경우 null
     */
    public Project toDomain(ProjectEntity projectEntity) {
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
                projectEntity.getThumbnailUrl()
                );
    }

    /**
     * Project 도메인 모델을 ProjectEntity로 변환합니다.
     *
     * 부모 프로젝트가 존재할 경우, 순환 참조를 방지하기 위해 부모의 ID와 제목만 포함한 엔티티로 매핑합니다.
     *
     * @param project 변환할 Project 도메인 모델
     * @return 변환된 ProjectEntity 인스턴스. 입력이 null이면 null을 반환합니다.
     */
    public ProjectEntity toEntity(Project project) {
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
                project.getContent(),
                project.getThumbnailUrl()
        );
    }
}
