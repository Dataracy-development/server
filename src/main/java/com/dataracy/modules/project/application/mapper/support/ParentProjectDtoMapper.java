package com.dataracy.modules.project.application.mapper.support;

import com.dataracy.modules.project.application.dto.response.support.ParentProjectResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

/**
 * 경량 프로젝트 도메인 DTO와 데이터와 연결된 프로젝트 도메인 모델을 변환하는 매퍼
 */
@Component
public class ParentProjectDtoMapper {
    /**
     * Project 도메인 객체와 추가 정보를 결합하여 ParentProjectResponse DTO로 변환합니다.
     *
     * @param project 변환할 Project 도메인 객체
     * @param username 프로젝트와 연관된 사용자 이름
     * @return 프로젝트 정보와 사용자 정보를 포함하는 ParentProjectResponse DTO
     */
    public ParentProjectResponse toResponseDto(
            Project project,
            String username
    ) {
        return new ParentProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getContent(),
                username,
                project.getCommentCount(),
                project.getLikeCount(),
                project.getViewCount(),
                project.getCreatedAt()
        );
    }
}
