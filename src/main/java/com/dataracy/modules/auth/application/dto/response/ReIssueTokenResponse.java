package com.dataracy.modules.auth.application.dto.response;

/**
 * 쿠키의 리프레시 토큰을 통한 토큰 재발급을 위한 애플리케이션 응답 객체
 *
 * @param accessToken 새롭게 발급한 어세스토큰
 * @param refreshToken 새롭게 발급한 리프레시토큰
 * @param accessTokenExpiration 어세스토큰 만료시간
 * @param refreshTokenExpiration 리프레시토큰 만료시간
 */
public record ReIssueTokenResponse(
        String accessToken,
        String refreshToken,
        long accessTokenExpiration,
        long refreshTokenExpiration
) {}
