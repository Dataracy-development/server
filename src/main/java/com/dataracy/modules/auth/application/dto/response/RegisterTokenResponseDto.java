package com.dataracy.modules.auth.application.dto.response;

public record RegisterTokenResponseDto(
        String registerToken,
        long registerTokenExpiration
) {
}
