/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;

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
  @CsvSource({"INVALID, 'INVALID'", "null, null", "'', ''"})
  @DisplayName("of - 잘못된 입력으로 UserException이 발생한다")
  void of_WhenInvalidInput_ThrowsUserException(String input, String expectedInput) {
    // when & then
    String actualInput = "null".equals(input) ? null : input;
    UserException exception =
        catchThrowableOfType(() -> RoleType.of(actualInput), UserException.class);
    assertThat(exception)
        .isNotNull()
        .hasFieldOrPropertyWithValue("errorCode", UserErrorStatus.INVALID_ROLE_TYPE);
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

  @Test
  @DisplayName("values - 모든 enum 값들을 반환한다")
  void values_ReturnsAllEnumValues() {
    // when
    RoleType[] values = RoleType.values();

    // then
    assertThat(values)
        .hasSize(3)
        .containsExactly(RoleType.ROLE_USER, RoleType.ROLE_ADMIN, RoleType.ROLE_ANONYMOUS);
  }

  @Test
  @DisplayName("valueOf - ROLE_USER 문자열로 ROLE_USER enum을 반환한다")
  void valueOf_WhenRoleUserString_ReturnsRoleUserEnum() {
    // when
    RoleType result = RoleType.valueOf("ROLE_USER");

    // then
    assertThat(result).isEqualTo(RoleType.ROLE_USER);
  }

  @Test
  @DisplayName("valueOf - ROLE_ADMIN 문자열로 ROLE_ADMIN enum을 반환한다")
  void valueOf_WhenRoleAdminString_ReturnsRoleAdminEnum() {
    // when
    RoleType result = RoleType.valueOf("ROLE_ADMIN");

    // then
    assertThat(result).isEqualTo(RoleType.ROLE_ADMIN);
  }
}
