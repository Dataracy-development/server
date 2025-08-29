package com.dataracy.modules.project.adapter.web.response.read;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "유저가 업로드한 프로젝트 웹 응답 DTO")
public record UserProjectWebResponse(
        @Schema(description = "프로젝트 아이디", example = "1")
        Long id,

        @Schema(description = "프로젝트 제목", example = "디자인에 관하여")
        String title,

        @Schema(description = "분석 내용", example = "디자인과 관련된 분석 내용은 ~~")
        String content,

        @Schema(description = "프로젝트 썸네일 url", example = "http://www.s3~~~~")
        String projectThumbnailUrl,

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

        @Schema(description = "생성일", example = "2025-08-04T10:30:00")
        LocalDateTime createdAt
) {}
