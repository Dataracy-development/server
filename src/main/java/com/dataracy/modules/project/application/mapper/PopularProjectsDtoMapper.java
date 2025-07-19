package com.dataracy.modules.project.application.mapper;

import com.dataracy.modules.project.application.dto.response.ProjectPopularSearchResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

/**
 * 인기있는 프로젝트 도메인 DTO와 인기있는 프로젝트 도메인 모델을 변환하는 매퍼
 */
@Component
public class PopularProjectsDtoMapper {
    public ProjectPopularSearchResponse toResponseDto(
            Project project,
            String username,
            String topicLabel,
            String analysisPurposeLabel,
            String dataSourceLabel,
            String authorLevelLabel
    ) {
        return new ProjectPopularSearchResponse(
                project.getId(),
                project.getTitle(),
                project.getContent(),
                username,
                project.getFileUrl(),
                topicLabel,
                analysisPurposeLabel,
                dataSourceLabel,
                authorLevelLabel,
                project.getCommentCount(),
                project.getLikeCount(),
                project.getViewCount()
        );
    }
}
