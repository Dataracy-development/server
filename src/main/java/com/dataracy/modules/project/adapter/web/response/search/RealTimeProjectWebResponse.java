package com.dataracy.modules.project.adapter.web.response.search;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "자동완성을 위한 실시간 프로젝트 조회 웹 응답 DTO")
public record RealTimeProjectWebResponse(
        @Schema(description = "프로젝트 아이디", example = "1")
        Long id,

        @Schema(description = "프로젝트 제목", example = "디자인에 관하여")
        String title,

        @Schema(description = "작성자 아이디", example = "1")
        Long creatorId,

        @Schema(description = "작성자 닉네임", example = "박준형")
        String creatorName,

        @Schema(description = "프로젝트 썸네일 url", example = "https://www.s3.~~~")
        String projectThumbnailUrl
) {}
