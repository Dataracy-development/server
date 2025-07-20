package com.dataracy.modules.project.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record ProjectFilterWebRequest(
        @Schema(description = "검색 키워드 (제목 또는 작성자)", example = "LLM")
        String keyword,

        @Pattern(regexp = "LATEST|OLDEST|MOST_LIKED|MOST_VIEWED|MOST_COMMENTED|LEAST_COMMENTED",
                message = "유효하지 않은 정렬 타입입니다. 프로젝트 정렬 방식은 LATEST, OLDEST, MOST_LIKED, MOST_VIEWED, MOST_COMMENTED, LEAST_COMMENTED 만 가능합니다.")
        String sortType,

        @Schema(description = "도메인 ID")
        Long topicId,

        @Schema(description = "분석 목적 ID")
        Long analysisPurposeId,

        @Schema(description = "데이터 출처 ID")
        Long dataSourceId,

        @Schema(description = "작성자 레벨 ID")
        Long authorLevelId
) {}
