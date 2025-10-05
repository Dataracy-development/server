package com.dataracy.modules.user.adapter.web.mapper.password;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.user.adapter.web.request.password.ChangePasswordWebRequest;
import com.dataracy.modules.user.adapter.web.request.password.ConfirmPasswordWebRequest;
import com.dataracy.modules.user.adapter.web.request.password.ResetPasswordWithTokenWebRequest;
import com.dataracy.modules.user.application.dto.request.password.ChangePasswordRequest;
import com.dataracy.modules.user.application.dto.request.password.ConfirmPasswordRequest;
import com.dataracy.modules.user.application.dto.request.password.ResetPasswordWithTokenRequest;

class UserPasswordWebMapperTest {

  private UserPasswordWebMapper mapper = new UserPasswordWebMapper();

  @Test
  @DisplayName("toApplicationDto(Change): 웹 → 앱 매핑")
  void toApplicationDtochange() {
    // given
    ChangePasswordWebRequest web = new ChangePasswordWebRequest("pw", "pw");

    // when
    ChangePasswordRequest dto = mapper.toApplicationDto(web);

    // then
    assertAll(
        () -> assertThat(dto.password()).isEqualTo("pw"),
        () -> assertThat(dto.passwordConfirm()).isEqualTo("pw"));
  }

  @Test
  @DisplayName("toApplicationDto(ResetWithToken): 웹 → 앱 매핑")
  void toApplicationDtoreset() {
    // given
    ResetPasswordWithTokenWebRequest web =
        new ResetPasswordWithTokenWebRequest("token", "pw", "pw");

    // when
    ResetPasswordWithTokenRequest dto = mapper.toApplicationDto(web);

    // then
    assertAll(
        () -> assertThat(dto.resetPasswordToken()).isEqualTo("token"),
        () -> assertThat(dto.password()).isEqualTo("pw"),
        () -> assertThat(dto.passwordConfirm()).isEqualTo("pw"));
  }

  @Test
  @DisplayName("toApplicationDto(Confirm): 웹 → 앱 매핑")
  void toApplicationDtoconfirm() {
    // given
    ConfirmPasswordWebRequest web = new ConfirmPasswordWebRequest("pw");

    // when
    ConfirmPasswordRequest dto = mapper.toApplicationDto(web);

    // then
    assertThat(dto.password()).isEqualTo("pw");
  }
}
