package com.dataracy.modules.comment.application.dto.response.read;

import java.time.LocalDateTime;

/**
 * 답글 조회 애플리케이션 응답 DTO
 *
 * @param id 답글 ID
 * @param username 작성자 닉네임
 * @param authorLevelLabel 작성자 등급 라벨
 * @param userProfileUrl 작성자 프로필 이미지 URL
 * @param content 댓글 내용
 * @param likeCount 좋아요 수
 * @param createdAt 생성 시각
 * @param isLiked 현재 사용자의 좋아요 여부
 */
public record FindReplyCommentResponse(
        Long id,
        String username,
        String authorLevelLabel,
        String userProfileUrl,
        String content,
        Long likeCount,
        LocalDateTime createdAt,
        boolean isLiked
) {}
