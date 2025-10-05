package com.dataracy.modules.auth.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 재발급 Web 요청")
public record RefreshTokenWebRequest(
    @Schema(description = "리프레시 토큰", example = "ezwkdjf2kjk3jd~~") String refreshToken) {}
