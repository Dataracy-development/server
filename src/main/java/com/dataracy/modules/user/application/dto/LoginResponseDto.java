package com.dataracy.modules.user.application.dto;

public record LoginResponseDto(
        Long userId,
        String refreshToken,
        long refreshTokenExpiration
) {
}
