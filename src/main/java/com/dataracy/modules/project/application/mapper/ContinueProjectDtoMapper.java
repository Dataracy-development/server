package com.dataracy.modules.project.application.mapper;

import com.dataracy.modules.project.application.dto.response.ContinueProjectResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

/**
 * 필터링된 프로젝트 도메인 DTO와 필터링된 프로젝트 도메인 모델을 변환하는 매퍼
 */
@Component
public class ContinueProjectDtoMapper {
    public ContinueProjectResponse toResponseDto(
            Project project,
            String username,
            String userThumbnailUrl,
            String topicLabel,
            String authorLevelLabel
    ) {
        return new ContinueProjectResponse(
                project.getId(),
                project.getTitle(),
                username,
                userThumbnailUrl,
                project.getFileUrl(),
                topicLabel,
                authorLevelLabel,
                project.getCommentCount(),
                project.getLikeCount(),
                project.getViewCount(),
                project.getCreatedAt()
        );
    }
}
