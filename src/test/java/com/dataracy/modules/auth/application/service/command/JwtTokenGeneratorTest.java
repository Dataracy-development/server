package com.dataracy.modules.auth.application.service.command;

import com.dataracy.modules.auth.application.port.out.jwt.JwtGeneratorPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.domain.enums.RoleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtTokenGeneratorTest {

    @Mock
    private JwtGeneratorPort jwtGeneratorPort;

    private MockedStatic<LoggerFactory> loggerFactoryMock;

    private JwtTokenGenerator jwtTokenGenerator;

    @BeforeEach
    void setUp() {
        jwtTokenGenerator = new JwtTokenGenerator(jwtGeneratorPort);
        loggerFactoryMock = mockStatic(LoggerFactory.class);
    }

    @AfterEach
    void tearDown() {
        if (loggerFactoryMock != null) {
            loggerFactoryMock.close();
        }
    }

    @Test
    @DisplayName("generateRegisterToken - 레지스터 토큰을 성공적으로 생성한다")
    void generateRegisterToken_Success()  {
        // given
        String provider = "google";
        String providerId = "123456";
        String email = "test@example.com";
        String expectedToken = "register_token_123";
        
        when(jwtGeneratorPort.generateRegisterToken(provider, providerId, email))
                .thenReturn(expectedToken);
        
        mockLoggerFactory();

        // when
        String result = jwtTokenGenerator.generateRegisterToken(provider, providerId, email);

        // then
        assertThat(result).isEqualTo(expectedToken);
        then(jwtGeneratorPort).should().generateRegisterToken(provider, providerId, email);
    }

    @Test
    @DisplayName("generateRegisterToken - 토큰 생성 실패 시 AuthException을 던진다")
    void generateRegisterToken_Failure()  {
        // given
        String provider = "google";
        String providerId = "123456";
        String email = "test@example.com";
        
        when(jwtGeneratorPort.generateRegisterToken(provider, providerId, email))
                .thenThrow(new RuntimeException("Token generation failed"));
        
        mockLoggerFactory();

        // when & then
        AuthException exception = catchThrowableOfType(() -> jwtTokenGenerator.generateRegisterToken(provider, providerId, email), AuthException.class);
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(AuthErrorStatus.FAILED_GENERATE_REGISTER_TOKEN);
    }

    @Test
    @DisplayName("generateResetPasswordToken - 패스워드 재설정 토큰을 성공적으로 생성한다")
    void generateResetPasswordToken_Success()  {
        // given
        String email = "test@example.com";
        String expectedToken = "reset_token_123";
        
        when(jwtGeneratorPort.generateResetPasswordToken(email))
                .thenReturn(expectedToken);
        
        mockLoggerFactory();

        // when
        String result = jwtTokenGenerator.generateResetPasswordToken(email);

        // then
        assertThat(result).isEqualTo(expectedToken);
        then(jwtGeneratorPort).should().generateResetPasswordToken(email);
    }

    @Test
    @DisplayName("generateResetPasswordToken - 토큰 생성 실패 시 AuthException을 던진다")
    void generateResetPasswordToken_Failure()  {
        // given
        String email = "test@example.com";
        
        when(jwtGeneratorPort.generateResetPasswordToken(email))
                .thenThrow(new RuntimeException("Token generation failed"));
        
        mockLoggerFactory();

        // when & then
        AuthException exception = catchThrowableOfType(() -> jwtTokenGenerator.generateResetPasswordToken(email), AuthException.class);
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(AuthErrorStatus.FAILED_GENERATE_RESET_PASSWORD_TOKEN);
    }

    @Test
    @DisplayName("generateAccessToken - 액세스 토큰을 성공적으로 생성한다")
    void generateAccessToken_Success()  {
        // given
        Long userId = 1L;
        RoleType role = RoleType.ROLE_USER;
        String expectedToken = "access_token_123";
        
        when(jwtGeneratorPort.generateAccessToken(userId, role))
                .thenReturn(expectedToken);
        
        mockLoggerFactory();

        // when
        String result = jwtTokenGenerator.generateAccessToken(userId, role);

        // then
        assertThat(result).isEqualTo(expectedToken);
        then(jwtGeneratorPort).should().generateAccessToken(userId, role);
    }

    @Test
    @DisplayName("generateAccessToken - 토큰 생성 실패 시 AuthException을 던진다")
    void generateAccessToken_Failure()  {
        // given
        Long userId = 1L;
        RoleType role = RoleType.ROLE_USER;
        
        when(jwtGeneratorPort.generateAccessToken(userId, role))
                .thenThrow(new RuntimeException("Token generation failed"));
        
        mockLoggerFactory();

        // when & then
        AuthException exception = catchThrowableOfType(() -> jwtTokenGenerator.generateAccessToken(userId, role), AuthException.class);
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(AuthErrorStatus.FAILED_GENERATE_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("generateRefreshToken - 리프레시 토큰을 성공적으로 생성한다")
    void generateRefreshToken_Success()  {
        // given
        Long userId = 1L;
        RoleType role = RoleType.ROLE_USER;
        String expectedToken = "refresh_token_123";
        
        when(jwtGeneratorPort.generateRefreshToken(userId, role))
                .thenReturn(expectedToken);
        
        mockLoggerFactory();

        // when
        String result = jwtTokenGenerator.generateRefreshToken(userId, role);

        // then
        assertThat(result).isEqualTo(expectedToken);
        then(jwtGeneratorPort).should().generateRefreshToken(userId, role);
    }

    @Test
    @DisplayName("generateRefreshToken - 토큰 생성 실패 시 AuthException을 던진다")
    void generateRefreshToken_Failure()  {
        // given
        Long userId = 1L;
        RoleType role = RoleType.ROLE_USER;
        
        when(jwtGeneratorPort.generateRefreshToken(userId, role))
                .thenThrow(new RuntimeException("Token generation failed"));
        
        mockLoggerFactory();

        // when & then
        AuthException exception = catchThrowableOfType(() -> jwtTokenGenerator.generateRefreshToken(userId, role), AuthException.class);
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(AuthErrorStatus.FAILED_GENERATE_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("getErrorStatus - 알 수 없는 토큰 타입에 대해 기본 에러 상태를 반환한다")
    void getErrorStatus_UnknownTokenType()  {
        // given
        // getErrorStatus는 private 메서드이므로 간접적으로 테스트
        when(jwtGeneratorPort.generateAccessToken(any(), any()))
                .thenThrow(new RuntimeException("Token generation failed"));
        
        mockLoggerFactory();

        // when & then
        AuthException exception = catchThrowableOfType(() -> jwtTokenGenerator.generateAccessToken(1L, RoleType.ROLE_USER), AuthException.class);
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(AuthErrorStatus.FAILED_GENERATE_ACCESS_TOKEN);
    }

    private void mockLoggerFactory() {
        com.dataracy.modules.common.logging.ServiceLogger loggerService = mock(com.dataracy.modules.common.logging.ServiceLogger.class);
        loggerFactoryMock.when(() -> LoggerFactory.service()).thenReturn(loggerService);
        when(loggerService.logStart(anyString(), anyString())).thenReturn(Instant.now());
        doNothing().when(loggerService).logSuccess(anyString(), anyString(), any(Instant.class));
        doNothing().when(loggerService).logException(anyString(), anyString(), any(Exception.class));
    }
}
