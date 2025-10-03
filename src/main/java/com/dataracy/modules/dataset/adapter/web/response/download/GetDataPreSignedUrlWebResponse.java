/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.web.response.download;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "데이터셋 다운로드를 위한 일시적 url 반환 웹 응답 DTO")
public record GetDataPreSignedUrlWebResponse(
    @Schema(description = "유효기간이 있는 다운로드 링크", example = "https://~~~~") String preSignedUrl) {}
