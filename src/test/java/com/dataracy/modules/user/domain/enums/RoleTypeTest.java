package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

class RoleTypeTest {

    @Test
    @DisplayName("of - ROLE_USER 문자열로 ROLE_USER enum을 반환한다")
    void of_WhenRoleUserString_ReturnsRoleUserEnum() {
        // when
        RoleType result = RoleType.of("ROLE_USER");

        // then
        assertThat(result).isEqualTo(RoleType.ROLE_USER);
    }

    @Test
    @DisplayName("of - role_user 소문자로 ROLE_USER enum을 반환한다")
    void of_WhenRoleUserLowerCase_ReturnsRoleUserEnum() {
        // when
        RoleType result = RoleType.of("role_user");

        // then
        assertThat(result).isEqualTo(RoleType.ROLE_USER);
    }

    @Test
    @DisplayName("of - ROLE_ADMIN 문자열로 ROLE_ADMIN enum을 반환한다")
    void of_WhenRoleAdminString_ReturnsRoleAdminEnum() {
        // when
        RoleType result = RoleType.of("ROLE_ADMIN");

        // then
        assertThat(result).isEqualTo(RoleType.ROLE_ADMIN);
    }

    @Test
    @DisplayName("of - role_admin 소문자로 ROLE_ADMIN enum을 반환한다")
    void of_WhenRoleAdminLowerCase_ReturnsRoleAdminEnum() {
        // when
        RoleType result = RoleType.of("role_admin");

        // then
        assertThat(result).isEqualTo(RoleType.ROLE_ADMIN);
    }

    @Test
    @DisplayName("of - ROLE_ANONYMOUS 문자열로 ROLE_ANONYMOUS enum을 반환한다")
    void of_WhenRoleAnonymousString_ReturnsRoleAnonymousEnum() {
        // when
        RoleType result = RoleType.of("ROLE_ANONYMOUS");

        // then
        assertThat(result).isEqualTo(RoleType.ROLE_ANONYMOUS);
    }

    @Test
    @DisplayName("of - role_anonymous 소문자로 ROLE_ANONYMOUS enum을 반환한다")
    void of_WhenRoleAnonymousLowerCase_ReturnsRoleAnonymousEnum() {
        // when
        RoleType result = RoleType.of("role_anonymous");

        // then
        assertThat(result).isEqualTo(RoleType.ROLE_ANONYMOUS);
    }

    @Test
    @DisplayName("of - 유효하지 않은 문자열로 UserException이 발생한다")
    void of_WhenInvalidString_ThrowsUserException() {
        // when & then
        UserException exception = catchThrowableOfType(
                () -> RoleType.of("INVALID"),
                UserException.class
        );
        assertAll(
                () -> org.assertj.core.api.Assertions.assertThat(exception).isNotNull(),
                () -> org.assertj.core.api.Assertions.assertThat(exception).hasFieldOrPropertyWithValue("errorCode", UserErrorStatus.INVALID_ROLE_TYPE)
        );
    }

    @Test
    @DisplayName("of - null로 UserException이 발생한다")
    void of_WhenNull_ThrowsUserException() {
        // when & then
        UserException exception = catchThrowableOfType(
                () -> RoleType.of(null),
                UserException.class
        );
        assertAll(
                () -> org.assertj.core.api.Assertions.assertThat(exception).isNotNull(),
                () -> org.assertj.core.api.Assertions.assertThat(exception).hasFieldOrPropertyWithValue("errorCode", UserErrorStatus.INVALID_ROLE_TYPE)
        );
    }

    @Test
    @DisplayName("of - 빈 문자열로 UserException이 발생한다")
    void of_WhenEmptyString_ThrowsUserException() {
        // when & then
        UserException exception = catchThrowableOfType(
                () -> RoleType.of(""),
                UserException.class
        );
        assertAll(
                () -> org.assertj.core.api.Assertions.assertThat(exception).isNotNull(),
                () -> org.assertj.core.api.Assertions.assertThat(exception).hasFieldOrPropertyWithValue("errorCode", UserErrorStatus.INVALID_ROLE_TYPE)
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