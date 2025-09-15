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
     * Project 도메인과 추가 문자열 정보를 결합해 ConnectedProjectResponse DTO를 생성하여 반환합니다.
     *
     * <p>Project의 식별자, 제목, 사용자 ID, 댓글/좋아요/조회수, 생성일자와 함께 전달된 username 및 topicLabel을 응답 DTO에 포함합니다.</p>
     *
     * @param project 변환 대상 Project 도메인 객체
     * @param username 프로젝트와 연관된 사용자 이름
     * @param userProfileImageUrl 프로젝트와 연관된 사용자 프로필 이미지 URL
     * @param topicLabel 프로젝트와 연관된 토픽 라벨
     * @return 프로젝트 정보·사용자·토픽을 포함한 ConnectedProjectResponse 인스턴스
     */
    public ConnectedProjectResponse toResponseDto(
            Project project,
            String username,
            String userProfileImageUrl,
            String topicLabel
    ) {
        return new ConnectedProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getUserId(),
                username,
                userProfileImageUrl,
                topicLabel,
                project.getCommentCount(),
                project.getLikeCount(),
                project.getViewCount(),
                project.getCreatedAt()
        );
    }
}
