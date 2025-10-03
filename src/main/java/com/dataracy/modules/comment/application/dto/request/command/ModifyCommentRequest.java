/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.application.dto.request.command;

/**
 * 댓글 수정 애플리케이션 요청 DTO
 *
 * @param content 댓글 내용
 */
public record ModifyCommentRequest(String content) {}
