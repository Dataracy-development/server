package com.dataracy.modules.auth.adapter.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 로그인 또는 회원가입 성공 시 리프레시 토큰 쿠키 설정을 위한 도메인 응답 객체
 *
 * @param refreshToken 쿠키에 설정할 리프레시 토큰
 * @param refreshTokenExpiration 리프레시 토큰 유효기간 (밀리초 단위)
 */
@Schema(description = "리프레시 토큰 발급 웹 응답 DTO")
public record RefreshTokenWebResponse(
        @Schema(description = "리프레시 토큰", example = "ezwkdjf2kjk3jd~~")
        String refreshToken,

        @Schema(description = "리프레시 토큰 만료 시간", example = "3196009000")
        long refreshTokenExpiration
) {}
