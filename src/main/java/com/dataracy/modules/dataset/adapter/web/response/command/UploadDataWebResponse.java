/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.web.response.command;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "데이터셋 업로드 웹 응답 DTO")
public record UploadDataWebResponse(@Schema(description = "데이터셋 아이디", example = "1") Long id) {}
