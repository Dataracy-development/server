package com.dataracy.modules.email.adapter.web.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.email.adapter.web.api.command.EmailCommandController;
import com.dataracy.modules.email.adapter.web.mapper.command.EmailCommandWebMapper;
import com.dataracy.modules.email.adapter.web.request.command.SendEmailWebRequest;
import com.dataracy.modules.email.application.dto.request.command.SendEmailRequest;
import com.dataracy.modules.email.application.port.in.command.SendEmailUseCase;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.exception.EmailException;
import com.dataracy.modules.email.domain.status.EmailSuccessStatus;

@ExtendWith(MockitoExtension.class)
class EmailCommandControllerTest {

  @Mock private EmailCommandWebMapper mapper;

  @Mock private SendEmailUseCase useCase;

  @InjectMocks private EmailCommandController controller;

  @ParameterizedTest(name = "목적 {0} 호출 시 200 OK + SuccessResponse 반환")
  @ValueSource(strings = {"SIGN_UP", "PASSWORD_SEARCH", "PASSWORD_RESET"})
  @DisplayName("전송 목적별 정상 플로우")
  void sendCodeSuccess(String purpose) {
    // given
    SendEmailWebRequest webReq = new SendEmailWebRequest("user@example.com", purpose);
    SendEmailRequest appReq = new SendEmailRequest("user@example.com", purpose);

    given(mapper.toApplicationDto(webReq)).willReturn(appReq);

    // when
    ResponseEntity<SuccessResponse<Void>> response = controller.sendCode(webReq);

    // then
    assertAll(
        () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
        () -> {
          // 응답 메시지 상태 코드 확인
          EmailVerificationType type = EmailVerificationType.of(purpose);
          switch (type) {
            case SIGN_UP ->
                assertThat(response.getBody().getMessage())
                    .isEqualTo(EmailSuccessStatus.OK_SEND_EMAIL_CODE_SIGN_UP.getMessage());
            case PASSWORD_SEARCH ->
                assertThat(response.getBody().getMessage())
                    .isEqualTo(EmailSuccessStatus.OK_SEND_EMAIL_CODE_PASSWORD_SEARCH.getMessage());
            case PASSWORD_RESET ->
                assertThat(response.getBody().getMessage())
                    .isEqualTo(EmailSuccessStatus.OK_SEND_EMAIL_CODE_PASSWORD_RESET.getMessage());
          }
        });
    then(useCase)
        .should()
        .sendEmailVerificationCode("user@example.com", EmailVerificationType.of(purpose));
  }

  @Test
  @DisplayName("잘못된 purpose 이면 EmailException 발생")
  void invalidPurpose() {
    // given
    SendEmailWebRequest webReq = new SendEmailWebRequest("user@example.com", "NOT_VALID");

    // when
    EmailException ex =
        catchThrowableOfType(() -> controller.sendCode(webReq), EmailException.class);

    // then
    assertThat(ex).isNotNull();
    then(useCase).shouldHaveNoInteractions();
  }
}
