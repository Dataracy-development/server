/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.application.dto.request.password;

import com.dataracy.modules.user.application.dto.request.PasswordConfirmable;

/**
 * 비밀번호를 변경하는 애플리케이션 요청 DTO
 *
 * @param password 비밀번호
 * @param passwordConfirm 비밀번호 확인
 */
public record ChangePasswordRequest(String password, String passwordConfirm)
    implements PasswordConfirmable {}
