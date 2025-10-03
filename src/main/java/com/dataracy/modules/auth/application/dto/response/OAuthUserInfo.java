/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.application.dto.response;

/**
 * 소셜 로그인으로부터 받은 유저 정보
 *
 * @param email 이메일
 * @param name 닉네임
 * @param provider 제공자
 * @param providerId 제공자로부터 받은 유저 id
 */
public record OAuthUserInfo(String email, String name, String provider, String providerId) {}
