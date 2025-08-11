package com.dataracy.modules.project.application.dto.response.search;

/**
 * 유사 프로젝트 검색 응답 DTO.
 *
 * @param id 프로젝트 ID
 * @param title 프로젝트 제목
 * @param content 프로젝트 내용 요약
 * @param username 작성자 닉네임
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
        String username,
        String projectThumbnailUrl,
        String topicLabel,
        String analysisPurposeLabel,
        String dataSourceLabel,
        String authorLevelLabel,
        Long commentCount,
        Long likeCount,
        Long viewCount
) {}
