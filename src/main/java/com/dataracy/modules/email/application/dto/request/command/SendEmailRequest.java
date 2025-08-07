package com.dataracy.modules.email.application.dto.request.command;

/**
 * 이메일 인증 코드 전송을 위한 도메인 요청 DTO
 * @param email
 * @param purpose
 */
public record SendEmailRequest(
        String email,
        String purpose
) {}
