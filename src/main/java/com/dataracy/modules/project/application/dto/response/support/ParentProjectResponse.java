package com.dataracy.modules.project.application.dto.response.support;

import java.time.LocalDateTime;

/**
 * 부모 프로젝트 보조 응답 DTO.
 *
 * @param id 프로젝트 ID
 * @param title 프로젝트 제목
 * @param content 프로젝트 내용 요약
 * @param creatorId 작성자 아이디
 * @param creatorName 작성자 닉네임
 * @param userProfileImageUrl 작성자 프로필 이미지 URL
 * @param commentCount 댓글 수
 * @param likeCount 좋아요 수
 * @param viewCount 조회 수
 * @param createdAt 작성 시기
 */
public record ParentProjectResponse(
        Long id,
        String title,
        String content,
        Long creatorId,
        String creatorName,
        String userProfileImageUrl,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        LocalDateTime createdAt
) {}
