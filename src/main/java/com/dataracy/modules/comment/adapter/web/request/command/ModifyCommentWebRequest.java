/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.adapter.web.request.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "피드백 댓글 수정 웹 요청 DTO")
public record ModifyCommentWebRequest(
    @Schema(description = "피드백 내용", example = "피드백 내용") @NotBlank(message = "피드백 내용을 입력해주세요")
        String content) {}
