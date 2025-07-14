package com.dataracy.modules.data.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "프로젝트 업로드 요청 DTO")
public record DataUploadWebRequest(
        @Schema(description = "제목", example = "프로젝트명")
        @NotBlank(message = "제목을 입력해주세요")
        String title,

        @Schema(description = "도메인", example = "2")
        @NotNull(message = "도메인을 입력해주세요")
        Long topicId,

        @Schema(description = "데이터 출처", example = "3")
        @NotNull(message = "데이터 출처를 입력해주세요")
        Long dataSourceId,

        @Schema(description = "작성자 유형", example = "3")
        @NotNull(message = "작성자 유형을 입력해주세요")
        Long authorLevelId,

        @Schema(description = "설명", example = "지금 이 데이터는 ~~.")
        @NotBlank(message = "설명을 입력해주세요")
        String description,

        @Schema(description = "설명", example = "지금 이 데이터는 ~~.")
        @NotBlank(message = "설명을 입력해주세요")
        String analysisGuide
) {}
