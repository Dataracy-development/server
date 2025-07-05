package com.dataracy.modules.user.application.dto.request;

/**
 * 비밀번호를 변경하는 도메인 요청 DTO
 *
 * @param password 비밀번호
 */
public record ChangePasswordRequest(
        String password
) {}
