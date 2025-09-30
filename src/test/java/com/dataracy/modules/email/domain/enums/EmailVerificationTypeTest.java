package com.dataracy.modules.email.domain.enums;

import com.dataracy.modules.email.domain.exception.EmailException;
import com.dataracy.modules.email.domain.status.EmailErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
/**
 * EmailVerificationType enum 테스트
 */
class EmailVerificationTypeTest {

    @Test
    @DisplayName("EmailVerificationType.of() - value로 정상 조회")
    void of_withValue_returnsCorrectType() {
        // given & when & then
        assertThat(EmailVerificationType.of("SIGN_UP")).isEqualTo(EmailVerificationType.SIGN_UP);
        assertThat(EmailVerificationType.of("PASSWORD_SEARCH")).isEqualTo(EmailVerificationType.PASSWORD_SEARCH);
        assertThat(EmailVerificationType.of("PASSWORD_RESET")).isEqualTo(EmailVerificationType.PASSWORD_RESET);
    }

    @Test
    @DisplayName("EmailVerificationType.of() - 대소문자 무시하고 value로 조회")
    void of_withValueIgnoreCase_returnsCorrectType() {
        // given & when & then
        assertThat(EmailVerificationType.of("sign_up")).isEqualTo(EmailVerificationType.SIGN_UP);
        assertThat(EmailVerificationType.of("password_search")).isEqualTo(EmailVerificationType.PASSWORD_SEARCH);
        assertThat(EmailVerificationType.of("password_reset")).isEqualTo(EmailVerificationType.PASSWORD_RESET);
        assertThat(EmailVerificationType.of("Sign_Up")).isEqualTo(EmailVerificationType.SIGN_UP);
        assertThat(EmailVerificationType.of("Password_Search")).isEqualTo(EmailVerificationType.PASSWORD_SEARCH);
        assertThat(EmailVerificationType.of("Password_Reset")).isEqualTo(EmailVerificationType.PASSWORD_RESET);
    }

    @Test
    @DisplayName("EmailVerificationType.of() - 유효하지 않은 값으로 조회 시 예외 발생")
    void of_withInvalidValue_throwsException() {
        // given
        String invalidValue = "INVALID_EMAIL_TYPE";

        // when & then
        EmailException exception = catchThrowableOfType(() -> EmailVerificationType.of(invalidValue), EmailException.class);
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(EmailErrorStatus.INVALID_EMAIL_SEND_TYPE);
    }

    @Test
    @DisplayName("EmailVerificationType.of() - null 값으로 조회 시 예외 발생")
    void of_withNullValue_throwsException() {
        // when & then
        EmailException exception = catchThrowableOfType(() -> EmailVerificationType.of(null), EmailException.class);
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(EmailErrorStatus.INVALID_EMAIL_SEND_TYPE);
    }

    @Test
    @DisplayName("EmailVerificationType.of() - 빈 문자열로 조회 시 예외 발생")
    void of_withEmptyValue_throwsException() {
        // when & then
        EmailException exception = catchThrowableOfType(() -> EmailVerificationType.of(""), EmailException.class);
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(EmailErrorStatus.INVALID_EMAIL_SEND_TYPE);
    }

    @Test
    @DisplayName("EmailVerificationType value 속성 확인")
    void emailVerificationType_values_areCorrect() {
        // given & when & then
        assertThat(EmailVerificationType.SIGN_UP.getValue()).isEqualTo("SIGN_UP");
        assertThat(EmailVerificationType.PASSWORD_SEARCH.getValue()).isEqualTo("PASSWORD_SEARCH");
        assertThat(EmailVerificationType.PASSWORD_RESET.getValue()).isEqualTo("PASSWORD_RESET");
    }
}