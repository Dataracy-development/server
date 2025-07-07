package com.dataracy.modules.user.application.dto.request;

/**
 * 비밀번호를 확인하는 도메인 요청 DTO
 *
 * @param password 비밀번호
 */
public record ConfirmPasswordRequest(
        String password
) {}
