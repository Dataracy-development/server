package com.dataracy.modules.project.application.dto.response.search;

import java.time.LocalDateTime;
import java.util.List;

import com.dataracy.modules.project.application.dto.response.support.ChildProjectResponse;

/**
 * 프로젝트 필터링 애플리케이션 응답 DTO
 *
 * @param id 프로젝트 ID
 * @param title 프로젝트 제목
 * @param content 프로젝트 내용
 * @param creatorId 작성자 아이디
 * @param creatorName 작성자 닉네임
 * @param userProfileImageUrl 작성자 프로필 이미지 URL
 * @param projectThumbnailUrl 프로젝트 썸네일
 * @param topicLabel 토픽 라벨
 * @param analysisPurposeLabel 분석 목적 라벨
 * @param dataSourceLabel 데이터 출처 라벨
 * @param authorLevelLabel 작성자 유형 라벨
 * @param commentCount 댓글수
 * @param likeCount 좋아요수
 * @param viewCount 조회수
 * @param createdAt 작성 시기
 * @param childProjects 자식 프로젝트 목록
 */
public record FilteredProjectResponse(
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
    Long viewCount,
    LocalDateTime createdAt,
    List<ChildProjectResponse> childProjects) {}
