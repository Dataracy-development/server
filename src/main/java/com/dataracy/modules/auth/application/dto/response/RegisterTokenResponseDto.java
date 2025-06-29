package com.dataracy.modules.auth.application.dto.response;

/**
 * 로그인 성공 시 발급받는 레지스터 토큰
 * @param registerToken 레지스터 토큰
 * @param registerTokenExpiration 레지스터 토큰 만료시간 (밀리초 단위)
 */
public record RegisterTokenResponseDto(
        String registerToken,
        long registerTokenExpiration
) {
}
