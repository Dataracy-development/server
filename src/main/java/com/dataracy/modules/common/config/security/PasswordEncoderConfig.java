/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** 패스워드 암호화 설정 */
@Configuration
public class PasswordEncoderConfig {

  private static final int MAX_PASSWORD_LENGTH = 128; // CVE-2025-22228 해결을 위한 비밀번호 길이 제한

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder() {
      @Override
      public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
          throw new IllegalArgumentException("Raw password cannot be null");
        }
        if (rawPassword.length() > MAX_PASSWORD_LENGTH) {
          throw new IllegalArgumentException(
              "Password length cannot exceed " + MAX_PASSWORD_LENGTH + " characters");
        }
        return super.encode(rawPassword);
      }

      @Override
      public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
          return false;
        }
        if (rawPassword.length() > MAX_PASSWORD_LENGTH) {
          return false;
        }
        return super.matches(rawPassword, encodedPassword);
      }
    };
  }
}
