/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.email.application.dto.response;

/**
 * 이메일 인증 코드 검증 완료 후 비밀번호 재설정 목적일 경우 재설정용 토큰 발급 응답 DTO
 *
 * @param resetToken 비밀번호 재설정용 토큰
 */
public record GetResetTokenResponse(String resetToken) {}
