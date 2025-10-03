/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.web.response.command;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 업로드 웹 응답 DTO")
public record UploadProjectWebResponse(@Schema(description = "프로젝트 아이디", example = "1") Long id) {}
