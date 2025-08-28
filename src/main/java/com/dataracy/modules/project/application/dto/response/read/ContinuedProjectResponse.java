package com.dataracy.modules.project.application.dto.response.read;

import java.time.LocalDateTime;

/**
 * 해당 프로젝트의 이어가기 프로젝트 목록 조회 애플리케이션 응답 DTO.
 *
 * @param id 프로젝트 ID
 * @param title 프로젝트 제목
 * @param creatorId 작성자 아이디
 * @param creatorName 작성자 닉네임
 * @param creatorProfileUrl 작성자 프로필 이미지 URL
 * @param projectThumbnailUrl 프로젝트 썸네일 URL
 * @param topicLabel 주제 라벨
 * @param authorLevelLabel 작성자 레벨 라벨
 * @param commentCount 댓글 수
 * @param likeCount 좋아요 수
 * @param viewCount 조회 수
 * @param createdAt 작성 시기
 */
public record ContinuedProjectResponse(
        Long id,
        String title,
        Long creatorId,
        String creatorName,
        String userProfileUrl,
        String projectThumbnailUrl,
        String topicLabel,
        String authorLevelLabel,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        LocalDateTime createdAt
) {}
