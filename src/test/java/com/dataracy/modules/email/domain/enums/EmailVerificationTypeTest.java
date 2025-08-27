package com.dataracy.modules.email.domain.enums;

import com.dataracy.modules.email.domain.exception.EmailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EmailVerificationTypeTest {

    @Test
    @DisplayName("of(String) - 대소문자 무시하고 value/name 모두 인식한다")
    void ofShouldConvertStringIgnoringCase() {
        // given
        String raw1 = "sign_up";          // value
        String raw2 = "PASSWORD_SEARCH";  // name
        String raw3 = "password_reset";   // value

        // when
        EmailVerificationType t1 = EmailVerificationType.of(raw1);
        EmailVerificationType t2 = EmailVerificationType.of(raw2);
        EmailVerificationType t3 = EmailVerificationType.of(raw3);

        // then
        assertThat(t1).isEqualTo(EmailVerificationType.SIGN_UP);
        assertThat(t2).isEqualTo(EmailVerificationType.PASSWORD_SEARCH);
        assertThat(t3).isEqualTo(EmailVerificationType.PASSWORD_RESET);
    }

    @Test
    @DisplayName("of(String) - 정의되지 않은 값이면 EmailException(INVALID_EMAIL_SEND_TYPE)")
    void ofShouldThrowExceptionOnInvalidValue() {
        // given
        String invalid = "not-supported";

        // when
        EmailException ex = catchThrowableOfType(
                () -> EmailVerificationType.of(invalid),
                EmailException.class
        );

        // then
        assertThat(ex).isNotNull();
        assertThat(ex.getErrorCode().getCode()).isEqualTo("EMAIL-004");
        assertThat(ex.getErrorCode().getMessage()).contains("이메일 전송 목적");
    }
}
