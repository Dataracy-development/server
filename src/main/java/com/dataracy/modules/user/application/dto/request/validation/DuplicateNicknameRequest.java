package com.dataracy.modules.user.application.dto.request.validation;

/**
 * 닉네임 중복체크를 위한 도메인 요청 DTO
 *
 * @param nickname 닉네임
 */
public record DuplicateNicknameRequest(
        String nickname
) {}
