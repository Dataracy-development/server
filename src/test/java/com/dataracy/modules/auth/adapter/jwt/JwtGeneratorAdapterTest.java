package com.dataracy.modules.auth.adapter.jwt;

import com.dataracy.modules.user.domain.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtGeneratorAdapterTest {

    @Mock
    private JwtUtilInternal jwtUtilInternal;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtGeneratorAdapter jwtGeneratorAdapter;

    @BeforeEach
    void setUp() {
        // 각 테스트에서 필요한 경우에만 설정하도록 변경
    }

    @Test
    @DisplayName("generateResetPasswordToken - 비밀번호 재설정 토큰을 생성한다")
    void generateResetPasswordToken_WhenValidEmail_GeneratesToken() {
        // given
        String email = "test@example.com";
        String expectedToken = "reset.token.here";
        when(jwtProperties.getResetTokenExpirationTime()).thenReturn(3600000L);
        when(jwtUtilInternal.generateToken(any(Map.class), anyLong(), any())).thenReturn(expectedToken);

        // when
        String result = jwtGeneratorAdapter.generateResetPasswordToken(email);

        // then
        assertThat(result).isEqualTo(expectedToken);
        verify(jwtUtilInternal).generateToken(
                Map.of("email", email),
                3600000L,
                com.dataracy.modules.auth.domain.enums.TokenType.RESET_PASSWORD
        );
    }

    @Test
    @DisplayName("generateRegisterToken - 레지스터 토큰을 생성한다")
    void generateRegisterToken_WhenValidData_GeneratesToken() {
        // given
        String provider = "google";
        String providerId = "123456789";
        String email = "test@example.com";
        String expectedToken = "register.token.here";
        when(jwtProperties.getRegisterTokenExpirationTime()).thenReturn(1800000L);
        when(jwtUtilInternal.generateToken(any(Map.class), anyLong(), any())).thenReturn(expectedToken);

        // when
        String result = jwtGeneratorAdapter.generateRegisterToken(provider, providerId, email);

        // then
        assertThat(result).isEqualTo(expectedToken);
        verify(jwtUtilInternal).generateToken(
                Map.of("provider", provider, "providerId", providerId, "email", email),
                1800000L,
                com.dataracy.modules.auth.domain.enums.TokenType.REGISTER
        );
    }

    @Test
    @DisplayName("generateAccessToken - 액세스 토큰을 생성한다")
    void generateAccessToken_WhenValidData_GeneratesToken() {
        // given
        Long userId = 1L;
        RoleType role = RoleType.ROLE_USER;
        String expectedToken = "access.token.here";
        when(jwtProperties.getAccessTokenExpirationTime()).thenReturn(900000L);
        when(jwtUtilInternal.generateToken(any(Map.class), anyLong(), any())).thenReturn(expectedToken);

        // when
        String result = jwtGeneratorAdapter.generateAccessToken(userId, role);

        // then
        assertThat(result).isEqualTo(expectedToken);
        verify(jwtUtilInternal).generateToken(
                Map.of("userId", userId, "role", role.getValue()),
                900000L,
                com.dataracy.modules.auth.domain.enums.TokenType.ACCESS
        );
    }

    @Test
    @DisplayName("generateRefreshToken - 리프레시 토큰을 생성한다")
    void generateRefreshToken_WhenValidData_GeneratesToken() {
        // given
        Long userId = 1L;
        RoleType role = RoleType.ROLE_ADMIN;
        String expectedToken = "refresh.token.here";
        when(jwtProperties.getRefreshTokenExpirationTime()).thenReturn(604800000L);
        when(jwtUtilInternal.generateToken(any(Map.class), anyLong(), any())).thenReturn(expectedToken);

        // when
        String result = jwtGeneratorAdapter.generateRefreshToken(userId, role);

        // then
        assertThat(result).isEqualTo(expectedToken);
        verify(jwtUtilInternal).generateToken(
                Map.of("userId", userId, "role", role.getValue()),
                604800000L,
                com.dataracy.modules.auth.domain.enums.TokenType.REFRESH
        );
    }

    @Test
    @DisplayName("generateResetPasswordToken - null 이메일로도 토큰을 생성한다")
    void generateResetPasswordToken_WhenNullEmail_ThrowsException() {
        // given
        String email = null;

        // when & then
        NullPointerException exception = catchThrowableOfType(
                () -> jwtGeneratorAdapter.generateResetPasswordToken(email),
                NullPointerException.class
        );
        assertAll(
                () -> assertThat(exception).isNotNull()
        );
    }

    @Test
    @DisplayName("generateRegisterToken - null 값들로도 토큰을 생성한다")
    void generateRegisterToken_WhenNullValues_ThrowsException() {
        // given
        String provider = null;
        String providerId = null;
        String email = null;

        // when & then
        NullPointerException exception = catchThrowableOfType(
                () -> jwtGeneratorAdapter.generateRegisterToken(provider, providerId, email),
                NullPointerException.class
        );
        assertAll(
                () -> assertThat(exception).isNotNull()
        );
    }
}
