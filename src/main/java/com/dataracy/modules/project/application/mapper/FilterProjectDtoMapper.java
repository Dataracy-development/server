package com.dataracy.modules.project.application.mapper;

import com.dataracy.modules.project.application.dto.response.ProjectFilterResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 필터링된 프로젝트 도메인 DTO와 필터링된 프로젝트 도메인 모델을 변환하는 매퍼
 */
@Component
public class FilterProjectDtoMapper {
    public ProjectFilterResponse toResponseDto(
            Project project,
            String username,
            String topicLabel,
            String analysisPurposeLabel,
            String dataSourceLabel,
            String authorLevelLabel
    ) {
        return new ProjectFilterResponse(
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
                project.getViewCount(),
                project.getCreatedAt(),
                null
        );
    }
}
