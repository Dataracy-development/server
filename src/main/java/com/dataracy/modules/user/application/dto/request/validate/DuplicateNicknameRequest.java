/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.application.dto.request.validate;

/**
 * 닉네임 중복체크를 위한 애플리케이션 요청 DTO
 *
 * @param nickname 닉네임
 */
public record DuplicateNicknameRequest(String nickname) {}
