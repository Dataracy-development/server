package com.dataracy.modules.auth.adapter.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 쿠키의 리프레시 토큰을 통한 토큰 재발급을 위한 도메인 응답 객체
 *
 * @param accessToken 새롭게 발급한 어세스토큰
 * @param refreshToken 새롭게 발급한 리프레시토큰
 * @param accessTokenExpiration 어세스토큰 만료시간
 * @param refreshTokenExpiration 리프레시토큰 만료시간
 */
@Schema(description = "토큰 재발급 웹 응답 DTO")
public record ReIssueTokenWebResponse(
        @Schema(description = "어세스 토큰", example = "ezwkdjf2kjk3jd~~")
        String accessToken,

        @Schema(description = "리프레시 토큰", example = "ezwkdjf2kjk3jd~~")
        String refreshToken,

        @Schema(description = "어세스 토큰 만료 시간", example = "3196009000")
        long accessTokenExpiration,

        @Schema(description = "리프레시 토큰 만료 시간", example = "3600000")
        long refreshTokenExpiration
) {}
