package com.dataracy.modules.comment.adapter.web.response.read;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "댓글에 대한 답글 웹 응답 DTO")
public record FindReplyCommentWebResponse(
    @Schema(description = "답글 아이디", example = "1") Long id,
    @Schema(description = "작성자 아이디", example = "1") Long creatorId,
    @Schema(description = "작성자 닉네임", example = "박준형") String creatorName,
    @Schema(description = "작성자 프로필 이미지", example = "https://www.s3.~~~") String userProfileImageUrl,
    @Schema(description = "작성자 유형 라벨", example = "실무자") String authorLevelLabel,
    @Schema(description = "댓글 내용", example = "해당 프로젝트에 대하여 댓글이 ~~~") String content,
    @Schema(description = "좋아요 수", example = "3") Long likeCount,
    @Schema(description = "생성일", example = "2025-08-04T10:30:00") LocalDateTime createdAt,
    @Schema(description = "해당 유저가 댓글에 대하여 좋아요 했는지 여부", example = "false") boolean isLiked) {}
