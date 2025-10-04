package com.dataracy.modules.project.application.mapper.support;

import org.springframework.stereotype.Component;

import com.dataracy.modules.project.application.dto.response.support.ParentProjectResponse;
import com.dataracy.modules.project.domain.model.Project;

/** 경량 프로젝트 도메인 DTO와 데이터와 연결된 프로젝트 도메인 모델을 변환하는 매퍼 */
@Component
public class ParentProjectDtoMapper {
  /**
   * Project 도메인과 사용자 정보를 결합해 ParentProjectResponse DTO를 생성하여 반환합니다.
   *
   * <p>Project의 id, title, content, userId, commentCount, likeCount, viewCount, createdAt 필드와 전달된
   * username 및 userProfileImageUrl을 사용해 ParentProjectResponse 인스턴스를 구성합니다. 입력값에 대한 검증이나 널(null) 체크는
   * 수행하지 않습니다.
   *
   * @param project 변환할 Project 도메인 객체
   * @param username 프로젝트와 연관된 사용자 이름
   * @param userProfileImageUrl 사용자 프로필 이미지 URL
   * @return 프로젝트 정보와 사용자명(및 프로필 이미지 URL)을 포함한 ParentProjectResponse DTO
   */
  public ParentProjectResponse toResponseDto(
      Project project, String username, String userProfileImageUrl) {
    return new ParentProjectResponse(
        project.getId(),
        project.getTitle(),
        project.getContent(),
        project.getUserId(),
        username,
        userProfileImageUrl,
        project.getCommentCount(),
        project.getLikeCount(),
        project.getViewCount(),
        project.getCreatedAt());
  }
}
