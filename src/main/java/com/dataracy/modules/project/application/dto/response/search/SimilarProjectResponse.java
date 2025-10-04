package com.dataracy.modules.project.application.dto.response.search;

/**
 * 유사 프로젝트 검색 애플리케이션 응답 DTO
 *
 * @param id 프로젝트 ID
 * @param title 프로젝트 제목
 * @param content 프로젝트 내용 요약
 * @param creatorId 작성자 아이디
 * @param creatorName 작성자 닉네임
 * @param userProfileImageUrl 작성자 프로필 이미지 URL
 * @param projectThumbnailUrl 프로젝트 썸네일 URL
 * @param topicLabel 주제 라벨
 * @param analysisPurposeLabel 분석 목적 라벨
 * @param dataSourceLabel 데이터 소스 라벨
 * @param authorLevelLabel 작성자 레벨 라벨
 * @param commentCount 댓글 수
 * @param likeCount 좋아요 수
 * @param viewCount 조회 수
 */
public record SimilarProjectResponse(
    Long id,
    String title,
    String content,
    Long creatorId,
    String creatorName,
    String userProfileImageUrl,
    String projectThumbnailUrl,
    String topicLabel,
    String analysisPurposeLabel,
    String dataSourceLabel,
    String authorLevelLabel,
    Long commentCount,
    Long likeCount,
    Long viewCount) {}
