package com.dataracy.modules.auth.application.service;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;
import com.dataracy.modules.auth.application.port.out.jwt.JwtGeneratorPort;
import com.dataracy.modules.auth.application.port.out.jwt.JwtValidatorPort;
import com.dataracy.modules.auth.application.port.out.token.ManageRefreshTokenPort;
import com.dataracy.modules.auth.application.service.command.AuthCommandService;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.user.domain.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

class ReIssueTokenUseCaseTest {

    private JwtValidatorPort jwtValidatorPort;
    private JwtGeneratorPort jwtGeneratorPort;
    private ManageRefreshTokenPort manageRefreshTokenPort;
    private JwtProperties jwtProperties;

    private AuthCommandService service;

    @BeforeEach
    void setup() {
        jwtValidatorPort = mock(JwtValidatorPort.class);
        jwtGeneratorPort = mock(JwtGeneratorPort.class);
        manageRefreshTokenPort = mock(ManageRefreshTokenPort.class);

        jwtProperties = new JwtProperties();
        jwtProperties.setAccessTokenExpirationTime(3600000L);
        jwtProperties.setRefreshTokenExpirationTime(1209600000L);

        service = new AuthCommandService(
                jwtProperties,
                jwtGeneratorPort,
                jwtValidatorPort,
                manageRefreshTokenPort,
                null // SelfLoginUseCase는 여기선 필요 없음
        );
    }

    @Test
    @DisplayName("유효한 RefreshToken → 새 Access/Refresh 토큰 반환")
    void reIssueSuccess() {
        String refreshToken = "valid-refresh";
        Long userId = 1L;

        given(jwtValidatorPort.getUserIdFromToken(refreshToken)).willReturn(userId);
        given(manageRefreshTokenPort.getRefreshToken(userId.toString())).willReturn(refreshToken);
        given(jwtValidatorPort.getRoleFromToken(refreshToken)).willReturn(RoleType.ROLE_USER);
        given(jwtGeneratorPort.generateAccessToken(userId, RoleType.ROLE_USER)).willReturn("new-access");
        given(jwtGeneratorPort.generateRefreshToken(userId, RoleType.ROLE_USER)).willReturn("new-refresh");

        ReIssueTokenResponse res = service.reIssueToken(refreshToken);

        assertThat(res.accessToken()).isEqualTo("new-access");
        assertThat(res.refreshToken()).isEqualTo("new-refresh");
        assertThat(res.accessTokenExpiration()).isEqualTo(3600000L);
        assertThat(res.refreshTokenExpiration()).isEqualTo(1209600000L);
    }

    @Test
    @DisplayName("만료된 RefreshToken(null 반환) → AuthException(EXPIRED_REFRESH_TOKEN)")
    void reIssueExpired() {
        String refreshToken = "expired";
        given(jwtValidatorPort.getUserIdFromToken(refreshToken)).willReturn(null);

        AuthException ex = catchThrowableOfType(
                () -> service.reIssueToken(refreshToken),
                AuthException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("Redis에 저장된 RefreshToken 없음 → AuthException(EXPIRED_REFRESH_TOKEN)")
    void reIssueNoTokenInRedis() {
        String refreshToken = "refresh";
        Long userId = 1L;

        given(jwtValidatorPort.getUserIdFromToken(refreshToken)).willReturn(userId);
        given(manageRefreshTokenPort.getRefreshToken(userId.toString())).willReturn(null);

        AuthException ex = catchThrowableOfType(
                () -> service.reIssueToken(refreshToken),
                AuthException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("Redis 토큰과 불일치 → AuthException(REFRESH_TOKEN_USER_MISMATCH_IN_REDIS)")
    void reIssueMismatch() {
        String refreshToken = "refresh";
        Long userId = 1L;

        given(jwtValidatorPort.getUserIdFromToken(refreshToken)).willReturn(userId);
        given(manageRefreshTokenPort.getRefreshToken(userId.toString())).willReturn("other-token");

        AuthException ex = catchThrowableOfType(
                () -> service.reIssueToken(refreshToken),
                AuthException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.REFRESH_TOKEN_USER_MISMATCH_IN_REDIS);
    }

    @Test
    @DisplayName("유효하지 않은 RefreshToken(JWT 파싱 실패) → AuthException(INVALID_REFRESH_TOKEN)")
    void reIssueInvalidToken() {
        String refreshToken = "invalid";
        given(jwtValidatorPort.getUserIdFromToken(refreshToken))
                .willThrow(new AuthException(AuthErrorStatus.INVALID_TOKEN));

        AuthException ex = catchThrowableOfType(
                () -> service.reIssueToken(refreshToken),
                AuthException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.INVALID_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("토큰 재발급 (Redis 연결 장애) → REDIS_CONNECTION_FAILURE 발생")
    void reIssueRedisConnectionError() {
        String refreshToken = "refresh";
        Long userId = 1L;

        given(jwtValidatorPort.getUserIdFromToken(refreshToken)).willReturn(userId);
        given(manageRefreshTokenPort.getRefreshToken(userId.toString()))
                .willThrow(new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE));

        CommonException ex = catchThrowableOfType(
                () -> service.reIssueToken(refreshToken),
                CommonException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
    }

    @Test
    @DisplayName("토큰 재발급 (네트워크 연결 장애 등) → DATA_ACCESS_EXCEPTION 발생")
    void reIssueDataAccessError() {
        String refreshToken = "refresh";
        Long userId = 1L;

        given(jwtValidatorPort.getUserIdFromToken(refreshToken)).willReturn(userId);
        given(manageRefreshTokenPort.getRefreshToken(userId.toString()))
                .willThrow(new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION));

        CommonException ex = catchThrowableOfType(
                () -> service.reIssueToken(refreshToken),
                CommonException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
    }
}
