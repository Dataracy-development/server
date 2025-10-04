package com.dataracy.modules.user.domain.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    assertAll(() -> assertThat(code).isEqualTo("201"), () -> assertThat(message).contains("회원가입"));
  }

  @Test
  @DisplayName("성공 검증")
  void okGroupCodeFields() {
    assertAll(
        () -> assertThat(UserSuccessStatus.OK_GET_USER_INFO.getCode()).isEqualTo("200"),
        () -> assertThat(UserSuccessStatus.OK_NOT_DUPLICATED_NICKNAME.getCode()).isEqualTo("200"),
        () -> assertThat(UserSuccessStatus.OK_CHANGE_PASSWORD.getCode()).isEqualTo("200"),
        () -> assertThat(UserSuccessStatus.OK_RESET_PASSWORD.getCode()).isEqualTo("200"),
        () -> assertThat(UserSuccessStatus.OK_CONFIRM_PASSWORD.getCode()).isEqualTo("200"));
  }
}
