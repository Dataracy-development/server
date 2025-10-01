package com.dataracy.modules.auth.application.service.command;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.out.jwt.JwtGeneratorPort;
import com.dataracy.modules.auth.application.port.out.jwt.JwtValidatorPort;
import com.dataracy.modules.auth.application.port.out.token.ManageRefreshTokenPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.user.application.port.in.query.auth.IsLoginPossibleUseCase;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
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
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SelfLoginServiceTest {

    @Mock
    private JwtGeneratorPort jwtGeneratorPort;

    @Mock
    private JwtValidatorPort jwtValidatorPort;

    @Mock
    private ManageRefreshTokenPort manageRefreshTokenPort;

    @Mock
    private IsLoginPossibleUseCase isLoginPossibleUseCase;

    // 실제 필드를 쓰면서도 주입은 자동으로 받기 위해 Spy 사용
    @Spy
    private JwtProperties jwtProperties = new JwtProperties();

    @InjectMocks
    private AuthCommandService service;

    @BeforeEach
    void setup() {
        jwtProperties.setRefreshTokenExpirationTime(1_209_600_000L);
    }

    private UserInfo dummyUserInfo(Long id, String email, RoleType role) {
        return new UserInfo(
                id,
                role,
                email,
                "nickname",
                1L,
                1L,
                Collections.emptyList(),
                1L,
                "https://profile",
                "intro text"
        );
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("이메일/비밀번호 맞으면 RefreshTokenResponse 반환")
        void loginSuccess() {
            // given
            SelfLoginRequest request = new SelfLoginRequest("test@email.com", "password123@");
            UserInfo userInfo = dummyUserInfo(1L, "test@email.com", RoleType.ROLE_USER);

            given(isLoginPossibleUseCase.checkLoginPossibleAndGetUserInfo("test@email.com", "password123@"))
                    .willReturn(userInfo);
            given(jwtGeneratorPort.generateRefreshToken(1L, RoleType.ROLE_USER))
                    .willReturn("issued-refresh");

            // when
            RefreshTokenResponse res = service.login(request);

            // then
            assertAll(

                    () -> assertThat(res.refreshToken()).isEqualTo("issued-refresh"),

                    () -> assertThat(res.refreshTokenExpiration()).isEqualTo(1209600000L)

            );

            then(manageRefreshTokenPort).should().saveRefreshToken("1", "issued-refresh");
        }

        @Test
        @DisplayName("이메일/비밀번호 불일치 → UserException(BAD_REQUEST_LOGIN)")
        void loginInvalid() {
            // given
            SelfLoginRequest request = new SelfLoginRequest("bad@email.com", "wrong");

            given(isLoginPossibleUseCase.checkLoginPossibleAndGetUserInfo("bad@email.com", "wrong"))
                    .willThrow(new  UserException(UserErrorStatus.BAD_REQUEST_LOGIN));

            // when & then
            UserException ex = catchThrowableOfType(() -> service.login(request), UserException.class);
            assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.BAD_REQUEST_LOGIN);
        }

        @Test
        @DisplayName("RefreshToken 발급 실패 → FAILED_GENERATE_REFRESH_TOKEN 발생")
        void loginTokenFail() {
            // given
            SelfLoginRequest request = new SelfLoginRequest("test@email.com", "password123@");
            UserInfo userInfo = dummyUserInfo(1L, "test@email.com", RoleType.ROLE_USER);

            given(isLoginPossibleUseCase.checkLoginPossibleAndGetUserInfo("test@email.com", "password123@"))
                    .willReturn(userInfo);
            given(jwtGeneratorPort.generateRefreshToken(1L, RoleType.ROLE_USER))
                    .willThrow(new  AuthException(AuthErrorStatus.FAILED_GENERATE_REFRESH_TOKEN));

            // when & then
            AuthException ex = catchThrowableOfType(() -> service.login(request), AuthException.class);
            assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.FAILED_GENERATE_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("로그인 (Redis 저장 실패) - Redis 연결 장애 → REDIS_CONNECTION_FAILURE 발생")
        void loginSaveRedisConnectionError() {
            // given
            SelfLoginRequest request = new SelfLoginRequest("test@email.com", "password123@");
            UserInfo userInfo = dummyUserInfo(1L, "test@email.com", RoleType.ROLE_USER);

            given(isLoginPossibleUseCase.checkLoginPossibleAndGetUserInfo("test@email.com", "password123@"))
                    .willReturn(userInfo);
            given(jwtGeneratorPort.generateRefreshToken(1L, RoleType.ROLE_USER))
                    .willReturn("issued-refresh");
            willThrow(new  CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE))
                    .given(manageRefreshTokenPort).saveRefreshToken("1", "issued-refresh");

            // when & then
            CommonException ex = catchThrowableOfType(() -> service.login(request), CommonException.class);
            assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        }

        @Test
        @DisplayName("로그인 (Redis 저장 실패) - 네트워크 장애 → DATA_ACCESS_EXCEPTION 발생")
        void loginSaveDataAccessError() {
            // given
            SelfLoginRequest request = new SelfLoginRequest("test@email.com", "password123@");
            UserInfo userInfo = dummyUserInfo(1L, "test@email.com", RoleType.ROLE_USER);

            given(isLoginPossibleUseCase.checkLoginPossibleAndGetUserInfo("test@email.com", "password123@"))
                    .willReturn(userInfo);
            given(jwtGeneratorPort.generateRefreshToken(1L, RoleType.ROLE_USER))
                    .willReturn("issued-refresh");
            willThrow(new  CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION))
                    .given(manageRefreshTokenPort).saveRefreshToken("1", "issued-refresh");

            // when & then
            CommonException ex = catchThrowableOfType(() -> service.login(request), CommonException.class);
            assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }
}
