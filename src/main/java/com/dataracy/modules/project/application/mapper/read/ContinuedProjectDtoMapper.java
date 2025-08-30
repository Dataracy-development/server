package com.dataracy.modules.project.application.mapper.read;

import com.dataracy.modules.project.application.dto.response.read.ContinuedProjectResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

/**
 * 이어가기 프로젝트 도메인 DTO와 이어가기 프로젝트 도메인 모델을 변환하는 매퍼
 */
@Component
public class ContinuedProjectDtoMapper {
    /**
     * Project 도메인과 제공된 사용자·라벨 정보를 결합해 ContinuedProjectResponse DTO를 생성합니다.
     *
     * Project 객체의 id, title, userId, thumbnailUrl, commentCount, likeCount, viewCount, createdAt 값을 사용하고,
     * 추가로 전달된 username, userProfileUrl, topicLabel, authorLevelLabel을 응답 DTO에 포함합니다.
     *
     * @param project 변환할 Project 도메인 객체(사용되는 필드: id, title, userId, thumbnailUrl, commentCount, likeCount, viewCount, createdAt)
     * @param username 프로젝트 작성자의 닉네임
     * @param userProfileImageUrl 프로젝트 작성자 프로필 이미지 URL
     * @param topicLabel 프로젝트의 주제 라벨
     * @param authorLevelLabel 작성자의 레벨 라벨
     * @return 프로젝트 및 사용자·라벨 정보를 포함한 ContinuedProjectResponse 인스턴스
     */
    public ContinuedProjectResponse toResponseDto(
            Project project,
            String username,
            String userProfileImageUrl,
            String topicLabel,
            String authorLevelLabel
    ) {
        return new ContinuedProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getUserId(),
                username,
                userProfileImageUrl,
                project.getThumbnailUrl(),
                topicLabel,
                authorLevelLabel,
                project.getCommentCount(),
                project.getLikeCount(),
                project.getViewCount(),
                project.getCreatedAt()
        );
    }
}
