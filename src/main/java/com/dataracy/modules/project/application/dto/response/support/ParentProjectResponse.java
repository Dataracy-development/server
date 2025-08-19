package com.dataracy.modules.project.application.dto.response.support;

import java.time.LocalDateTime;

/**
 * 부모 프로젝트 보조 응답 DTO.
 *
 * @param id 프로젝트 ID
 * @param title 프로젝트 제목
 * @param content 프로젝트 내용 요약
 * @param username 작성자 닉네임
 * @param commentCount 댓글 수
 * @param likeCount 좋아요 수
 * @param viewCount 조회 수
 * @param createdAt 작성 시기
 */
public record ParentProjectResponse(
        Long id,
        String title,
        String content,
        String username,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        LocalDateTime createdAt
) {}
