package com.dataracy.modules.auth.application.service.command;

import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.user.domain.enums.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtCommandServiceTest {

    @Mock
    private JwtTokenGenerator jwtTokenGenerator;

    @InjectMocks
    private JwtCommandService service;

    @Nested
    @DisplayName("generateRegisterToken")
    class GenerateRegisterToken {

        @Test
        @DisplayName("성공 - 토큰 생성 성공 시 그대로 반환")
        void success() {
            // given
            given(jwtTokenGenerator.generateRegisterToken("google", "pid-123", "user@test.com"))
                    .willReturn("registerToken");

            // when
            String result = service.generateRegisterToken("google", "pid-123", "user@test.com");

            // then
            assertThat(result).isEqualTo("registerToken");
        }

        @Test
        @DisplayName("실패 - 내부 에러 발생 시 AuthException 반환")
        void fail() {
            // given
            given(jwtTokenGenerator.generateRegisterToken(any(), any(), any()))
                    .willThrow(new  AuthException(AuthErrorStatus.FAILED_GENERATE_REGISTER_TOKEN));

            // when
            AuthException ex = catchThrowableOfType(
                    () -> service.generateRegisterToken("google", "pid-123", "user@test.com"),
                    AuthException.class
            );

            // then
            assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.FAILED_GENERATE_REGISTER_TOKEN);
        }
    }

    @Nested
    @DisplayName("generateResetPasswordToken")
    class GenerateResetPasswordToken {

        @Test
        @DisplayName("성공 - 토큰 생성 성공 시 그대로 반환")
        void success() {
            given(jwtTokenGenerator.generateResetPasswordToken("user@test.com"))
                    .willReturn("resetToken");

            String result = service.generateResetPasswordToken("user@test.com");

            assertThat(result).isEqualTo("resetToken");
        }

        @Test
        @DisplayName("실패 - 내부 에러 발생 시 AuthException 반환")
        void fail() {
            given(jwtTokenGenerator.generateResetPasswordToken(any()))
                    .willThrow(new  AuthException(AuthErrorStatus.FAILED_GENERATE_RESET_PASSWORD_TOKEN));

            AuthException ex = catchThrowableOfType(
                    () -> service.generateResetPasswordToken("user@test.com"),
                    AuthException.class
            );

            assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.FAILED_GENERATE_RESET_PASSWORD_TOKEN);
        }
    }

    @Nested
    @DisplayName("generateAccessToken")
    class GenerateAccessToken {

        @Test
        @DisplayName("성공 - 토큰 생성 성공 시 그대로 반환")
        void success() {
            given(jwtTokenGenerator.generateAccessToken(1L, RoleType.ROLE_USER))
                    .willReturn("accessToken");

            String result = service.generateAccessToken(1L, RoleType.ROLE_USER);

            assertThat(result).isEqualTo("accessToken");
        }

        @Test
        @DisplayName("실패 - 내부 에러 발생 시 AuthException 반환")
        void fail() {
            given(jwtTokenGenerator.generateAccessToken(anyLong(), any()))
                    .willThrow(new  AuthException(AuthErrorStatus.FAILED_GENERATE_ACCESS_TOKEN));

            AuthException ex = catchThrowableOfType(
                    () -> service.generateAccessToken(1L, RoleType.ROLE_USER),
                    AuthException.class
            );

            assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.FAILED_GENERATE_ACCESS_TOKEN);
        }
    }

    @Nested
    @DisplayName("generateRefreshToken")
    class GenerateRefreshToken {

        @Test
        @DisplayName("성공 - 토큰 생성 성공 시 그대로 반환")
        void success() {
            given(jwtTokenGenerator.generateRefreshToken(1L, RoleType.ROLE_USER))
                    .willReturn("refreshToken");

            String result = service.generateRefreshToken(1L, RoleType.ROLE_USER);

            assertThat(result).isEqualTo("refreshToken");
        }

        @Test
        @DisplayName("실패 - 내부 에러 발생 시 AuthException 반환")
        void fail() {
            given(jwtTokenGenerator.generateRefreshToken(anyLong(), any()))
                    .willThrow(new  AuthException(AuthErrorStatus.FAILED_GENERATE_REFRESH_TOKEN));

            AuthException ex = catchThrowableOfType(
                    () -> service.generateRefreshToken(1L, RoleType.ROLE_USER),
                    AuthException.class
            );

            assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.FAILED_GENERATE_REFRESH_TOKEN);
        }
    }
}
