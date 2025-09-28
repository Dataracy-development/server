package com.dataracy.modules.email.application.service.command;

import com.dataracy.modules.email.application.port.out.code.ManageEmailCodePort;
import com.dataracy.modules.email.application.port.out.command.SendEmailPort;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.exception.EmailException;
import com.dataracy.modules.email.domain.model.EmailContent;
import com.dataracy.modules.email.domain.status.EmailErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmailCommandServiceTest {

    @Mock
    private SendEmailPort sendEmailPort;

    @Mock
    private ManageEmailCodePort manageEmailCodePort;

    private EmailCommandService emailCommandService;

    @BeforeEach
    void setUp() {
        emailCommandService = new EmailCommandService(sendEmailPort, manageEmailCodePort);
    }

    @Nested
    @DisplayName("sendEmailVerificationCode 메서드 테스트")
    class SendEmailVerificationCodeTest {

        @Test
        @DisplayName("성공: 이메일 인증 코드 전송 성공")
        void sendEmailVerificationCode_정상전송_성공() {
            // given
            String email = "test@example.com";
            EmailVerificationType type = EmailVerificationType.SIGN_UP;

            // when
            emailCommandService.sendEmailVerificationCode(email, type);

            // then
            then(sendEmailPort).should().send(anyString(), anyString(), anyString());
            then(manageEmailCodePort).should().saveCode(anyString(), anyString(), any(EmailVerificationType.class));
        }

        @Test
        @DisplayName("성공: PASSWORD_SEARCH 타입 이메일 전송")
        void sendEmailVerificationCode_PASSWORDSEARCH타입_성공() {
            // given
            String email = "user@test.com";
            EmailVerificationType type = EmailVerificationType.PASSWORD_SEARCH;

            // when
            emailCommandService.sendEmailVerificationCode(email, type);

            // then
            then(sendEmailPort).should().send(anyString(), anyString(), anyString());
            then(manageEmailCodePort).should().saveCode(anyString(), anyString(), any(EmailVerificationType.class));
        }

        @Test
        @DisplayName("성공: PASSWORD_RESET 타입 이메일 전송")
        void sendEmailVerificationCode_PASSWORDRESET타입_성공() {
            // given
            String email = "reset@example.com";
            EmailVerificationType type = EmailVerificationType.PASSWORD_RESET;

            // when
            emailCommandService.sendEmailVerificationCode(email, type);

            // then
            then(sendEmailPort).should().send(anyString(), anyString(), anyString());
            then(manageEmailCodePort).should().saveCode(anyString(), anyString(), any(EmailVerificationType.class));
        }

        @Test
        @DisplayName("실패: 이메일 전송 중 예외 발생")
        void sendEmailVerificationCode_전송예외_EmailException발생() {
            // given
            String email = "error@example.com";
            EmailVerificationType type = EmailVerificationType.SIGN_UP;
            willThrow(new RuntimeException("Network error")).given(sendEmailPort).send(anyString(), anyString(), anyString());

            // when & then
            assertThatThrownBy(() -> emailCommandService.sendEmailVerificationCode(email, type))
                .isInstanceOf(EmailException.class);

            then(sendEmailPort).should().send(anyString(), anyString(), anyString());
            then(manageEmailCodePort).shouldHaveNoInteractions();
        }
    }
}