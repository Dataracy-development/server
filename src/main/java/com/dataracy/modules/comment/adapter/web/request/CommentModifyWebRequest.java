package com.dataracy.modules.comment.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "피드백 댓글 수정 요청 DTO")
public record CommentModifyWebRequest(
        @Schema(description = "피드백 내용", example = "피드백 내용")
        @NotBlank(message = "피드백 내용을 입력해주세요")
        String content
) {}
