package com.dataracy.modules.user.domain.exception;

import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserExceptionTest {

    @Test
    @DisplayName("UserException: 에러코드 보존")
    void constructor_shouldKeepErrorCode() {
        // given
        UserErrorStatus status = UserErrorStatus.NOT_FOUND_USER;

        // when
        UserException ex = new UserException(status);

        // then
        assertThat(ex.getErrorCode()).isEqualTo(status);
    }
}
