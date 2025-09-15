package com.dataracy.modules.auth.application.service.query;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.port.out.jwt.JwtValidatorPort;
import com.dataracy.modules.user.domain.enums.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class JwtQueryServiceTest {

    @Mock
    private JwtValidatorPort jwtValidatorPort;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtQueryService service;

    @Nested
    @DisplayName("토큰 유효성 검사")
    class ValidateToken {

        @Test
        @DisplayName("성공 - 위임 메서드 정상 실행")
        void success() {
            // given
            String token = "valid.jwt.token";

            // when
            service.validateToken(token);

            // then
            then(jwtValidatorPort).should().validateToken(token);
        }

        @Test
        @DisplayName("실패 - 내부 위임 메서드에서 예외 발생 시 전파")
        void fail() {
            // given
            String token = "bad.jwt.token";
            willThrow(new IllegalArgumentException("invalid")).given(jwtValidatorPort).validateToken(token);

            // when
            IllegalArgumentException ex = catchThrowableOfType(
                    () -> service.validateToken(token),
                    IllegalArgumentException.class
            );

            // then
            assertThat(ex).hasMessageContaining("invalid");
        }
    }

    @Test
    @DisplayName("getUserIdFromToken: 토큰에서 userId 추출 성공")
    void getUserIdFromTokenSuccess() {
        given(jwtValidatorPort.getUserIdFromToken("token")).willReturn(42L);

        Long userId = service.getUserIdFromToken("token");

        assertThat(userId).isEqualTo(42L);
    }

    @Test
    @DisplayName("getProviderFromRegisterToken: provider 추출 성공")
    void getProviderFromRegisterTokenSuccess() {
        given(jwtValidatorPort.getProviderFromRegisterToken("token")).willReturn("google");

        String provider = service.getProviderFromRegisterToken("token");

        assertThat(provider).isEqualTo("google");
    }

    @Test
    @DisplayName("getProviderIdFromRegisterToken: providerId 추출 성공")
    void getProviderIdFromRegisterTokenSuccess() {
        given(jwtValidatorPort.getProviderIdFromRegisterToken("token")).willReturn("pid-123");

        String providerId = service.getProviderIdFromRegisterToken("token");

        assertThat(providerId).isEqualTo("pid-123");
    }

    @Test
    @DisplayName("getEmailFromRegisterToken: 이메일 추출 성공")
    void getEmailFromRegisterTokenSuccess() {
        given(jwtValidatorPort.getEmailFromToken("token")).willReturn("user@test.com");

        String email = service.getEmailFromRegisterToken("token");

        assertThat(email).isEqualTo("user@test.com");
    }

    @Test
    @DisplayName("getEmailFromResetToken: 이메일 추출 성공")
    void getEmailFromResetTokenSuccess() {
        given(jwtValidatorPort.getEmailFromToken("resetToken")).willReturn("reset@test.com");

        String email = service.getEmailFromResetToken("resetToken");

        assertThat(email).isEqualTo("reset@test.com");
    }

    @Test
    @DisplayName("getRoleFromToken: 역할 추출 성공")
    void getRoleFromTokenSuccess() {
        given(jwtValidatorPort.getRoleFromToken("token")).willReturn(RoleType.ROLE_ADMIN);

        RoleType role = service.getRoleFromToken("token");

        assertThat(role).isEqualTo(RoleType.ROLE_ADMIN);
    }

    @Test
    @DisplayName("getRegisterTokenExpirationTime: JwtProperties 값 반환")
    void getRegisterTokenExpirationTime() {
        given(jwtProperties.getRegisterTokenExpirationTime()).willReturn(3600L);

        long exp = service.getRegisterTokenExpirationTime();

        assertThat(exp).isEqualTo(3600L);
    }

    @Test
    @DisplayName("getAccessTokenExpirationTime: JwtProperties 값 반환")
    void getAccessTokenExpirationTime() {
        given(jwtProperties.getAccessTokenExpirationTime()).willReturn(1800L);

        long exp = service.getAccessTokenExpirationTime();

        assertThat(exp).isEqualTo(1800L);
    }

    @Test
    @DisplayName("getRefreshTokenExpirationTime: JwtProperties 값 반환")
    void getRefreshTokenExpirationTime() {
        given(jwtProperties.getRefreshTokenExpirationTime()).willReturn(604800L);

        long exp = service.getRefreshTokenExpirationTime();

        assertThat(exp).isEqualTo(604800L);
    }

    @Test
    @DisplayName("getRedirectOnboardingUrl: JwtProperties 값 반환")
    void getRedirectOnboardingUrl() {
        given(jwtProperties.getRedirectOnboarding()).willReturn("http://onboarding");

        String url = service.getRedirectOnboardingUrl();

        assertThat(url).isEqualTo("http://onboarding");
    }

    @Test
    @DisplayName("getRedirectBaseUrl: JwtProperties 값 반환")
    void getRedirectBaseUrl() {
        given(jwtProperties.getRedirectBase()).willReturn("http://base");

        String url = service.getRedirectBaseUrl();

        assertThat(url).isEqualTo("http://base");
    }
}
