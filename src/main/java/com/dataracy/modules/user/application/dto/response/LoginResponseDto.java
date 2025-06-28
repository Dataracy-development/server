package com.dataracy.modules.user.application.dto.response;

/**
 * 로그인 성공 시 리프레시 토콘 지급
 * @param userId
 * @param refreshToken
 * @param refreshTokenExpiration
 */
public record LoginResponseDto(
        Long userId,
        String refreshToken,
        long refreshTokenExpiration
) {
}
