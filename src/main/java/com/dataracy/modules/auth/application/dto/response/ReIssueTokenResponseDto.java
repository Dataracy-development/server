package com.dataracy.modules.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 리프레시토큰을 통해 토큰 재발급을 진행한다.
 * @param userId 유저id
 * @param accessToken 어세스토큰
 * @param refreshToken 리프레시토큰
 * @param accessTokenExpiration 어세스토큰 만료시간
 * @param refreshTokenExpiration 리프레시토큰 만료시간
 */
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
