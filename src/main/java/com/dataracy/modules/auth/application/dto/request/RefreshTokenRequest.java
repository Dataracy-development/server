package com.dataracy.modules.auth.application.dto.request;

/**
 * 토큰 재발급을 위한 도메인 요청 객체
 *
 * @param refreshToken 리프레시 토큰
 */
public record RefreshTokenRequest(
        String refreshToken
) {}
