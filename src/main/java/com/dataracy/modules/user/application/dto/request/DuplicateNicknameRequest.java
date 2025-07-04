package com.dataracy.modules.user.application.dto.request;

/**
 * 닉네임 중복체크를 위한 도메인 요청 객체
 * @param nickname 닉네임
 */
public record DuplicateNicknameRequest(
        String nickname
) {}
