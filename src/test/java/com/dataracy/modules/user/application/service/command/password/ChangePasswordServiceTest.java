package com.dataracy.modules.user.application.service.command.password;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.in.token.ManageResetTokenUseCase;
import com.dataracy.modules.user.application.dto.request.password.ChangePasswordRequest;
import com.dataracy.modules.user.application.dto.request.password.ResetPasswordWithTokenRequest;
import com.dataracy.modules.user.application.port.out.command.UserCommandPort;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChangePasswordServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder
            ;
    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private UserCommandPort userCommandPort;

    @Mock
    private ManageResetTokenUseCase manageResetTokenUseCase;

    @Mock
    private JwtValidateUseCase jwtValidateUseCase;

    @InjectMocks
    ChangePasswordService service;

    private User localUser(ProviderType providerType) {
        return User.builder()
                .id(10L)
                .provider(providerType)
                .providerId("local-10")
                .role(RoleType.ROLE_USER)
                .email("u@test.com")
                .password("encoded")
                .nickname("nick")
                .authorLevelId(null)
                .occupationId(null)
                .topicIds(Collections.emptyList())
                .visitSourceId(null)
                .profileImageUrl(null)
                .introductionText(null)
                .isAdTermsAgreed(false)
                .isDeleted(false)
                .build();
    }

    // ================== changePassword ==================

    @Test
    @DisplayName("changePassword: LOCAL 유저 + 비밀번호 일치 → 성공 저장")
    void changePassword_success() {
        // given
        ChangePasswordRequest req = new ChangePasswordRequest("newPwd", "newPwd");
        given(userQueryPort.findUserById(10L)).willReturn(Optional.of(localUser(ProviderType.LOCAL)));
        given(passwordEncoder.encode("newPwd")).willReturn("encodedNew");

        // when
        service.changePassword(10L, req);

        // then
        then(userCommandPort).should().changePassword(10L, "encodedNew");
    }

    @Test
    @DisplayName("changePassword: 존재하지 않는 유저 → NOT_FOUND_USER 예외")
    void changePassword_userNotFound() {
        // given
        given(userQueryPort.findUserById(99L)).willReturn(Optional.empty());

        // when
        UserException ex = catchThrowableOfType(
                () -> service.changePassword(99L, new ChangePasswordRequest("pw","pw")),
                UserException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
        then(userCommandPort).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("changePassword: 소셜 계정(KAKAO/GOOGLE) → FORBIDDEN_CHANGE_PASSWORD_* 예외")
    void changePassword_socialForbidden() {
        // given
        User kakaoUser = localUser(ProviderType.KAKAO);
        given(userQueryPort.findUserById(10L)).willReturn(Optional.of(kakaoUser));

        // when
        UserException ex = catchThrowableOfType(
                () -> service.changePassword(10L, new ChangePasswordRequest("pw","pw")),
                UserException.class
        );

        // then
        assertThat(ex.getErrorCode()).isIn(
                UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_KAKAO,
                UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_GOOGLE
        );
        then(userCommandPort).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("changePassword: 비밀번호-확인 불일치 → validatePasswordMatch() 예외 전파")
    void changePassword_passwordMismatch() {
        // given
        ChangePasswordRequest req = new ChangePasswordRequest("pw1", "pw2");

        // when & then
        assertThatThrownBy(() -> service.changePassword(10L, req))
                .isInstanceOf(UserException.class);
        then(userQueryPort).shouldHaveNoInteractions();
        then(userCommandPort).shouldHaveNoInteractions();
    }

    // ================== resetPassword ==================

    @Nested
    class ResetPasswordTests {

        @Test
        @DisplayName("resetPassword: 유효 토큰 + LOCAL 유저 → 성공 저장")
        void resetPassword_success() {
            // given
            ResetPasswordWithTokenRequest req =
                    new ResetPasswordWithTokenRequest("reset-token","pw","pw");
            given(manageResetTokenUseCase.isValidResetToken("reset-token")).willReturn(true);
            given(jwtValidateUseCase.getEmailFromResetToken("reset-token")).willReturn("u@test.com");
            given(userQueryPort.findUserByEmail("u@test.com")).willReturn(Optional.of(localUser(ProviderType.LOCAL)));
            given(passwordEncoder.encode("pw")).willReturn("encodedNew");

            // when
            service.resetPassword(req);

            // then
            then(userCommandPort).should().changePassword(10L,"encodedNew");
        }

        @Test
        @DisplayName("resetPassword: 무효 토큰 → INVALID_OR_EXPIRED_RESET_PASSWORD_TOKEN")
        void resetPassword_invalidToken() {
            // given
            ResetPasswordWithTokenRequest req =
                    new ResetPasswordWithTokenRequest("bad","pw","pw");
            given(manageResetTokenUseCase.isValidResetToken("bad")).willReturn(false);

            // when
            UserException ex = catchThrowableOfType(() -> service.resetPassword(req), UserException.class);

            // then
            assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.INVALID_OR_EXPIRED_RESET_PASSWORD_TOKEN);
            then(userQueryPort).shouldHaveNoInteractions();
            then(userCommandPort).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("resetPassword: 유저 없음 → NOT_FOUND_USER 예외")
        void resetPassword_userNotFound() {
            // given
            ResetPasswordWithTokenRequest req =
                    new ResetPasswordWithTokenRequest("token","pw","pw");
            given(manageResetTokenUseCase.isValidResetToken("token")).willReturn(true);
            given(jwtValidateUseCase.getEmailFromResetToken("token")).willReturn("x@test.com");
            given(userQueryPort.findUserByEmail("x@test.com")).willReturn(Optional.empty());

            // when
            UserException ex = catchThrowableOfType(() -> service.resetPassword(req), UserException.class);

            // then
            assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
        }

        @Test
        @DisplayName("resetPassword: 소셜 계정 → FORBIDDEN_CHANGE_PASSWORD_* 예외")
        void resetPassword_socialForbidden() {
            // given
            ResetPasswordWithTokenRequest req =
                    new ResetPasswordWithTokenRequest("token","pw","pw");
            User kakao = localUser(ProviderType.KAKAO);
            given(manageResetTokenUseCase.isValidResetToken("token")).willReturn(true);
            given(jwtValidateUseCase.getEmailFromResetToken("token")).willReturn("u@test.com");
            given(userQueryPort.findUserByEmail("u@test.com")).willReturn(Optional.of(kakao));

            // when
            UserException ex = catchThrowableOfType(() -> service.resetPassword(req), UserException.class);

            // then
            assertThat(ex.getErrorCode()).isIn(
                    UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_KAKAO,
                    UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_GOOGLE
            );
            then(userCommandPort).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("resetPassword: 비밀번호-확인 불일치 → validatePasswordMatch() 예외 전파")
        void resetPassword_passwordMismatch() {
            // given
            ResetPasswordWithTokenRequest req =
                    new ResetPasswordWithTokenRequest("token","pw1","pw2");
            given(manageResetTokenUseCase.isValidResetToken("token")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> service.resetPassword(req))
                    .isInstanceOf(UserException.class);
            then(userCommandPort).shouldHaveNoInteractions();
        }
    }
}
