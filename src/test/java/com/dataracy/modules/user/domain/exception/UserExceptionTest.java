package com.dataracy.modules.user.domain.exception;

import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserExceptionTest {

    @Test
    @DisplayName("UserException: 에러코드 보존")
    void constructorShouldKeepErrorCode() {
        // given
        UserErrorStatus status = UserErrorStatus.NOT_FOUND_USER;

        // when
        UserException ex = new UserException(status);

        // then
        assertThat(ex.getErrorCode()).isEqualTo(status);
    }
}
