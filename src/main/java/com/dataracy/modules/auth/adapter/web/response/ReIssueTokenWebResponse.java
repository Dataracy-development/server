/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.adapter.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 재발급 웹 응답 DTO")
public record ReIssueTokenWebResponse(
    @Schema(description = "어세스 토큰", example = "ezwkdjf2kjk3jd~~") String accessToken,
    @Schema(description = "리프레시 토큰", example = "ezwkdjf2kjk3jd~~") String refreshToken,
    @Schema(description = "어세스 토큰 만료 시간", example = "3196009000") long accessTokenExpiration,
    @Schema(description = "리프레시 토큰 만료 시간", example = "3600000") long refreshTokenExpiration) {}
