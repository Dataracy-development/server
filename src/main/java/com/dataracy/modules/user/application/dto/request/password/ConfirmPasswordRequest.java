/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.application.dto.request.password;

/**
 * 비밀번호를 확인하는 애플리케이션 요청 DTO
 *
 * @param password 비밀번호
 */
public record ConfirmPasswordRequest(String password) {}
