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
     * Project 도메인과 사용자명을 결합해 ParentProjectResponse DTO로 변환합니다.
     *
     * <p>다음 값을 사용해 ParentProjectResponse를 생성하여 반환합니다:
     * id, title, content, userId(프로젝트 소유자 ID), username, commentCount, likeCount, viewCount, createdAt.</p>
     *
     * <p>입력값에 대한 검증이나 널(null) 체크는 수행하지 않습니다.</p>
     *
     * @param project 변환할 Project 도메인 객체
     * @param username 프로젝트와 연관된 사용자 이름
     * @return 프로젝트 정보와 사용자명을 포함한 ParentProjectResponse DTO
     */
    public ParentProjectResponse toResponseDto(
            Project project,
            String username
    ) {
        return new ParentProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getContent(),
                project.getUserId(),
                username,
                project.getCommentCount(),
                project.getLikeCount(),
                project.getViewCount(),
                project.getCreatedAt()
        );
    }
}
