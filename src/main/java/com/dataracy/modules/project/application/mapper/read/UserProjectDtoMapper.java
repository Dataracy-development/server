package com.dataracy.modules.project.application.mapper.read;

import org.springframework.stereotype.Component;

import com.dataracy.modules.project.application.dto.response.read.UserProjectResponse;
import com.dataracy.modules.project.domain.model.Project;

/** 필터링된 프로젝트 도메인 DTO와 필터링된 프로젝트 도메인 모델을 변환하는 매퍼 */
@Component
public class UserProjectDtoMapper {
  /**
   * Project 도메인 객체와 주제/작성자 레벨 라벨을 결합해 UserProjectResponse DTO를 생성합니다.
   *
   * <p>상세: Project에서 id, title, content, thumbnailUrl, commentCount, likeCount, viewCount,
   * createdAt 값을 추출하고, 전달된 topicLabel 및 authorLevelLabel을 포함해 새로운 UserProjectResponse 인스턴스를 반환합니다.
   *
   * @param project 변환 대상 Project 도메인 객체(사용되는 필드: id, title, content, thumbnailUrl, commentCount,
   *     likeCount, viewCount, createdAt). null일 경우 NPE가 발생할 수 있습니다.
   * @param topicLabel 프로젝트의 주제 라벨
   * @param authorLevelLabel 작성자의 레벨 라벨
   * @return 프로젝트 정보와 주제/작성자 라벨을 포함한 UserProjectResponse 인스턴스
   */
  public UserProjectResponse toResponseDto(
      Project project, String topicLabel, String authorLevelLabel) {
    return new UserProjectResponse(
        project.getId(),
        project.getTitle(),
        project.getContent(),
        project.getThumbnailUrl(),
        topicLabel,
        authorLevelLabel,
        project.getCommentCount(),
        project.getLikeCount(),
        project.getViewCount(),
        project.getCreatedAt());
  }
}
