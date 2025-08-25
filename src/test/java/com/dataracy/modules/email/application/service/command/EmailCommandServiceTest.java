package com.dataracy.modules.email.application.service.command;

import com.dataracy.modules.email.application.port.out.code.ManageEmailCodePort;
import com.dataracy.modules.email.application.port.out.command.SendEmailPort;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.exception.EmailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class EmailCommandServiceTest {

    @Mock private SendEmailPort sendEmailPort;
    @Mock private ManageEmailCodePort manageEmailCodePort;

    @InjectMocks
    private EmailCommandService service;

    @Nested
    @DisplayName("sendEmailVerificationCode")
    class SendEmailVerificationCode {

        @ParameterizedTest(name = "목적 {0} 일 때 성공 플로우")
        @EnumSource(EmailVerificationType.class)
        @DisplayName("성공: 6자리 코드를 생성해 메일 발송 후 저장한다(순서 검증 포함)")
        void success(EmailVerificationType type) {
            // given
            String email = "user@example.com";
            ArgumentCaptor<String> subjectCap = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> bodyCap = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> savedCodeCap = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<EmailVerificationType> typeCap = ArgumentCaptor.forClass(EmailVerificationType.class);

            // when
            service.sendEmailVerificationCode(email, type);

            // then
            // 메일 전송 호출에 대한 캡쳐
            then(sendEmailPort).should().send(eq(email), subjectCap.capture(), bodyCap.capture());
            // 코드 저장 호출에 대한 캡쳐
            then(manageEmailCodePort).should().saveCode(eq(email), savedCodeCap.capture(), typeCap.capture());

            String savedCode = savedCodeCap.getValue();
            assertThat(savedCode).matches("^\\d{6}$");       // 6자리 숫자 코드
            assertThat(bodyCap.getValue()).contains(savedCode);
            assertThat(subjectCap.getValue()).isNotBlank();
            assertThat(typeCap.getValue()).isEqualTo(type);

            // 호출 순서 검증: send → saveCode
            InOrder inOrder = inOrder(sendEmailPort, manageEmailCodePort);
            inOrder.verify(sendEmailPort).send(eq(email), anyString(), anyString());
            inOrder.verify(manageEmailCodePort).saveCode(eq(email), anyString(), eq(type));
        }

        @Test
        @DisplayName("실패: 메일 발송 실패 시 EmailException을 던지고 저장 호출되지 않는다")
        void failsWhenSendFails() {
            // given
            String email = "user2@example.com";
            willThrow(new RuntimeException("send failed"))
                    .given(sendEmailPort).send(eq(email), anyString(), anyString());

            // when
            EmailException ex = catchThrowableOfType(
                    () -> service.sendEmailVerificationCode(email, EmailVerificationType.PASSWORD_RESET),
                    EmailException.class
            );

            // then
            assertThat(ex).isNotNull();
            assertThat(ex.getErrorCode().getCode()).isEqualTo("EMAIL-001");
            then(manageEmailCodePort).should(never()).saveCode(anyString(), anyString(), any());
        }
    }
}
