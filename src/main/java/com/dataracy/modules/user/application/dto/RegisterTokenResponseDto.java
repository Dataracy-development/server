package com.dataracy.modules.user.application.dto;

public record RegisterTokenResponseDto(
        String registerToken,
        long registerTokenExpiration
) {
}
