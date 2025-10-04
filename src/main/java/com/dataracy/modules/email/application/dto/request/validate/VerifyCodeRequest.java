package com.dataracy.modules.email.application.dto.request.validate;

/**
 * 이메일 인증 코드 검증을 위한 애플리케이션 요청 DTO
 *
 * @param email 이메일
 * @param code 인증코드
 * @param purpose 이메일 전송 목적
 */
public record VerifyCodeRequest(String email, String code, String purpose) {}
