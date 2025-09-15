package com.dataracy.modules.user.domain.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

class UserSuccessStatusTest {

    @Test
    @DisplayName("CREATED_USER: 코드/메시지 검증")
    void createdUserFields() {
        // given
        UserSuccessStatus status = UserSuccessStatus.CREATED_USER;

        // when
        String code = status.getCode();
        String message = status.getMessage();

        // then
        assertThat(code).isEqualTo("201");
        assertThat(message).contains("회원가입");
    }

    @Test
    @DisplayName("성공 검증")
    void okGroupCodeFields() {
        assertThat(UserSuccessStatus.OK_GET_USER_INFO.getCode()).isEqualTo("200");
        assertThat(UserSuccessStatus.OK_NOT_DUPLICATED_NICKNAME.getCode()).isEqualTo("200");
        assertThat(UserSuccessStatus.OK_CHANGE_PASSWORD.getCode()).isEqualTo("200");
        assertThat(UserSuccessStatus.OK_RESET_PASSWORD.getCode()).isEqualTo("200");
        assertThat(UserSuccessStatus.OK_CONFIRM_PASSWORD.getCode()).isEqualTo("200");
    }
}
