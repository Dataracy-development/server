/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.web.response.read;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토픽별 데이터셋 개수 웹 응답 DTO")
public record DataGroupCountWebResponse(
    @Schema(description = "토픽 아이디", example = "3") Long topicId,
    @Schema(description = "토픽 라벨", example = "디자인") String topicLabel,
    @Schema(description = "데이터셋 개수", example = "3") Long count) {}
