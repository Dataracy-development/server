package com.dataracy.modules.user.application.service.query.validate;

import com.dataracy.modules.user.application.service.validate.UserDuplicateValidator;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateUserServiceTest {

    @Mock UserDuplicateValidator userDuplicateValidator;

    @InjectMocks ValidateUserService service;

    private User userWithProvider(ProviderType provider) {
        return User.builder()
                .id(1L)
                .provider(provider)
                .providerId("pid")
                .email("user@test.com")
                .password("encoded")
                .nickname("nick")
                .topicIds(Collections.emptyList())
                .isDeleted(false)
                .build();
    }

    // ---------------- validateDuplicatedNickname ----------------
    @Test
    @DisplayName("validateDuplicatedNickname: 중복 없음 → 성공 (예외 없음)")
    void validateDuplicatedNickname_success() {
        // given
        willDoNothing().given(userDuplicateValidator).duplicateNickname("nick");

        // when & then
        assertThatCode(() -> service.validateDuplicatedNickname("nick"))
                .doesNotThrowAnyException();
    }

    // ---------------- validateDuplicatedEmail ----------------
    @Test
    @DisplayName("validateDuplicatedEmail: 구글 계정 → DUPLICATED_GOOGLE_EMAIL 예외")
    void validateDuplicatedEmail_google() {
        given(userDuplicateValidator.duplicateEmail("google@test.com"))
                .willReturn(Optional.of(userWithProvider(ProviderType.GOOGLE)));

        UserException ex = catchThrowableOfType(
                () -> service.validateDuplicatedEmail("google@test.com"),
                UserException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.DUPLICATED_GOOGLE_EMAIL);
    }

    @Test
    @DisplayName("validateDuplicatedEmail: 카카오 계정 → DUPLICATED_KAKAO_EMAIL 예외")
    void validateDuplicatedEmail_kakao() {
        given(userDuplicateValidator.duplicateEmail("kakao@test.com"))
                .willReturn(Optional.of(userWithProvider(ProviderType.KAKAO)));

        UserException ex = catchThrowableOfType(
                () -> service.validateDuplicatedEmail("kakao@test.com"),
                UserException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.DUPLICATED_KAKAO_EMAIL);
    }

    @Test
    @DisplayName("validateDuplicatedEmail: 로컬 계정 → DUPLICATED_LOCAL_EMAIL 예외")
    void validateDuplicatedEmail_local() {
        given(userDuplicateValidator.duplicateEmail("local@test.com"))
                .willReturn(Optional.of(userWithProvider(ProviderType.LOCAL)));

        UserException ex = catchThrowableOfType(
                () -> service.validateDuplicatedEmail("local@test.com"),
                UserException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.DUPLICATED_LOCAL_EMAIL);
    }

    @Test
    @DisplayName("validateDuplicatedEmail: 알 수 없는 ProviderType → DUPLICATED_LOCAL_EMAIL 예외")
    void validateDuplicatedEmail_unknown() {
        // enum에 없는 값을 강제로 넣을 순 없으니, null Provider로 테스트
        User user = userWithProvider(null);
        given(userDuplicateValidator.duplicateEmail("unknown@test.com"))
                .willReturn(Optional.of(user));

        UserException ex = catchThrowableOfType(
                () -> service.validateDuplicatedEmail("unknown@test.com"),
                UserException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.DUPLICATED_LOCAL_EMAIL);
    }

    @Test
    @DisplayName("validateDuplicatedEmail: 중복 없음 → 성공 (예외 없음)")
    void validateDuplicatedEmail_success() {
        given(userDuplicateValidator.duplicateEmail("free@test.com"))
                .willReturn(Optional.empty());

        assertThatCode(() -> service.validateDuplicatedEmail("free@test.com"))
                .doesNotThrowAnyException();
    }
}
