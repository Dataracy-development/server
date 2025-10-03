/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.web.request.search;

import com.dataracy.modules.common.support.annotation.ValidEnumValue;
import com.dataracy.modules.dataset.domain.enums.DataSortType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Schema(description = "데이터셋 필터링 웹 요청 DTO")
public record FilteringDataWebRequest(
    @Schema(description = "검색 키워드(제목)", example = "LLM") String keyword,
    @Schema(description = "정렬 타입", example = "LATEST")
        @ValidEnumValue(
            enumClass = DataSortType.class,
            message = "데이터 정렬 유형은 LATEST, OLDEST, DOWNLOAD, UTILIZE만 가능합니다.")
        String sortType,
    @Schema(description = "도메인 ID") Long topicId,
    @Schema(description = "데이터 출처 ID") Long dataSourceId,
    @Schema(description = "데이터 유형 ID") Long dataTypeId,
    @Schema(description = "데이터 연도 (예: 2024)", example = "2024")
        @Min(value = 1900, message = "연도는 1900 이상이어야 합니다.")
        @Max(value = 2100, message = "연도는 2100 이하이어야 합니다.")
        Integer year) {}
