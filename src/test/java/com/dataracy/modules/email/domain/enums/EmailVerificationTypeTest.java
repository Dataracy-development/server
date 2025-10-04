package com.dataracy.modules.email.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** EmailVerificationType enum 테스트 */
class EmailVerificationTypeTest {

  @Test
  @DisplayName("EmailVerificationType.of() - value로 정상 조회")
  void ofwithValuereturnsCorrectType() {
    // given & when & then
    assertAll(
        () ->
            assertThat(EmailVerificationType.of("SIGN_UP"))
                .isEqualTo(EmailVerificationType.SIGN_UP),
        () ->
            assertThat(EmailVerificationType.of("PASSWORD_SEARCH"))
                .isEqualTo(EmailVerificationType.PASSWORD_SEARCH),
        () ->
            assertThat(EmailVerificationType.of("PASSWORD_RESET"))
                .isEqualTo(EmailVerificationType.PASSWORD_RESET));
  }

  @Test
  @DisplayName("EmailVerificationType.of() - 대소문자 무시하고 value로 조회")
  void ofwithValueIgnoreCasereturnsCorrectType() {
    // given & when & then
    assertAll(
        () ->
            assertThat(EmailVerificationType.of("sign_up"))
                .isEqualTo(EmailVerificationType.SIGN_UP),
        () ->
            assertThat(EmailVerificationType.of("password_search"))
                .isEqualTo(EmailVerificationType.PASSWORD_SEARCH),
        () ->
            assertThat(EmailVerificationType.of("password_reset"))
                .isEqualTo(EmailVerificationType.PASSWORD_RESET),
        () ->
            assertThat(EmailVerificationType.of("Sign_Up"))
                .isEqualTo(EmailVerificationType.SIGN_UP),
        () ->
            assertThat(EmailVerificationType.of("Password_Search"))
                .isEqualTo(EmailVerificationType.PASSWORD_SEARCH),
        () ->
            assertThat(EmailVerificationType.of("Password_Reset"))
                .isEqualTo(EmailVerificationType.PASSWORD_RESET));
  }

  @Test
  @DisplayName("EmailVerificationType value 속성 확인")
  void emailVerificationTypevaluesareCorrect() {
    // given & when & then
    assertAll(
        () -> assertThat(EmailVerificationType.SIGN_UP.getValue()).isEqualTo("SIGN_UP"),
        () ->
            assertThat(EmailVerificationType.PASSWORD_SEARCH.getValue())
                .isEqualTo("PASSWORD_SEARCH"),
        () ->
            assertThat(EmailVerificationType.PASSWORD_RESET.getValue())
                .isEqualTo("PASSWORD_RESET"));
  }

  @Test
  @DisplayName("EmailVerificationType.of() - 유효하지 않은 값으로 조회 시 예외 발생")
  void ofwithInvalidValuethrowsException() {
    // given & when & then
    assertAll(
        () -> {
          Exception ex =
              catchThrowableOfType(() -> EmailVerificationType.of("INVALID"), Exception.class);
          assertThat(ex).isNotNull();
        },
        () -> {
          Exception ex = catchThrowableOfType(() -> EmailVerificationType.of(""), Exception.class);
          assertThat(ex).isNotNull();
        },
        () -> {
          Exception ex =
              catchThrowableOfType(() -> EmailVerificationType.of(null), Exception.class);
          assertThat(ex).isNotNull();
        });
  }

  @Test
  @DisplayName("EmailVerificationType.valueOf() - 정상적인 값으로 조회")
  void valueOfwithValidValuereturnsCorrectType() {
    // given & when & then
    assertAll(
        () ->
            assertThat(EmailVerificationType.valueOf("SIGN_UP"))
                .isEqualTo(EmailVerificationType.SIGN_UP),
        () ->
            assertThat(EmailVerificationType.valueOf("PASSWORD_SEARCH"))
                .isEqualTo(EmailVerificationType.PASSWORD_SEARCH),
        () ->
            assertThat(EmailVerificationType.valueOf("PASSWORD_RESET"))
                .isEqualTo(EmailVerificationType.PASSWORD_RESET));
  }

  @Test
  @DisplayName("EmailVerificationType.valueOf() - 유효하지 않은 값으로 조회 시 예외 발생")
  void valueOfwithInvalidValuethrowsException() {
    // given & when & then
    assertAll(
        () -> {
          IllegalArgumentException ex =
              catchThrowableOfType(
                  () -> EmailVerificationType.valueOf("INVALID"), IllegalArgumentException.class);
          assertThat(ex).isNotNull();
        },
        () -> {
          IllegalArgumentException ex =
              catchThrowableOfType(
                  () -> EmailVerificationType.valueOf(""), IllegalArgumentException.class);
          assertThat(ex).isNotNull();
        },
        () -> {
          NullPointerException ex =
              catchThrowableOfType(
                  () -> EmailVerificationType.valueOf(null), NullPointerException.class);
          assertThat(ex).isNotNull();
        });
  }
}
