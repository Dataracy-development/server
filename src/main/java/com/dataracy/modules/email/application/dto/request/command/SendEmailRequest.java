/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.email.application.dto.request.command;

/**
 * 이메일 인증 코드 전송을 위한 애플리케이션 요청 DTO
 *
 * @param email 이메일
 * @param purpose 이메일 인증 코드 전송 목적
 */
public record SendEmailRequest(String email, String purpose) {}
