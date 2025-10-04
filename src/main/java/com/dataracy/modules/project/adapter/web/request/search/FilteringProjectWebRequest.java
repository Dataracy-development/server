package com.dataracy.modules.project.adapter.web.request.search;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@Schema(description = "프로젝트 필터링 웹 요청 DTO")
public record FilteringProjectWebRequest(
    @Schema(description = "검색 키워드 (제목 또는 작성자)", example = "LLM") String keyword,
    @Schema(description = "정렬 조건", example = "LATEST")
        @Pattern(
            regexp = "LATEST|OLDEST|MOST_LIKED|MOST_VIEWED|MOST_COMMENTED|LEAST_COMMENTED",
            message =
                "유효하지 않은 정렬 타입입니다. 프로젝트 정렬 방식은 LATEST, OLDEST, MOST_LIKED, MOST_VIEWED, MOST_COMMENTED, LEAST_COMMENTED 만 가능합니다.")
        String sortType,
    @Schema(description = "도메인 ID") @Min(1) Long topicId,
    @Schema(description = "분석 목적 ID") @Min(1) Long analysisPurposeId,
    @Schema(description = "데이터 출처 ID") @Min(1) Long dataSourceId,
    @Schema(description = "작성자 레벨 ID") @Min(1) Long authorLevelId) {}
