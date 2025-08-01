package com.dataracy.modules.auth.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * http를 통해 클라이언트와 통신을 하기 위하여 리프레시 토큰을 통해 토큰 재발급을 하는 웹 요청 DTO
 *
 * @param refreshToken
 */
public record RefreshTokenWebRequest(
        @Schema(description = "리프레시 토큰", example = "ezwkdjf2kjk3jd~~")
        String refreshToken
) {}
