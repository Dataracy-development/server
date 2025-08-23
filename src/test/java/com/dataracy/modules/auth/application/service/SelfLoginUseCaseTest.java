package com.dataracy.modules.auth.application.service;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.out.jwt.JwtGeneratorPort;
import com.dataracy.modules.auth.application.port.out.jwt.JwtValidatorPort;
import com.dataracy.modules.auth.application.port.out.token.ManageRefreshTokenPort;
import com.dataracy.modules.auth.application.service.command.AuthCommandService;
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
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

class SelfLoginUseCaseTest {

    private JwtGeneratorPort jwtGeneratorPort;
    private JwtValidatorPort jwtValidatorPort; // 여기선 사용 안 함
    private ManageRefreshTokenPort manageRefreshTokenPort;
    private IsLoginPossibleUseCase isLoginPossibleUseCase;
    private JwtProperties jwtProperties;

    private AuthCommandService service;

    @BeforeEach
    void setup() {
        jwtGeneratorPort = mock(JwtGeneratorPort.class);
        jwtValidatorPort = mock(JwtValidatorPort.class);
        manageRefreshTokenPort = mock(ManageRefreshTokenPort.class);
        isLoginPossibleUseCase = mock(IsLoginPossibleUseCase.class);

        jwtProperties = new JwtProperties();
        jwtProperties.setRefreshTokenExpirationTime(1209600000L);

        service = new AuthCommandService(
                jwtProperties,
                jwtGeneratorPort,
                jwtValidatorPort,
                manageRefreshTokenPort,
                isLoginPossibleUseCase
        );
    }

    private UserInfo dummyUserInfo(Long id, String email, RoleType role) {
        return new UserInfo(
                id,
                role,
                email,
                "nickname",     // nickname
                1L,             // authorLevelId
                1L,             // occupationId
                Collections.emptyList(), // topicIds
                1L,             // visitSourceId
                "profile.png",  // profileImageUrl
                "intro text"    // introductionText
        );
    }

    @Test
    @DisplayName("이메일/비밀번호 맞으면 RefreshTokenResponse 반환")
    void loginSuccess() {
        SelfLoginRequest request = new SelfLoginRequest("test@email.com", "pw");
        UserInfo userInfo = dummyUserInfo(1L, "test@email.com", RoleType.ROLE_USER);

        given(isLoginPossibleUseCase.checkLoginPossibleAndGetUserInfo("test@email.com", "pw"))
                .willReturn(userInfo);
        given(jwtGeneratorPort.generateRefreshToken(1L, RoleType.ROLE_USER)).willReturn("refresh-123");

        RefreshTokenResponse res = service.login(request);

        assertThat(res.refreshToken()).isEqualTo("refresh-123");
        assertThat(res.refreshTokenExpiration()).isEqualTo(1209600000L);
        then(manageRefreshTokenPort).should().saveRefreshToken("1", "refresh-123");
    }

    @Test
    @DisplayName("이메일/비밀번호 불일치 → UserException(BAD_REQUEST_LOGIN)")
    void loginInvalid() {
        SelfLoginRequest request = new SelfLoginRequest("bad@email.com", "wrong");

        given(isLoginPossibleUseCase.checkLoginPossibleAndGetUserInfo("bad@email.com", "wrong"))
                .willThrow(new UserException(UserErrorStatus.BAD_REQUEST_LOGIN));

        UserException ex = catchThrowableOfType(
                () -> service.login(request),
                UserException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.BAD_REQUEST_LOGIN);
    }

    @Test
    @DisplayName("RefreshToken 발급 실패 → FAILED_GENERATE_REFRESH_TOKEN 발생")
    void loginTokenFail() {
        SelfLoginRequest request = new SelfLoginRequest("test@email.com", "pw");
        UserInfo userInfo = dummyUserInfo(1L, "test@email.com", RoleType.ROLE_USER);

        given(isLoginPossibleUseCase.checkLoginPossibleAndGetUserInfo("test@email.com", "pw"))
                .willReturn(userInfo);
        given(jwtGeneratorPort.generateRefreshToken(1L, RoleType.ROLE_USER))
                .willThrow(new AuthException(AuthErrorStatus.FAILED_GENERATE_REFRESH_TOKEN));

        AuthException ex = catchThrowableOfType(
                () -> service.login(request),
                AuthException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.FAILED_GENERATE_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("로그인 (Redis 저장 실패) - Redis 연결 장애 → REDIS_CONNECTION_FAILURE 발생")
    void loginSaveRedisConnectionError() {
        SelfLoginRequest request = new SelfLoginRequest("test@email.com", "pw");
        UserInfo userInfo = dummyUserInfo(1L, "test@email.com", RoleType.ROLE_USER);

        given(isLoginPossibleUseCase.checkLoginPossibleAndGetUserInfo("test@email.com", "pw"))
                .willReturn(userInfo);
        given(jwtGeneratorPort.generateRefreshToken(1L, RoleType.ROLE_USER)).willReturn("refresh-123");
        willThrow(new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE))
                .given(manageRefreshTokenPort).saveRefreshToken("1", "refresh-123");

        // when & then
        CommonException ex = catchThrowableOfType(
                () -> service.login(request),
                CommonException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
    }

    @Test
    @DisplayName("로그인 (Redis 저장 실패) - 네트워크 장애 → DATA_ACCESS_EXCEPTION 발생")
    void loginSaveDataAccessError() {
        SelfLoginRequest request = new SelfLoginRequest("test@email.com", "pw");
        UserInfo userInfo = dummyUserInfo(1L, "test@email.com", RoleType.ROLE_USER);

        given(isLoginPossibleUseCase.checkLoginPossibleAndGetUserInfo("test@email.com", "pw"))
                .willReturn(userInfo);
        given(jwtGeneratorPort.generateRefreshToken(1L, RoleType.ROLE_USER)).willReturn("refresh-123");
        willThrow(new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION))
                .given(manageRefreshTokenPort).saveRefreshToken("1", "refresh-123");

        // when & then
        CommonException ex = catchThrowableOfType(
                () -> service.login(request),
                CommonException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
    }
}
