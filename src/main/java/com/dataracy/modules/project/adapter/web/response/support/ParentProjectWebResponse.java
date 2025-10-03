/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.web.response.support;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "부모 프로젝트 웹 응답 DTO")
public record ParentProjectWebResponse(
    @Schema(description = "프로젝트 아이디", example = "1") Long id,
    @Schema(description = "프로젝트 제목", example = "디자인에 관하여") String title,
    @Schema(description = "분석 내용", example = "디자인과 관련된 분석 내용은 ~~") String content,
    @Schema(description = "작성자 아이디", example = "1") Long creatorId,
    @Schema(description = "작성자 닉네임", example = "박준형") String creatorName,
    @Schema(description = "작성자 프로필 이미지 URL", example = "https://www.s3.~~~")
        String userProfileImageUrl,
    @Schema(description = "댓글 수", example = "1") Long commentCount,
    @Schema(description = "좋아요 수", example = "1") Long likeCount,
    @Schema(description = "조회 수", example = "1") Long viewCount,
    @Schema(description = "생성 시기") LocalDateTime createdAt) {}
