package com.dataracy.modules.dataset.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "데이터셋 업로드 요청 DTO")
public record DataUploadWebRequest(
        @Schema(description = "제목", example = "데이터셋 제목명")
        @NotBlank(message = "제목을 입력해주세요")
        String title,

        @Schema(description = "도메인", example = "2")
        @NotNull(message = "도메인을 입력해주세요")
        Long topicId,

        @Schema(description = "데이터 출처", example = "3")
        @NotNull(message = "데이터 출처를 입력해주세요")
        Long dataSourceId,

        @Schema(description = "데이터 유형", example = "3")
        @NotNull(message = "데이터 유형을 입력해주세요")
        Long dataTypeId,

        @Schema(description = "데이터 수집 시작일", example = "2025-01-01")
        LocalDate startDate,

        @Schema(description = "데이터 수집 종료일", example = "2025-2-01")
        LocalDate endDate,

        @Schema(description = "설명", example = "지금 이 데이터는 설명은 ~~.")
        @NotBlank(message = "설명을 입력해주세요")
        String description,

        @Schema(description = "분석 가이드", example = "지금 이 데이터 분석 가이드는 ~~.")
        @NotBlank(message = "분석 가이드를 입력해주세요")
        String analysisGuide
) {}
