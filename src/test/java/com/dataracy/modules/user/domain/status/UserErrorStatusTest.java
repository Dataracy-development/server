package com.dataracy.modules.user.domain.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserErrorStatusTest {

    @Test
    @DisplayName("중복 관련 코드/메시지")
    void duplicatedFields() {
        // given
        // (상태 고정값)

        // when
        // (액션 없음)

        // then
        assertThat(UserErrorStatus.DUPLICATED_NICKNAME.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(UserErrorStatus.DUPLICATED_NICKNAME.getCode()).isEqualTo("USER-004");
        assertThat(UserErrorStatus.DUPLICATED_EMAIL.getCode()).isEqualTo("USER-005");
        assertThat(UserErrorStatus.DUPLICATED_PASSWORD.getCode()).isEqualTo("USER-006");
    }

    @Test
    @DisplayName("비밀번호 변경 불가(소셜 계정) 상태")
    void forbiddenChangePassword_social() {
        // given
        // (상태 고정값)

        // when
        // (액션 없음)

        // then
        assertThat(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_KAKAO.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_GOOGLE.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
