package com.dataracy.modules.user.application.service.query.password;

import com.dataracy.modules.user.application.dto.request.password.ConfirmPasswordRequest;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmPasswordServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserQueryPort userQueryPort;

    @InjectMocks
    private ConfirmPasswordService service;

    private User localUser() {
        return User.builder()
                .id(10L)
                .provider(ProviderType.LOCAL)
                .providerId("local-10")
                .role(RoleType.ROLE_USER)
                .email("u@test.com")
                .password("encoded")
                .nickname("nick")
                .topicIds(Collections.emptyList())
                .isDeleted(false)
                .build();
    }

    @Test
    @DisplayName("confirmPassword: 유저 존재 + 비밀번호 일치 → 성공 (예외 없음)")
    void confirmPassword_success() {
        // given
        given(userQueryPort.findUserById(10L)).willReturn(Optional.of(localUser()));
        given(passwordEncoder.matches("raw","encoded")).willReturn(true);
        ConfirmPasswordRequest req = new ConfirmPasswordRequest("raw");

        // when & then
        assertThatCode(() -> service.confirmPassword(10L, req))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("confirmPassword: 유저 없음 → NOT_FOUND_USER 예외")
    void confirmPassword_userNotFound() {
        // given
        given(userQueryPort.findUserById(99L)).willReturn(Optional.empty());
        ConfirmPasswordRequest req = new ConfirmPasswordRequest("pw");

        // when
        UserException ex = catchThrowableOfType(
                () -> service.confirmPassword(99L, req),
                UserException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
        then(passwordEncoder).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("confirmPassword: 비밀번호 불일치 → FAIL_CONFIRM_PASSWORD 예외")
    void confirmPassword_passwordMismatch() {
        // given
        given(userQueryPort.findUserById(10L)).willReturn(Optional.of(localUser()));
        given(passwordEncoder.matches("wrong","encoded")).willReturn(false);
        ConfirmPasswordRequest req = new ConfirmPasswordRequest("wrong");

        // when
        UserException ex = catchThrowableOfType(
                () -> service.confirmPassword(10L, req),
                UserException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.FAIL_CONFIRM_PASSWORD);
    }
}
