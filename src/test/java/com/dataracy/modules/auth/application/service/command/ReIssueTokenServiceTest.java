package com.dataracy.modules.auth.application.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;
import com.dataracy.modules.auth.application.port.in.auth.SelfLoginUseCase;
import com.dataracy.modules.auth.application.port.out.jwt.JwtGeneratorPort;
import com.dataracy.modules.auth.application.port.out.jwt.JwtValidatorPort;
import com.dataracy.modules.auth.application.port.out.token.ManageRefreshTokenPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.user.domain.enums.RoleType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReIssueTokenServiceTest {

  // Test constants
  private static final Long ONE_HOUR_IN_MILLIS = 3600000L;
  private static final Long TWO_WEEKS_IN_MILLIS = 1209600000L;

  @Mock private JwtValidatorPort jwtValidatorPort;

  @Mock private JwtGeneratorPort jwtGeneratorPort;

  @Mock private ManageRefreshTokenPort manageRefreshTokenPort;

  @Mock private SelfLoginUseCase selfLoginUseCase;

  @Spy private JwtProperties jwtProperties = new JwtProperties();

  @InjectMocks private AuthCommandService service;

  @BeforeEach
  void setUp() {
    jwtProperties.setAccessTokenExpirationTime(ONE_HOUR_IN_MILLIS);
    jwtProperties.setRefreshTokenExpirationTime(TWO_WEEKS_IN_MILLIS);
  }

  @Nested
  @DisplayName("토큰 재발급")
  class ReIssueToken {

    @Test
    @DisplayName("유효한 RefreshToken → 새 Access/Refresh 토큰 반환")
    void reIssueSuccess() {
      // given
      String refreshToken = "valid-refresh";
      Long userId = 1L;

      given(jwtValidatorPort.getUserIdFromToken(refreshToken)).willReturn(userId);
      given(manageRefreshTokenPort.getRefreshToken(userId.toString())).willReturn(refreshToken);
      given(jwtValidatorPort.getRoleFromToken(refreshToken)).willReturn(RoleType.ROLE_USER);
      given(jwtGeneratorPort.generateAccessToken(userId, RoleType.ROLE_USER))
          .willReturn("new-access");
      given(jwtGeneratorPort.generateRefreshToken(userId, RoleType.ROLE_USER))
          .willReturn("new-refresh");

      // when
      ReIssueTokenResponse res = service.reIssueToken(refreshToken);

      // then
      assertAll(
          () -> assertThat(res.accessToken()).isEqualTo("new-access"),
          () -> assertThat(res.refreshToken()).isEqualTo("new-refresh"),
          () -> assertThat(res.accessTokenExpiration()).isEqualTo(ONE_HOUR_IN_MILLIS),
          () -> assertThat(res.refreshTokenExpiration()).isEqualTo(TWO_WEEKS_IN_MILLIS));
    }

    @Test
    @DisplayName("만료된 RefreshToken(null 반환) → AuthException(EXPIRED_REFRESH_TOKEN)")
    void reIssueExpired() {
      // given
      String refreshToken = "expired";
      given(jwtValidatorPort.getUserIdFromToken(refreshToken)).willReturn(null);

      // when & then
      AuthException ex =
          catchThrowableOfType(() -> service.reIssueToken(refreshToken), AuthException.class);
      assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("Redis에 저장된 RefreshToken 없음 → AuthException(EXPIRED_REFRESH_TOKEN)")
    void reIssueNoTokenInRedis() {
      // given
      String refreshToken = "refresh";
      Long userId = 1L;

      given(jwtValidatorPort.getUserIdFromToken(refreshToken)).willReturn(userId);
      given(manageRefreshTokenPort.getRefreshToken(userId.toString())).willReturn(null);

      // when & then
      AuthException ex =
          catchThrowableOfType(() -> service.reIssueToken(refreshToken), AuthException.class);
      assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("Redis 토큰과 불일치 → AuthException(REFRESH_TOKEN_USER_MISMATCH_IN_REDIS)")
    void reIssueMismatch() {
      // given
      String refreshToken = "refresh";
      Long userId = 1L;

      given(jwtValidatorPort.getUserIdFromToken(refreshToken)).willReturn(userId);
      given(manageRefreshTokenPort.getRefreshToken(userId.toString())).willReturn("other-token");

      // when & then
      AuthException ex =
          catchThrowableOfType(() -> service.reIssueToken(refreshToken), AuthException.class);
      assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.REFRESH_TOKEN_USER_MISMATCH_IN_REDIS);
    }

    @Test
    @DisplayName("유효하지 않은 RefreshToken(JWT 파싱 실패) → AuthException(INVALID_REFRESH_TOKEN)")
    void reIssueInvalidToken() {
      // given
      String refreshToken = "invalid";
      given(jwtValidatorPort.getUserIdFromToken(refreshToken))
          .willThrow(new AuthException(AuthErrorStatus.INVALID_TOKEN));

      // when & then
      AuthException ex =
          catchThrowableOfType(() -> service.reIssueToken(refreshToken), AuthException.class);
      assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.INVALID_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("토큰 재발급 (Redis 연결 장애) → REDIS_CONNECTION_FAILURE 발생")
    void reIssueRedisConnectionError() {
      // given
      String refreshToken = "refresh";
      Long userId = 1L;

      given(jwtValidatorPort.getUserIdFromToken(refreshToken)).willReturn(userId);
      given(manageRefreshTokenPort.getRefreshToken(userId.toString()))
          .willThrow(new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE));

      // when & then
      CommonException ex =
          catchThrowableOfType(() -> service.reIssueToken(refreshToken), CommonException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
    }

    @Test
    @DisplayName("토큰 재발급 (네트워크 연결 장애 등) → DATA_ACCESS_EXCEPTION 발생")
    void reIssueDataAccessError() {
      // given
      String refreshToken = "refresh";
      Long userId = 1L;

      given(jwtValidatorPort.getUserIdFromToken(refreshToken)).willReturn(userId);
      given(manageRefreshTokenPort.getRefreshToken(userId.toString()))
          .willThrow(new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION));

      // when & then
      CommonException ex =
          catchThrowableOfType(() -> service.reIssueToken(refreshToken), CommonException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
    }
  }
}
