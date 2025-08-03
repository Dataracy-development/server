package com.dataracy.modules.project.adapter.web.response.read;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "프로젝트와 연결된 자식 프로젝트 웹 응답 DTO")
public record ContinuedProjectWebResponse(
        @Schema(description = "프로젝트 아이디", example = "1")
        Long id,

        @Schema(description = "프로젝트 제목", example = "디자인에 관하여")
        String title,

        @Schema(description = "작성자명", example = "박준형")
        String username,

        @Schema(description = "작성자 프로필 썸네일 url", example = "http://www.s3~~~~")
        String userThumbnailUrl,

        @Schema(description = "프로젝트 썸네일 url", example = "http://www.s3~~~~")
        String fileUrl,

        @Schema(description = "토픽 라벨", example = "디자인")
        String topicLabel,

        @Schema(description = "작성자 유형 라벨", example = "실무자")
        String authorLevelLabel,

        @Schema(description = "댓글 수", example = "1")
        Long commentCount,

        @Schema(description = "좋아요 수", example = "1")
        Long likeCount,

        @Schema(description = "조회 수", example = "100")
        Long viewCount,

        @Schema(description = "작성 날짜", example = "2025-08-02")
        LocalDateTime createdAt
) {}
