package com.dataracy.modules.project.application.mapper.read;

import com.dataracy.modules.project.application.dto.response.read.ProjectDetailResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectDetailDtoMapper {
    public ProjectDetailResponse toResponseDto(
            Project project,
            String username,
            String authorLevelLabel,
            String occupationLabel,
            String topicLabel,
            String analysisPurposeLabel,
            String dataSourceLabel,
            boolean isLiked,
            boolean hasChild,
            boolean hasData
    ) {
        return new ProjectDetailResponse(
                project.getId(),
                project.getTitle(),
                username,
                authorLevelLabel,
                occupationLabel,
                topicLabel,
                analysisPurposeLabel,
                dataSourceLabel,
                project.getIsContinue(),
                project.getParentProjectId(),
                project.getContent(),
                project.getFileUrl(),
                project.getCreatedAt(),
                project.getCommentCount(),
                project.getLikeCount(),
                project.getViewCount(),
                isLiked,
                hasChild,
                hasData
        );
    }
}
