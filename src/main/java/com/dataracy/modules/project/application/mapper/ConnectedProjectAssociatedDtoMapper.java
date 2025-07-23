package com.dataracy.modules.project.application.mapper;

import com.dataracy.modules.project.application.dto.response.ConnectedProjectAssociatedWithDataResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

/**
 * 데이터와 연결된 프로젝트 도메인 DTO와 데이터와 연결된 프로젝트 도메인 모델을 변환하는 매퍼
 */
@Component
public class ConnectedProjectAssociatedDtoMapper {
    public ConnectedProjectAssociatedWithDataResponse toResponseDto(
            Project project,
            String username,
            String topicLabel
    ) {
        return new ConnectedProjectAssociatedWithDataResponse(
                project.getId(),
                project.getTitle(),
                username,
                topicLabel,
                project.getCommentCount(),
                project.getLikeCount(),
                project.getViewCount(),
                project.getCreatedAt()
        );
    }
}
