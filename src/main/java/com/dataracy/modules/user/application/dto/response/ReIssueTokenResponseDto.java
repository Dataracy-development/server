package com.dataracy.modules.user.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReIssueTokenResponseDto(

        @Schema(description = "유저id", example = "3")
        Long userId,

        @Schema(description = "어세스 토큰", example = "ezsdsdfadsaf")
        String accessToken,

        @Schema(description = "리프레시 토큰", example = "ezsdsdfadsafwwefwef")
        String refreshToken,

        @Schema(description = "어세스토큰 유효기간", example = "3600000")
        long accessTokenExpiration,

        @Schema(description = "리프레시토큰 유효기간", example = "1209600000")
        long refreshTokenExpiration
) {
}
