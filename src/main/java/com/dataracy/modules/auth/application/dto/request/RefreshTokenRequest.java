package com.dataracy.modules.auth.application.dto.request;

/**
 * 개발용 토큰 재발급을 위한 도메인 요청 DTO
 *
 * @param refreshToken 리프레시 토큰
 */
public record RefreshTokenRequest(
        String refreshToken
) {}
