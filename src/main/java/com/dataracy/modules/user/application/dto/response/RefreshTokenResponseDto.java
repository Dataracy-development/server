package com.dataracy.modules.user.application.dto.response;

/**
 * 로그인 성공 시 리프레시 토콘 지급
 * 실제 응답 DTO가 아닌 중간 과정 DTO이기 때문에 스웨거 문서화를 생략한다.
 * @param userId
 * @param refreshToken
 * @param refreshTokenExpiration
 */
public record RefreshTokenResponseDto(
        Long userId,
        String refreshToken,
        long refreshTokenExpiration
) {
}
