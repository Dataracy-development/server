/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.web.response.read;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "데이터셋 최소 정보 웹 응답 DTO")
public record RecentMinimalDataWebResponse(
    @Schema(description = "데이터셋 아이디", example = "1") Long id,
    @Schema(description = "데이터셋 제목", example = "최신 3년 이내 개발자 현황") String title,
    @Schema(description = "작성자 아이디", example = "1") Long creatorId,
    @Schema(description = "작성자 닉네임", example = "박준형") String creatorName,
    @Schema(description = "작성자 프로필 이미지 URL", example = "https://www.s3.~~~")
        String userProfileImageUrl,
    @Schema(description = "데이터셋 썸네일 url", example = "https://www.s3.~~~") String dataThumbnailUrl,
    @Schema(description = "생성일", example = "2025-08-04T10:30:00") LocalDateTime createdAt) {}
