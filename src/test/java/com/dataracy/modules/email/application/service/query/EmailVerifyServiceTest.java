package com.dataracy.modules.email.application.service.query;

import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.token.ManageResetTokenUseCase;
import com.dataracy.modules.email.application.dto.response.GetResetTokenResponse;
import com.dataracy.modules.email.application.port.out.code.ManageEmailCodePort;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.exception.EmailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerifyServiceTest {

    @Mock private ManageEmailCodePort manageEmailCodePort;
    @Mock private JwtGenerateUseCase jwtGenerateUseCase;
    @Mock private ManageResetTokenUseCase manageResetTokenUseCase;

    @InjectMocks
    private EmailVerifyService service;

    @Nested
    @DisplayName("verifyCode")
    class VerifyCode {

        @Test
        @DisplayName("성공: PASSWORD_SEARCH → 코드 일치 시 삭제 + 토큰 발급/저장")
        void successPasswordSearch() {
            // given
            String email = "a@ex.com";
            String inputCode = "123456";
            given(manageEmailCodePort.verifyCode(email, inputCode, EmailVerificationType.PASSWORD_SEARCH))
                    .willReturn(inputCode);
            given(jwtGenerateUseCase.generateResetPasswordToken(email))
                    .willReturn("jwt-token-123");

            // when
            GetResetTokenResponse res =
                    service.verifyCode(email, inputCode, EmailVerificationType.PASSWORD_SEARCH);

            // then
            assertThat(res.resetToken()).isEqualTo("jwt-token-123");
            then(manageEmailCodePort).should().deleteCode(email, EmailVerificationType.PASSWORD_SEARCH);
            then(manageResetTokenUseCase).should().saveResetToken("jwt-token-123");
        }

        @Test
        @DisplayName("성공: SIGN_UP → 코드 일치 시 삭제만, 토큰은 null")
        void successSignUp() {
            // given
            String email = "b@ex.com";
            String inputCode = "654321";
            given(manageEmailCodePort.verifyCode(email, inputCode, EmailVerificationType.SIGN_UP))
                    .willReturn(inputCode);

            // when
            GetResetTokenResponse res =
                    service.verifyCode(email, inputCode, EmailVerificationType.SIGN_UP);

            // then
            assertThat(res.resetToken()).isNull();
            then(manageEmailCodePort).should().deleteCode(email, EmailVerificationType.SIGN_UP);
            then(jwtGenerateUseCase).should(never()).generateResetPasswordToken(anyString());
            then(manageResetTokenUseCase).should(never()).saveResetToken(anyString());
        }

        @Test
        @DisplayName("실패: 만료(null) → EmailException(EXPIRED_EMAIL_CODE: EMAIL-003)")
        void expired() {
            // given
            String email = "c@ex.com";
            String inputCode = "000000";
            given(manageEmailCodePort.verifyCode(email, inputCode, EmailVerificationType.SIGN_UP))
                    .willReturn(null);

            // when
            EmailException ex = catchThrowableOfType(
                    () -> service.verifyCode(email, inputCode, EmailVerificationType.SIGN_UP),
                    EmailException.class
            );

            // then
            assertThat(ex).isNotNull();
            assertThat(ex.getErrorCode().getCode()).isEqualTo("EMAIL-003");
            then(manageEmailCodePort).should(never()).deleteCode(anyString(), any());
        }

        @Test
        @DisplayName("실패: 불일치 → EmailException(FAIL_VERIFY_EMAIL_CODE: EMAIL-002)")
        void mismatch() {
            // given
            String email = "d@ex.com";
            String inputCode = "111111";
            given(manageEmailCodePort.verifyCode(email, inputCode, EmailVerificationType.PASSWORD_RESET))
                    .willReturn("222222");

            // when
            EmailException ex = catchThrowableOfType(
                    () -> service.verifyCode(email, inputCode, EmailVerificationType.PASSWORD_RESET),
                    EmailException.class
            );

            // then
            assertThat(ex).isNotNull();
            assertThat(ex.getErrorCode().getCode()).isEqualTo("EMAIL-002");
            then(manageEmailCodePort).should(never()).deleteCode(anyString(), any());
        }
    }
}
