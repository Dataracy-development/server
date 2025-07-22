package com.dataracy.modules.dataset.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record DataFilterWebRequest(
        @Schema(description = "검색 키워드(제목)", example = "LLM")
        String keyword,

        @Pattern(regexp = "LATEST|OLDEST|DOWNLOAD|UTILIZE",
                message = "유효하지 않은 정렬 타입입니다. 프로젝트 정렬 방식은 LATEST, OLDEST, DOWNLOAD, UTILIZE 만 가능합니다.")
        String sortType,

        @Schema(description = "도메인 ID")
        Long topicId,

        @Schema(description = "데이터 출처 ID")
        Long dataSourceId,

        @Schema(description = "데이터 유형 ID")
        Long dataTypeId,

        @Schema(description = "데이터 연도 (예: 2024)", example = "2024")
        @Min(value = 1900, message = "연도는 1900 이상이어야 합니다.")
        @Max(value = 2100, message = "연도는 2100 이하이어야 합니다.")
        Integer year
) {}
