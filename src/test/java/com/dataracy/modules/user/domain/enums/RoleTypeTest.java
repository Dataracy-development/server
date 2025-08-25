package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RoleTypeTest {

    @Test
    @DisplayName("of: 대소문자 무시하고 이름/값 매칭")
    void of_shouldReturnEnum_whenValidCaseInsensitive() {
        // given
        String v1 = "ROLE_USER";
        String v2 = "role_admin";
        String v3 = "RoLe_AnOnYmOuS";

        // when
        RoleType byName = RoleType.of(v1);
        RoleType byValueLower = RoleType.of(v2);
        RoleType mixed = RoleType.of(v3);

        // then
        assertThat(byName).isEqualTo(RoleType.ROLE_USER);
        assertThat(byValueLower).isEqualTo(RoleType.ROLE_ADMIN);
        assertThat(mixed).isEqualTo(RoleType.ROLE_ANONYMOUS);
    }

    @Test
    @DisplayName("of: 유효하지 않은 값이면 UserException(INVALID_ROLE_TYPE)")
    void of_shouldThrow_whenInvalid() {
        // given
        String invalid = "MANAGER";

        // when
        UserException ex = catchThrowableOfType(() -> RoleType.of(invalid), UserException.class);

        // then
        assertThat(ex).isNotNull();
        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.INVALID_ROLE_TYPE);
    }
}
