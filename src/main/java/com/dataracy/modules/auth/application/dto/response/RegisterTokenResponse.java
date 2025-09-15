package com.dataracy.modules.auth.application.dto.response;

/**
 * 소셜 서버로부터 유저 정보 조회 성공 시 클라이언트측에 쿠키로 전달해주기 위한 레지스터 토큰 응답 객체
 *
 * @param registerToken 레지스터 토큰
 * @param registerTokenExpiration 레지스터 토큰 만료시간 (밀리초 단위)
 */
public record RegisterTokenResponse(
        String registerToken,
        long registerTokenExpiration
) {}
