package com.dataracy.modules.auth.application.dto.request;

/**
 * 자체 로그인을 위한 도메인 요청 객체
 *
 * @param email 이메일
 * @param password 비밀번호
 */
public record SelfLoginRequest(
        String email,
        String password
) {}
