package com.dataracy.modules.auth.adapter.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "리프레시 토큰 발급 웹 응답 DTO")
public record RefreshTokenWebResponse(
        @Schema(description = "리프레시 토큰", example = "ezwkdjf2kjk3jd~~")
        String refreshToken,

        @Schema(description = "리프레시 토큰 만료 시간", example = "3196009000")
        long refreshTokenExpiration
) {}
