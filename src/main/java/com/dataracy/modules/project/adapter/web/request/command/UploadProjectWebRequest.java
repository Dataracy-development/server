package com.dataracy.modules.project.adapter.web.request.command;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "프로젝트 업로드 웹 요청 DTO")
public record UploadProjectWebRequest(
    @Schema(description = "제목", example = "프로젝트명") @NotBlank(message = "제목을 입력해주세요") String title,
    @Schema(description = "도메인", example = "2") @NotNull(message = "도메인을 입력해주세요") @Min(1)
        Long topicId,
    @Schema(description = "목적", example = "2") @NotNull(message = "목적을 입력해주세요") @Min(1)
        Long analysisPurposeId,
    @Schema(description = "데이터 출처", example = "3") @NotNull(message = "데이터 출처를 입력해주세요") @Min(1)
        Long dataSourceId,
    @Schema(description = "작성자 유형", example = "3") @NotNull(message = "작성자 유형을 입력해주세요") @Min(1)
        Long authorLevelId,
    @Schema(description = "이어가기 선택", example = "true") @NotNull(message = "이어가기 유무를 입력해주세요")
        Boolean isContinue,
    @Schema(description = "이어가기 프로젝트", example = "3") @Min(1) Long parentProjectId,
    @Schema(description = "내용", example = "지금 데이터 출처에 대해서 ~~.") @NotBlank(message = "내용을 입력해주세요")
        String content,
    @Schema(description = "데이터셋 리스트", example = "[1, 3]")
        @NotNull(message = "데이터셋 리스트에 null은 올 수 없습니다.")
        List<Long> dataIds) {}
