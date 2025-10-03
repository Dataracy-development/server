/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.application.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.port.out.jwt.JwtValidatorPort;
import com.dataracy.modules.user.domain.enums.RoleType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtQueryServiceTest {

  @Mock private JwtValidatorPort jwtValidatorPort;

  @Mock private JwtProperties jwtProperties;

  @InjectMocks private JwtQueryService service;

  private MockedStatic<com.dataracy.modules.common.logging.support.LoggerFactory> loggerFactoryMock;
  private com.dataracy.modules.common.logging.ServiceLogger loggerService;

  @BeforeEach
  void setUp() {
    loggerFactoryMock = mockStatic(com.dataracy.modules.common.logging.support.LoggerFactory.class);
    loggerService = mock(com.dataracy.modules.common.logging.ServiceLogger.class);
    loggerFactoryMock
        .when(com.dataracy.modules.common.logging.support.LoggerFactory::service)
        .thenReturn(loggerService);
    doReturn(Instant.now()).when(loggerService).logStart(anyString(), anyString());
    doNothing().when(loggerService).logSuccess(anyString(), anyString(), any(Instant.class));
  }

  @AfterEach
  void tearDown() {
    if (loggerFactoryMock != null) {
      loggerFactoryMock.close();
    }
  }

  @Nested
  @DisplayName("토큰 유효성 검사")
  class ValidateToken {

    @Test
    @DisplayName("성공 - 위임 메서드 정상 실행 및 로깅 검증")
    void success() {
      // given
      String token = "valid.jwt.token";

      // when
      service.validateToken(token);

      // then
      then(jwtValidatorPort).should().validateToken(token);

      // 로깅 검증
      then(loggerService).should().logStart(eq("JwtValidateUseCase"), contains("토큰 유효성 검사 서비스 시작"));
      then(loggerService)
          .should()
          .logSuccess(eq("JwtValidateUseCase"), contains("토큰 유효성 검사 서비스 성공"), any(Instant.class));
    }

    @Test
    @DisplayName("실패 - 내부 위임 메서드에서 예외 발생 시 전파")
    void fail() {
      // given
      String token = "bad.jwt.token";
      willThrow(new IllegalArgumentException("invalid token"))
          .given(jwtValidatorPort)
          .validateToken(token);

      // when
      IllegalArgumentException ex =
          catchThrowableOfType(() -> service.validateToken(token), IllegalArgumentException.class);

      // then
      assertThat(ex).hasMessageContaining("invalid token");

      // 로깅 검증
      then(loggerService).should().logStart(eq("JwtValidateUseCase"), contains("토큰 유효성 검사 서비스 시작"));
      then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
    }

    @Test
    @DisplayName("null 토큰 → IllegalArgumentException 발생")
    void failWhenTokenIsNull() {
      // given
      String token = null;
      willThrow(new IllegalArgumentException("Token cannot be null"))
          .given(jwtValidatorPort)
          .validateToken(token);

      // when
      IllegalArgumentException ex =
          catchThrowableOfType(() -> service.validateToken(token), IllegalArgumentException.class);

      // then
      assertThat(ex).hasMessageContaining("Token cannot be null");

      // 로깅 검증
      then(loggerService).should().logStart(eq("JwtValidateUseCase"), contains("토큰 유효성 검사 서비스 시작"));
    }
  }

  @Nested
  @DisplayName("토큰에서 사용자 ID 추출")
  class GetUserIdFromToken {

    @Test
    @DisplayName("성공 - 사용자 ID 추출 및 로깅 검증")
    void success() {
      // given
      String token = "valid.jwt.token";
      Long expectedUserId = 123L;
      given(jwtValidatorPort.getUserIdFromToken(token)).willReturn(expectedUserId);

      // when
      Long userId = service.getUserIdFromToken(token);

      // then
      assertThat(userId).isEqualTo(expectedUserId);
      then(jwtValidatorPort).should().getUserIdFromToken(token);

      // 로깅 검증
      then(loggerService)
          .should()
          .logStart(eq("JwtValidateUseCase"), contains("토큰으로부터 유저 아이디 추출 서비스 시작"));
      then(loggerService)
          .should()
          .logSuccess(
              eq("JwtValidateUseCase"), contains("토큰으로부터 유저 아이디 추출 서비스 성공"), any(Instant.class));
    }

    @Test
    @DisplayName("실패 - 잘못된 토큰으로 인한 예외 발생")
    void failWhenInvalidToken() {
      // given
      String token = "invalid.token";
      willThrow(new IllegalArgumentException("Invalid token format"))
          .given(jwtValidatorPort)
          .getUserIdFromToken(token);

      // when
      IllegalArgumentException ex =
          catchThrowableOfType(
              () -> service.getUserIdFromToken(token), IllegalArgumentException.class);

      // then
      assertThat(ex).hasMessageContaining("Invalid token format");

      // 로깅 검증
      then(loggerService)
          .should()
          .logStart(eq("JwtValidateUseCase"), contains("토큰으로부터 유저 아이디 추출 서비스 시작"));
      then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
    }
  }

  @Nested
  @DisplayName("토큰에서 역할 추출")
  class GetRoleFromToken {

    @Test
    @DisplayName("성공 - 사용자 역할 추출 및 로깅 검증")
    void success() {
      // given
      String token = "valid.jwt.token";
      RoleType expectedRole = RoleType.ROLE_USER;
      given(jwtValidatorPort.getRoleFromToken(token)).willReturn(expectedRole);

      // when
      RoleType role = service.getRoleFromToken(token);

      // then
      assertThat(role).isEqualTo(expectedRole);
      then(jwtValidatorPort).should().getRoleFromToken(token);

      // 로깅 검증
      then(loggerService)
          .should()
          .logStart(eq("JwtValidateUseCase"), contains("토큰으로부터 유저 role 추출 서비스 시작"));
      then(loggerService)
          .should()
          .logSuccess(
              eq("JwtValidateUseCase"), contains("토큰으로부터 유저 role 추출 서비스 성공"), any(Instant.class));
    }
  }

  @Nested
  @DisplayName("토큰 만료 시간 조회")
  class GetTokenExpirationTime {

    @Test
    @DisplayName("Access Token 만료 시간 조회 성공")
    void getAccessTokenExpirationTimeSuccess() {
      // given
      long expectedExpirationTime = 3600000L; // 1 hour
      given(jwtProperties.getAccessTokenExpirationTime()).willReturn(expectedExpirationTime);

      // when
      long expirationTime = service.getAccessTokenExpirationTime();

      // then
      assertThat(expirationTime).isEqualTo(expectedExpirationTime);
      then(jwtProperties).should().getAccessTokenExpirationTime();

      // 로깅 검증
      then(loggerService)
          .should()
          .logStart(eq("JwtValidateUseCase"), contains("어세스 토큰 유효기간 반환 서비스 시작"));
      then(loggerService)
          .should()
          .logSuccess(
              eq("JwtValidateUseCase"), contains("어세스 토큰 유효기간 반환 서비스 성공"), any(Instant.class));
    }

    @Test
    @DisplayName("Refresh Token 만료 시간 조회 성공")
    void getRefreshTokenExpirationTimeSuccess() {
      // given
      long expectedExpirationTime = 604800000L; // 7 days
      given(jwtProperties.getRefreshTokenExpirationTime()).willReturn(expectedExpirationTime);

      // when
      long expirationTime = service.getRefreshTokenExpirationTime();

      // then
      assertThat(expirationTime).isEqualTo(expectedExpirationTime);
      then(jwtProperties).should().getRefreshTokenExpirationTime();

      // 로깅 검증
      then(loggerService)
          .should()
          .logStart(eq("JwtValidateUseCase"), contains("리프레시 토큰 유효기간 반환 서비스 시작"));
      then(loggerService)
          .should()
          .logSuccess(
              eq("JwtValidateUseCase"), contains("리프레시 토큰 유효기간 반환 서비스 성공"), any(Instant.class));
    }
  }

  @Nested
  @DisplayName("리다이렉트 URL 조회")
  class GetRedirectUrls {

    @Test
    @DisplayName("온보딩 리다이렉트 URL 조회 성공")
    void getRedirectOnboardingUrlSuccess() {
      // given
      String expectedUrl = "https://example.com/onboarding";
      given(jwtProperties.getRedirectOnboarding()).willReturn(expectedUrl);

      // when
      String url = service.getRedirectOnboardingUrl();

      // then
      assertThat(url).isEqualTo(expectedUrl);
      then(jwtProperties).should().getRedirectOnboarding();

      // 로깅 검증
      then(loggerService)
          .should()
          .logStart(eq("JwtValidateUseCase"), contains("소셜 회원가입 시 추가정보 입력을 위한 온보딩 url 반환 서비스 시작"));
      then(loggerService)
          .should()
          .logSuccess(
              eq("JwtValidateUseCase"),
              contains("소셜 회원가입 시 추가정보 입력을 위한 온보딩 url 반환 서비스 성공"),
              any(Instant.class));
    }

    @Test
    @DisplayName("기본 리다이렉트 URL 조회 성공")
    void getRedirectBaseUrlSuccess() {
      // given
      String expectedUrl = "https://example.com";
      given(jwtProperties.getRedirectBase()).willReturn(expectedUrl);

      // when
      String url = service.getRedirectBaseUrl();

      // then
      assertThat(url).isEqualTo(expectedUrl);
      then(jwtProperties).should().getRedirectBase();

      // 로깅 검증
      then(loggerService)
          .should()
          .logStart(eq("JwtValidateUseCase"), contains("로그인, 회원가입 완료 후 이동하는 메인 url 반환 서비스 시작"));
      then(loggerService)
          .should()
          .logSuccess(
              eq("JwtValidateUseCase"),
              contains("로그인, 회원가입 완료 후 이동하는 메인 url 반환 서비스 성공"),
              any(Instant.class));
    }
  }
}
