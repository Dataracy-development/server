package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

class RoleTypeTest {

    @ParameterizedTest(name = "of - {0} 문자열로 {1} enum을 반환한다")
    @CsvSource({
            "ROLE_USER, ROLE_USER",
            "role_user, ROLE_USER",
            "ROLE_ADMIN, ROLE_ADMIN",
            "role_admin, ROLE_ADMIN",
            "ROLE_ANONYMOUS, ROLE_ANONYMOUS",
            "role_anonymous, ROLE_ANONYMOUS"
    })
    @DisplayName("of - 문자열로 해당 enum을 반환한다")
    void of_WhenValidString_ReturnsCorrespondingEnum(String input, String expectedEnumName) {
        // when
        RoleType result = RoleType.of(input);

        // then
        assertThat(result.name()).isEqualTo(expectedEnumName);
    }

    @ParameterizedTest(name = "of - {0}로 UserException이 발생한다")
    @CsvSource({
            "INVALID, 'INVALID'",
            "null, null",
            "'', ''"
    })
    @DisplayName("of - 잘못된 입력으로 UserException이 발생한다")
    void of_WhenInvalidInput_ThrowsUserException(String input, String expectedInput) {
        // when & then
        UserException exception = catchThrowableOfType(
                () -> RoleType.of("null".equals(input) ? null : input),
                UserException.class
        );
        assertAll(
                () -> assertThat(exception).isNotNull(),
                () -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", UserErrorStatus.INVALID_ROLE_TYPE)
        );
    }

    @Test
    @DisplayName("getValue - ROLE_USER의 value를 반환한다")
    void getValue_WhenRoleUser_ReturnsRoleUserValue() {
        // when
        String result = RoleType.ROLE_USER.getValue();

        // then
        assertThat(result).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("getValue - ROLE_ADMIN의 value를 반환한다")
    void getValue_WhenRoleAdmin_ReturnsRoleAdminValue() {
        // when
        String result = RoleType.ROLE_ADMIN.getValue();

        // then
        assertThat(result).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("getValue - ROLE_ANONYMOUS의 value를 반환한다")
    void getValue_WhenRoleAnonymous_ReturnsRoleAnonymousValue() {
        // when
        String result = RoleType.ROLE_ANONYMOUS.getValue();

        // then
        assertThat(result).isEqualTo("ROLE_ANONYMOUS");
    }
}