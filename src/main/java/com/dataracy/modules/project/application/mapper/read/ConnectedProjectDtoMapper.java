package com.dataracy.modules.project.application.mapper.read;

import com.dataracy.modules.project.application.dto.response.read.ConnectedProjectResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

/**
 * 데이터와 연결된 프로젝트 도메인 DTO와 데이터와 연결된 프로젝트 도메인 모델을 변환하는 매퍼
 */
@Component
public class ConnectedProjectDtoMapper {
    /**
     * Project 도메인 객체와 추가 정보를 결합하여 ConnectedProjectAssociatedWithDataResponse DTO로 변환합니다.
     *
     * @param project    변환할 Project 도메인 객체
     * @param username   프로젝트와 연관된 사용자 이름
     * @param topicLabel 프로젝트와 연관된 토픽 라벨
     * @return 프로젝트 정보와 사용자, 토픽 정보를 포함하는 ConnectedProjectAssociatedWithDataResponse DTO
     */
    public ConnectedProjectResponse toResponseDto(
            Project project,
            String username,
            String topicLabel
    ) {
        return new ConnectedProjectResponse(
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
