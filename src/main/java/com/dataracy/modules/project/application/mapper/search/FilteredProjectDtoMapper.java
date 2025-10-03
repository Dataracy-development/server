/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.mapper.search;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dataracy.modules.project.application.dto.response.search.FilteredProjectResponse;
import com.dataracy.modules.project.application.dto.response.support.ChildProjectResponse;
import com.dataracy.modules.project.domain.model.Project;

/** 필터링된 프로젝트 도메인 DTO와 필터링된 프로젝트 도메인 모델을 변환하는 매퍼 */
@Component
public class FilteredProjectDtoMapper {

  @Value("${default.profile.image-url:}")
  private String defaultUserProfileImageUrl;

  /**
   * Project 도메인 객체와 관련 메타데이터를 FilteredProjectResponse DTO로 변환합니다.
   *
   * <p>자식 프로젝트 목록은 ChildProjectResponse로 매핑되며, 자식 작성자의 이름이 제공되지 않으면 "익명 유저"로 대체하고, 자식의 프로필 이미지 URL이
   * 제공되지 않으면 컴포넌트에 주입된 기본 프로필 이미지 URL을 사용합니다.
   *
   * @param project 변환할 프로젝트 도메인 객체
   * @param username 프로젝트 작성자의 표시 이름(응답에 포함됨)
   * @param userProfileImageUrl 프로젝트 작성자의 프로필 이미지 URL(응답에 포함됨)
   * @param topicLabel 프로젝트의 주제 라벨(응답에 포함됨)
   * @param analysisPurposeLabel 분석 목적 라벨(응답에 포함됨)
   * @param dataSourceLabel 데이터 소스 라벨(응답에 포함됨)
   * @param authorLevelLabel 작성자 등급 라벨(응답에 포함됨)
   * @param childUsernames 자식 프로젝트의 userId -> username 매핑(없을 경우 "익명 유저" 사용)
   * @param childUserProfileImageUrls 자식 프로젝트의 userId -> profileImageUrl 매핑(없을 경우 컴포넌트의 기본 이미지 사용)
   * @return 변환된 FilteredProjectResponse DTO
   *     <p>참고: 9개의 파라미터를 가지지만, Mapper 레이어에서 프로젝트와 하위 프로젝트 정보를 조합하여 필터링된 검색 DTO를 생성하므로 허용됩니다.
   */
  @SuppressWarnings("java:S107") // Mapper 메서드로 여러 정보 조합 필요
  public FilteredProjectResponse toResponseDto(
      Project project,
      String username,
      String userProfileImageUrl,
      String topicLabel,
      String analysisPurposeLabel,
      String dataSourceLabel,
      String authorLevelLabel,
      Map<Long, String> childUsernames,
      Map<Long, String> childUserProfileImageUrls) {
    List<ChildProjectResponse> childProjects =
        project.getChildProjects().stream()
            .map(
                child ->
                    new ChildProjectResponse(
                        child.getId(),
                        child.getTitle(),
                        child.getContent(),
                        child.getUserId(),
                        childUsernames.getOrDefault(child.getUserId(), "익명 유저"),
                        childUserProfileImageUrls.getOrDefault(
                            child.getUserId(), defaultUserProfileImageUrl),
                        child.getCommentCount(),
                        child.getLikeCount()))
            .toList();

    return new FilteredProjectResponse(
        project.getId(),
        project.getTitle(),
        project.getContent(),
        project.getUserId(),
        username,
        userProfileImageUrl,
        project.getThumbnailUrl(),
        topicLabel,
        analysisPurposeLabel,
        dataSourceLabel,
        authorLevelLabel,
        project.getCommentCount(),
        project.getLikeCount(),
        project.getViewCount(),
        project.getCreatedAt(),
        childProjects);
  }
}
