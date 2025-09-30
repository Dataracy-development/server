package com.dataracy.modules.user.application.service.query.auth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RegisterTokenResponse;
import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.in.token.ManageRefreshTokenUseCase;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;
@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private JwtValidateUseCase jwtValidateUseCase;

    @Mock
    private JwtGenerateUseCase jwtGenerateUseCase;

    @Mock
    private ManageRefreshTokenUseCase manageRefreshTokenUseCase;

    @InjectMocks
    private UserAuthService service;

    private OAuthUserInfo kakaoUserInfo() {
        return new OAuthUserInfo("user@test.com", "주니", "KAKAO","kakao-123");
    }

    private User localUser() {
        return User.builder()
                .id(1L)
                .provider(ProviderType.LOCAL)
                .providerId("local-1")
                .role(RoleType.ROLE_USER)
                .email("user@test.com")
                .password("encoded")
                .nickname("nick")
                .isDeleted(false)
                .build();
    }

    // -------------------- isNewUser --------------------

    @Test
    @DisplayName("isNewUser: 존재하지 않으면 true 반환")
    void isNewUserReturnsTrueWhenNotExists() {
        // given
        given(userQueryPort.findUserByProviderId("kakao-123")).willReturn(Optional.empty());

        // when
        boolean result = service.isNewUser(kakaoUserInfo());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isNewUser: 이미 존재하면 false 반환")
    void isNewUserReturnsFalseWhenExists() {
        // given
        given(userQueryPort.findUserByProviderId("kakao-123")).willReturn(Optional.of(localUser()));

        // when
        boolean result = service.isNewUser(kakaoUserInfo());

        // then
        assertThat(result).isFalse();
    }

    // -------------------- handleNewUser --------------------

    @Test
    @DisplayName("handleNewUser: 토큰 생성 및 RegisterTokenResponse 반환")
    void handleNewUserGeneratesRegisterToken() {
        // given
        given(jwtGenerateUseCase.generateRegisterToken("KAKAO","kakao-123","user@test.com"))
                .willReturn("register-token");
        given(jwtValidateUseCase.getRegisterTokenExpirationTime()).willReturn(1000L);

        // when
        RegisterTokenResponse res = service.handleNewUser(kakaoUserInfo());

        // then
        assertThat(res.registerToken()).isEqualTo("register-token");
        assertThat(res.registerTokenExpiration()).isEqualTo(1000L);
    }

    // -------------------- handleExistingUser --------------------

    @Test
    @DisplayName("handleExistingUser: 유저 존재 시 refresh 토큰 발급/저장")
    void handleExistingUserGeneratesAndSavesRefreshToken() {
        // given
        User exist = localUser();
        given(userQueryPort.findUserByProviderId("kakao-123")).willReturn(Optional.of(exist));
        given(jwtGenerateUseCase.generateRefreshToken(1L, RoleType.ROLE_USER)).willReturn("refresh-token");
        given(jwtValidateUseCase.getRefreshTokenExpirationTime()).willReturn(1000L);

        // when
        RefreshTokenResponse res = service.handleExistingUser(kakaoUserInfo());

        // then
        assertThat(res.refreshToken()).isEqualTo("refresh-token");
        then(manageRefreshTokenUseCase).should().saveRefreshToken("1","refresh-token");
    }

    @Test
    @DisplayName("handleExistingUser: 유저 없음 → NOT_FOUND_USER 예외")
    void handleExistingUserThrowsNotFoundWhenUserMissing() {
        // given
        given(userQueryPort.findUserByProviderId("kakao-123")).willReturn(Optional.empty());
        OAuthUserInfo userInfo = kakaoUserInfo();

        // when
        UserException ex = catchThrowableOfType(
                () -> service.handleExistingUser(userInfo),
                UserException.class
        );

        // then
        assertThat(ex).isNotNull();
        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
        then(manageRefreshTokenUseCase).shouldHaveNoInteractions();
    }

    // -------------------- checkLoginPossibleAndGetUserInfo --------------------

    @Test
    @DisplayName("checkLoginPossibleAndGetUserInfo: 이메일 존재 + 비밀번호 일치 → UserInfo 반환")
    void checkLoginPossibleReturnsUserInfoWhenValid() {
        // given
        User user = localUser();
        given(userQueryPort.findUserByEmail("user@test.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("raw","encoded")).willReturn(true);

        // when
        UserInfo res = service.checkLoginPossibleAndGetUserInfo("user@test.com","raw");

        // then
        assertThat(res.email()).isEqualTo("user@test.com");
    }

    @Test
    @DisplayName("checkLoginPossibleAndGetUserInfo: 유저 없음 → BAD_REQUEST_LOGIN 예외")
    void checkLoginPossibleThrowsWhenUserNotFound() {
        // given
        given(userQueryPort.findUserByEmail("x@test.com")).willReturn(Optional.empty());

        // when
        UserException ex = catchThrowableOfType(
                () -> service.checkLoginPossibleAndGetUserInfo("x@test.com","pw"),
                UserException.class
        );

        // then
        assertThat(ex).isNotNull();
        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.BAD_REQUEST_LOGIN);
    }

    @Test
    @DisplayName("checkLoginPossibleAndGetUserInfo: 비밀번호 불일치 → BAD_REQUEST_LOGIN 예외")
    void checkLoginPossibleThrowsWhenPasswordMismatch() {
        // given
        User user = localUser();
        given(userQueryPort.findUserByEmail("user@test.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong","encoded")).willReturn(false);

        // when
        UserException ex = catchThrowableOfType(
                () -> service.checkLoginPossibleAndGetUserInfo("user@test.com","wrong"),
                UserException.class
        );

        // then
        assertThat(ex).isNotNull();
        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.BAD_REQUEST_LOGIN);
    }
}
