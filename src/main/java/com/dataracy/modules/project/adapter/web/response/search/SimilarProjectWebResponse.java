package com.dataracy.modules.project.adapter.web.response.search;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유사 프로젝트 웹 응답 DTO")
public record SimilarProjectWebResponse(
        @Schema(description = "프로젝트 아이디", example = "1")
        Long id,

        @Schema(description = "프로젝트 제목", example = "디자인에 관하여")
        String title,

        @Schema(description = "분석 내용", example = "디자인과 관련된 분석 내용은 ~~")
        String content,

        @Schema(description = "작성자 아이디", example = "1")
        Long creatorId,

        @Schema(description = "작성자 닉네임", example = "박준형")
        String creatorName,

        @Schema(description = "프로젝트 썸네일 url", example = "https://www.s3.~~~")
        String projectThumbnailUrl,

        @Schema(description = "토픽 라벨", example = "디자인")
        String topicLabel,

        @Schema(description = "분석 목적 라벨", example = "디자인 통계 데이터에 대한 분석을 위하여")
        String analysisPurposeLabel,

        @Schema(description = "데이터 출처 라벨", example = "한국데이터협회")
        String dataSourceLabel,

        @Schema(description = "작성자 유형 라벨", example = "실무자")
        String authorLevelLabel,

        @Schema(description = "댓글 수", example = "1")
        Long commentCount,

        @Schema(description = "좋아요 수", example = "1")
        Long likeCount,

        @Schema(description = "조회 수", example = "100")
        Long viewCount
) {}
