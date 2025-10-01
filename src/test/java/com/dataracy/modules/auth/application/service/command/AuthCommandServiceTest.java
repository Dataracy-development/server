package com.dataracy.modules.auth.application.service.command;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.out.RateLimitPort;
import com.dataracy.modules.auth.application.port.out.jwt.JwtGeneratorPort;
import com.dataracy.modules.auth.application.port.out.jwt.JwtValidatorPort;
import com.dataracy.modules.auth.application.port.out.token.ManageRefreshTokenPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.user.application.port.in.query.auth.IsLoginPossibleUseCase;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthCommandServiceTest {

    @InjectMocks
    private AuthCommandService service;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private JwtGeneratorPort jwtGeneratorPort;

    @Mock
    private JwtValidatorPort jwtValidatorPort;

    @Mock
    private ManageRefreshTokenPort manageRefreshTokenPort;

    @Mock
    private RateLimitPort rateLimitPort;

    @Mock
    private IsLoginPossibleUseCase isLoginPossibleUseCase;

    @Nested
    @DisplayName("login 메서드 테스트")
    class LoginTest {

        @Test
        @DisplayName("로그인 성공")
        void loginSuccess() {
            // given
            SelfLoginRequest request = new SelfLoginRequest("test@example.com", "password");
            UserInfo userInfo = new UserInfo(1L, RoleType.ROLE_USER, "test@example.com", "testuser", 1L, 1L, List.of(1L), 1L, "https://example.com/profile.jpg", "테스트 사용자");
            String refreshToken = "refresh-token";

            given(isLoginPossibleUseCase.checkLoginPossibleAndGetUserInfo("test@example.com", "password")).willReturn(userInfo);
            given(jwtGeneratorPort.generateRefreshToken(1L, RoleType.ROLE_USER)).willReturn(refreshToken);
            given(jwtProperties.getRefreshTokenExpirationTime()).willReturn(7 * 24 * 60 * 60 * 1000L);

            // when
            RefreshTokenResponse result = service.login(request);

            // then
            assertAll(

                    () -> assertThat(result.refreshToken()).isEqualTo(refreshToken),

                    () -> assertThat(result.refreshTokenExpiration()).isEqualTo(7 * 24 * 60 * 60 * 1000L)

            );

        }
    }

    @Nested
    @DisplayName("loginWithRateLimit 메서드 테스트")
    class LoginWithRateLimitTest {

        @Test
        @DisplayName("레이트 리미팅 적용 로그인 성공")
        void loginWithRateLimitSuccess() {
            // given
            SelfLoginRequest request = new SelfLoginRequest("test@gmail.com", "password");
            String clientIp = "192.168.1.1";
            UserInfo userInfo = new UserInfo(1L, RoleType.ROLE_USER, "test@gmail.com", "testuser", 1L, 1L, List.of(1L), 1L, "https://example.com/profile.jpg", "테스트 사용자");
            String refreshToken = "refresh-token";

            given(isLoginPossibleUseCase.checkLoginPossibleAndGetUserInfo(anyString(), anyString())).willReturn(userInfo);
            given(rateLimitPort.isAllowed(anyString(), any(Integer.class), any(Integer.class))).willReturn(true);
            given(jwtGeneratorPort.generateRefreshToken(1L, RoleType.ROLE_USER)).willReturn(refreshToken);
            given(jwtProperties.getRefreshTokenExpirationTime()).willReturn(7 * 24 * 60 * 60 * 1000L);

            // when
            RefreshTokenResponse result = service.loginWithRateLimit(request, clientIp);

            // then
            assertThat(result.refreshToken()).isEqualTo(refreshToken);
        }

        @Test
        @DisplayName("레이트 리미팅 초과 시 AuthException 발생")
        void loginWithRateLimitFailWhenRateLimitExceeded() {
            // given
            SelfLoginRequest request = new SelfLoginRequest("attacker@example.com", "password");
            String clientIp = "192.168.1.1";
            UserInfo userInfo = new UserInfo(1L, RoleType.ROLE_USER, "attacker@example.com", "testuser", 1L, 1L, List.of(1L), 1L, "https://example.com/profile.jpg", "테스트 사용자");

            given(isLoginPossibleUseCase.checkLoginPossibleAndGetUserInfo(anyString(), anyString())).willReturn(userInfo);
            given(rateLimitPort.isAllowed(anyString(), any(Integer.class), any(Integer.class))).willReturn(false);

            // when & then
            AuthException exception = catchThrowableOfType(() -> service.loginWithRateLimit(request, clientIp), AuthException.class);
            assertAll(

                    () -> assertThat(exception).isNotNull(),

                    () -> assertThat(exception.getErrorCode()).isEqualTo(AuthErrorStatus.RATE_LIMIT_EXCEEDED)

            );

        }
    }

    @Nested
    @DisplayName("reIssueToken 메서드 테스트")
    class ReIssueTokenTest {

        @Test
        @DisplayName("토큰 재발급 성공")
        void reIssueTokenSuccess() {
            // given
            String refreshToken = "old-refresh-token";
            String newAccessToken = "new-access-token";
            String newRefreshToken = "new-refresh-token";

            given(jwtValidatorPort.getUserIdFromToken(refreshToken)).willReturn(1L);
            given(manageRefreshTokenPort.getRefreshToken("1")).willReturn(refreshToken);
            given(jwtValidatorPort.getRoleFromToken(refreshToken)).willReturn(RoleType.ROLE_USER);
            given(jwtGeneratorPort.generateAccessToken(1L, RoleType.ROLE_USER)).willReturn(newAccessToken);
            given(jwtGeneratorPort.generateRefreshToken(1L, RoleType.ROLE_USER)).willReturn(newRefreshToken);
            given(jwtProperties.getAccessTokenExpirationTime()).willReturn(30 * 60 * 1000L);
            given(jwtProperties.getRefreshTokenExpirationTime()).willReturn(7 * 24 * 60 * 60 * 1000L);

            // when
            ReIssueTokenResponse result = service.reIssueToken(refreshToken);

            // then
            assertAll(

                    () -> assertThat(result.accessToken()).isEqualTo(newAccessToken),

                    () -> assertThat(result.refreshToken()).isEqualTo(newRefreshToken),

                    () -> assertThat(result.accessTokenExpiration()).isEqualTo(30 * 60 * 1000L),

                    () -> assertThat(result.refreshTokenExpiration()).isEqualTo(7 * 24 * 60 * 60 * 1000L)

            );

        }

        @Test
        @DisplayName("만료된 리프레시 토큰으로 재발급 실패")
        void reIssueTokenFailWhenUserIdIsNull() {
            // given
            String refreshToken = "expired-refresh-token";

            given(jwtValidatorPort.getUserIdFromToken(refreshToken)).willReturn(null);

            // when & then
            AuthException exception = catchThrowableOfType(() -> service.reIssueToken(refreshToken), AuthException.class);
            assertAll(

                    () -> assertThat(exception).isNotNull(),

                    () -> assertThat(exception.getErrorCode()).isEqualTo(AuthErrorStatus.EXPIRED_REFRESH_TOKEN)

            );

        }

        @Test
        @DisplayName("저장된 리프레시 토큰이 없을 때 재발급 실패")
        void reIssueTokenFailWhenSavedTokenIsNull() {
            // given
            String refreshToken = "refresh-token";

            given(jwtValidatorPort.getUserIdFromToken(refreshToken)).willReturn(1L);
            given(manageRefreshTokenPort.getRefreshToken("1")).willReturn(null);

            // when & then
            AuthException exception = catchThrowableOfType(() -> service.reIssueToken(refreshToken), AuthException.class);
            assertAll(

                    () -> assertThat(exception).isNotNull(),

                    () -> assertThat(exception.getErrorCode()).isEqualTo(AuthErrorStatus.EXPIRED_REFRESH_TOKEN)

            );

        }
    }
}
