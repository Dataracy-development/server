package com.dataracy.modules.comment.adapter.web.response.command;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "댓글 업로드 웹 응답 DTO")
public record UploadCommentWebResponse(
        @Schema(description = "댓글 아이디", example = "1")
        Long id
) {}
