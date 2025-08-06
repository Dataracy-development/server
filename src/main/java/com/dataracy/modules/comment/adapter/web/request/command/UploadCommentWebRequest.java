package com.dataracy.modules.comment.adapter.web.request.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "피드백 댓글 업로드 요청 DTO")
public record UploadCommentWebRequest(
        @Schema(description = "피드백 내용", example = "피드백 내용")
        @NotBlank(message = "피드백 내용을 입력해주세요")
        String content,

        @Min(1)
        @Schema(description = "답글 대상 댓글 ID. 일반 댓글일 경우 null")
        Long parentCommentId
) {}
