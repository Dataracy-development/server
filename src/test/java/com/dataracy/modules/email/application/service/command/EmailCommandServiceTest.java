package com.dataracy.modules.email.application.service.command;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.email.application.port.out.code.ManageEmailCodePort;
import com.dataracy.modules.email.application.port.out.command.SendEmailPort;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.exception.EmailException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmailCommandServiceTest {

  @Mock private SendEmailPort sendEmailPort;

  @Mock private ManageEmailCodePort manageEmailCodePort;

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
    void sendEmailVerificationCodeNormalSendingReturnsSuccess() {
      // given
      String email = "test@example.com";
      EmailVerificationType type = EmailVerificationType.SIGN_UP;

      // when
      emailCommandService.sendEmailVerificationCode(email, type);

      // then
      then(sendEmailPort).should().send(anyString(), anyString(), anyString());
      then(manageEmailCodePort)
          .should()
          .saveCode(anyString(), anyString(), any(EmailVerificationType.class));
    }

    @Test
    @DisplayName("성공: PASSWORD_SEARCH 타입 이메일 전송")
    void sendEmailVerificationCodeWithPasswordSearchTypeReturnsSuccess() {
      // given
      String email = "user@test.com";
      EmailVerificationType type = EmailVerificationType.PASSWORD_SEARCH;

      // when
      emailCommandService.sendEmailVerificationCode(email, type);

      // then
      then(sendEmailPort).should().send(anyString(), anyString(), anyString());
      then(manageEmailCodePort)
          .should()
          .saveCode(anyString(), anyString(), any(EmailVerificationType.class));
    }

    @Test
    @DisplayName("성공: PASSWORD_RESET 타입 이메일 전송")
    void sendEmailVerificationCodeWithPasswordResetTypeReturnsSuccess() {
      // given
      String email = "reset@example.com";
      EmailVerificationType type = EmailVerificationType.PASSWORD_RESET;

      // when
      emailCommandService.sendEmailVerificationCode(email, type);

      // then
      then(sendEmailPort).should().send(anyString(), anyString(), anyString());
      then(manageEmailCodePort)
          .should()
          .saveCode(anyString(), anyString(), any(EmailVerificationType.class));
    }

    @Test
    @DisplayName("실패: 이메일 전송 중 예외 발생")
    void sendEmailVerificationCodeWhenSendingExceptionThrowsEmailException() {
      // given
      String email = "error@example.com";
      EmailVerificationType type = EmailVerificationType.SIGN_UP;
      willThrow(new RuntimeException("Network error"))
          .given(sendEmailPort)
          .send(anyString(), anyString(), anyString());

      // when & then
      EmailException exception =
          catchThrowableOfType(
              () -> emailCommandService.sendEmailVerificationCode(email, type),
              EmailException.class);
      assertAll(
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .isInstanceOf(EmailException.class));

      then(sendEmailPort).should().send(anyString(), anyString(), anyString());
      then(manageEmailCodePort).shouldHaveNoInteractions();
    }
  }
}
