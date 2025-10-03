/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.like.adapter.web.request;

import com.dataracy.modules.common.support.annotation.ValidEnumValue;
import com.dataracy.modules.like.domain.enums.TargetType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "타겟 좋아요 및 취소 요청 DTO")
public record TargetLikeWebRequest(
    @Schema(description = "타겟 아이디", example = "3") @NotNull(message = "NULL이 아닌 값을 입력해주세요") @Min(1)
        Long targetId,
    @Schema(description = "좋아요 타겟", example = "PROJECT")
        @ValidEnumValue(
            enumClass = TargetType.class,
            required = true,
            message = "좋아요 타겟은 PROJECT, COMMENT만 가능합니다.")
        String targetType,
    @Schema(description = "이전 좋아요 상태", example = "false")
        @NotNull(message = "좋아요 상태는 NULL 값이 불가합니다.")
        boolean previouslyLiked) {}
