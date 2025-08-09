package com.dataracy.modules.dataset.adapter.web.response.read;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "데이터셋 최소 정보 웹 응답 DTO")
public record RecentMinimalDataWebResponse(
        @Schema(description = "데이터셋 아이디", example = "1")
        Long id,

        @Schema(description = "데이터셋 제목", example = "최신 3년 이내 개발자 현황")
        String title,

        @Schema(description = "데이터셋 썸네일 url", example = "https://www.s3.~~~")
        String dataThumbnailUrl,

        @Schema(description = "생성일", example = "2025-08-04T10:30:00")
        LocalDateTime createdAt
) {}
