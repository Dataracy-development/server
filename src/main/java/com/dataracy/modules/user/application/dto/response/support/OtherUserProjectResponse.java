package com.dataracy.modules.user.application.dto.response.support;

import java.time.LocalDateTime;

/**
 * 인기있는 프로젝트 목록 조회 애플리케이션 응답 DTO.
 *
 * @param id 프로젝트 ID
 * @param title 프로젝트 제목
 * @param content 프로젝트 내용
 * @param projectThumbnailUrl 프로젝트 썸네일 URL
 * @param topicLabel 주제 라벨
 * @param authorLevelLabel 작성자 레벨 라벨
 * @param commentCount 댓글 수
 * @param likeCount 좋아요 수
 * @param viewCount 조회 수
 * @param createdAt 작성 시기
 */
public record OtherUserProjectResponse(
        Long id,
        String title,
        String content,
        String projectThumbnailUrl,
        String topicLabel,
        String authorLevelLabel,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        LocalDateTime createdAt
) {}
