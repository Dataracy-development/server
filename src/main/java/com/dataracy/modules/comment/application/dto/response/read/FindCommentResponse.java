package com.dataracy.modules.comment.application.dto.response.read;

import java.time.LocalDateTime;

/**
 * 댓글 조회 애플리케이션 응답 DTO
 *
 * @param id 댓글 아이디
 * @param creatorId 작성자 아이디
 * @param creatorName 작성자 닉네임
 * @param userProfileImageUrl 작성자 프로필 이미지 URL
 * @param authorLevelLabel 작성자 등급 라벨
 * @param content 댓글 내용
 * @param likeCount 좋아요 수
 * @param childCommentCount 댓글에 대한 답글 수
 * @param createdAt 생성 시각
 * @param isLiked 현재 사용자의 좋아요 여부
 */
public record FindCommentResponse(
    Long id,
    Long creatorId,
    String creatorName,
    String userProfileImageUrl,
    String authorLevelLabel,
    String content,
    Long likeCount,
    Long childCommentCount,
    LocalDateTime createdAt,
    boolean isLiked) {}
