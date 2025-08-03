package com.dataracy.modules.project.application.mapper.read;

import com.dataracy.modules.project.application.dto.response.read.ContinuedProjectResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

/**
 * 필터링된 프로젝트 도메인 DTO와 필터링된 프로젝트 도메인 모델을 변환하는 매퍼
 */
@Component
public class ContinuedProjectDtoMapper {
    /**
     * Project 도메인 객체와 사용자 및 라벨 정보를 결합하여 ContinueProjectResponse DTO로 변환합니다.
     *
     * @param project 변환할 프로젝트 도메인 객체
     * @param username 프로젝트 작성자 또는 관련 사용자의 이름
     * @param userThumbnailUrl 사용자 썸네일 이미지 URL
     * @param topicLabel 프로젝트의 주제 라벨
     * @param authorLevelLabel 작성자의 레벨 라벨
     * @return 프로젝트 및 부가 정보를 포함하는 ContinueProjectResponse DTO
     */
    public ContinuedProjectResponse toResponseDto(
            Project project,
            String username,
            String userThumbnailUrl,
            String topicLabel,
            String authorLevelLabel
    ) {
        return new ContinuedProjectResponse(
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
