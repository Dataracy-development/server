package com.dataracy.modules.email.application.dto.request;

/**
 * 이메일 인증 코드 검증을 위한 도메인 요청 DTO
 * @param email 이메일
 * @param code 인증코드
 */
public record VerifyCodeRequest(
        String email,
        String code
) {}
