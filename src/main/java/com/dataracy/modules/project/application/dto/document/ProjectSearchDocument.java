package com.dataracy.modules.project.application.dto.document;

import java.time.LocalDateTime;

import com.dataracy.modules.project.domain.model.Project;

import lombok.Builder;

@Builder
public record ProjectSearchDocument(
    Long id,
    String title,
    String content,
    Long topicId,
    String topicLabel,
    Long userId,
    String username,
    String userProfileImageUrl,
    Long analysisPurposeId,
    String analysisPurposeLabel,
    Long dataSourceId,
    String dataSourceLabel,
    Long authorLevelId,
    String authorLevelLabel,
    Boolean isContinue,
    String projectThumbnailUrl,
    LocalDateTime createdAt,
    Long commentCount,
    Long likeCount,
    Long viewCount,
    Boolean isDeleted) {
  /**
   * Project 도메인 객체와 관련 라벨 및 사용자 정보를 사용해 Elasticsearch 색인용 ProjectSearchDocument를 생성합니다.
   *
   * <p>반환되는 문서는 프로젝트의 핵심 필드(id, title, content 등), 각종
   * 라벨(topic/analysisPurpose/dataSource/authorLevel), 사용자 정보(username 및 프로필 이미지 URL),
   * 메트릭(comment/like/view), 삭제 여부, 썸네일 URL, 생성일 등을 포함합니다.
   *
   * @param project 변환 대상 Project 도메인 객체
   * @param topicLabel 프로젝트 주제 라벨
   * @param analysisPurposeLabel 분석 목적 라벨
   * @param dataSourceLabel 데이터 소스 라벨
   * @param authorLevelLabel 작성자 등급 라벨
   * @param username 연관된 사용자 이름
   * @param userProfileImageUrl 연관된 사용자의 프로필 이미지 URL
   * @return 색인에 사용될 값들로 채워진 ProjectSearchDocument 인스턴스
   */
  public static ProjectSearchDocument from(
      Project project,
      String topicLabel,
      String analysisPurposeLabel,
      String dataSourceLabel,
      String authorLevelLabel,
      String username,
      String userProfileImageUrl) {
    return ProjectSearchDocument.builder()
        .id(project.getId())
        .title(project.getTitle())
        .content(project.getContent())
        .topicId(project.getTopicId())
        .topicLabel(topicLabel)
        .userId(project.getUserId())
        .username(username)
        .userProfileImageUrl(userProfileImageUrl)
        .analysisPurposeId(project.getAnalysisPurposeId())
        .analysisPurposeLabel(analysisPurposeLabel)
        .dataSourceId(project.getDataSourceId())
        .dataSourceLabel(dataSourceLabel)
        .authorLevelId(project.getAuthorLevelId())
        .authorLevelLabel(authorLevelLabel)
        .isContinue(project.getIsContinue())
        .projectThumbnailUrl(project.getThumbnailUrl())
        .createdAt(project.getCreatedAt())
        .commentCount(project.getCommentCount())
        .likeCount(project.getLikeCount())
        .viewCount(project.getViewCount())
        .isDeleted(project.getIsDeleted())
        .build();
  }
}
