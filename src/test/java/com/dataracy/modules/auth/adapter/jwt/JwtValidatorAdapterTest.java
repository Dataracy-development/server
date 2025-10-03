/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.adapter.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dataracy.modules.user.domain.enums.RoleType;

import io.jsonwebtoken.Claims;

@ExtendWith(MockitoExtension.class)
class JwtValidatorAdapterTest {

  @Mock private JwtUtilInternal jwtUtilInternal;

  @Mock private Claims claims;

  @InjectMocks private JwtValidatorAdapter jwtValidatorAdapter;

  @BeforeEach
  void setUp() {
    when(jwtUtilInternal.parseToken(anyString())).thenReturn(claims);
  }

  @Test
  @DisplayName("validateToken - 유효한 토큰을 검증한다")
  void validateToken_WhenValidToken_ValidatesSuccessfully() {
    // given
    String token = "valid.token.here";

    // when
    jwtValidatorAdapter.validateToken(token);

    // then
    verify(jwtUtilInternal).parseToken(token);
  }

  @Test
  @DisplayName("getUserIdFromToken - 토큰에서 사용자 ID를 추출한다")
  void getUserIdFromToken_WhenValidToken_ReturnsUserId() {
    // given
    String token = "valid.token.here";
    Long expectedUserId = 123L;
    when(claims.get("userId", Long.class)).thenReturn(expectedUserId);

    // when
    Long result = jwtValidatorAdapter.getUserIdFromToken(token);

    // then
    assertThat(result).isEqualTo(expectedUserId);
    verify(jwtUtilInternal).parseToken(token);
  }

  @Test
  @DisplayName("getRoleFromToken - 토큰에서 역할을 추출한다")
  void getRoleFromToken_WhenValidToken_ReturnsRole() {
    // given
    String token = "valid.token.here";
    String roleValue = "ROLE_USER";
    when(claims.get("role", String.class)).thenReturn(roleValue);

    // when
    RoleType result = jwtValidatorAdapter.getRoleFromToken(token);

    // then
    assertThat(result).isEqualTo(RoleType.ROLE_USER);
    verify(jwtUtilInternal).parseToken(token);
  }

  @Test
  @DisplayName("getEmailFromToken - 토큰에서 이메일을 추출한다")
  void getEmailFromToken_WhenValidToken_ReturnsEmail() {
    // given
    String token = "valid.token.here";
    String expectedEmail = "test@example.com";
    when(claims.get("email", String.class)).thenReturn(expectedEmail);

    // when
    String result = jwtValidatorAdapter.getEmailFromToken(token);

    // then
    assertThat(result).isEqualTo(expectedEmail);
    verify(jwtUtilInternal).parseToken(token);
  }

  @Test
  @DisplayName("getProviderFromRegisterToken - 레지스터 토큰에서 제공자를 추출한다")
  void getProviderFromRegisterToken_WhenValidToken_ReturnsProvider() {
    // given
    String token = "register.token.here";
    String expectedProvider = "google";
    when(claims.get("provider", String.class)).thenReturn(expectedProvider);

    // when
    String result = jwtValidatorAdapter.getProviderFromRegisterToken(token);

    // then
    assertThat(result).isEqualTo(expectedProvider);
    verify(jwtUtilInternal).parseToken(token);
  }

  @Test
  @DisplayName("getProviderIdFromRegisterToken - 레지스터 토큰에서 제공자 ID를 추출한다")
  void getProviderIdFromRegisterToken_WhenValidToken_ReturnsProviderId() {
    // given
    String token = "register.token.here";
    String expectedProviderId = "123456789";
    when(claims.get("providerId", String.class)).thenReturn(expectedProviderId);

    // when
    String result = jwtValidatorAdapter.getProviderIdFromRegisterToken(token);

    // then
    assertThat(result).isEqualTo(expectedProviderId);
    verify(jwtUtilInternal).parseToken(token);
  }

  @Test
  @DisplayName("getUserIdFromToken - null 사용자 ID를 반환한다")
  void getUserIdFromToken_WhenNullUserId_ReturnsNull() {
    // given
    String token = "valid.token.here";
    when(claims.get("userId", Long.class)).thenReturn(null);

    // when
    Long result = jwtValidatorAdapter.getUserIdFromToken(token);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("getEmailFromToken - null 이메일을 반환한다")
  void getEmailFromToken_WhenNullEmail_ReturnsNull() {
    // given
    String token = "valid.token.here";
    when(claims.get("email", String.class)).thenReturn(null);

    // when
    String result = jwtValidatorAdapter.getEmailFromToken(token);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("getRoleFromToken - null 역할을 반환한다")
  void getRoleFromToken_WhenNullRole_ThrowsException() {
    // given
    String token = "valid.token.here";
    when(claims.get("role", String.class)).thenReturn(null);

    // when & then
    com.dataracy.modules.user.domain.exception.UserException exception =
        catchThrowableOfType(
            () -> jwtValidatorAdapter.getRoleFromToken(token),
            com.dataracy.modules.user.domain.exception.UserException.class);
    assertAll(() -> assertThat(exception).isNotNull());
  }
}
