package com.dataracy.modules.email.adapter.ses;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.email.application.port.out.command.SendEmailPort;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/** SES를 통해 이메일을 보내는 어댑터 */
@Component
@RequiredArgsConstructor
public class SendEmailSesAdapter implements SendEmailPort {
  private final AmazonSimpleEmailService ses;

  @Value("${aws.ses.sender:}")
  private String sender;

  /**
   * 지정된 이메일 주소로 AWS SES를 통해 이메일을 발송합니다.
   *
   * @param email 수신자 이메일 주소
   * @param title 이메일 제목
   * @param body 이메일 본문 내용
   */
  @Override
  public void send(String email, String title, String body) {
    LoggerFactory.common().logStart("이메일 전송 시도 시작", "to=" + email + ", title=" + title);

    SendEmailRequest request =
        new SendEmailRequest()
            .withDestination(new Destination().withToAddresses(email))
            .withMessage(
                new Message()
                    .withSubject(new Content().withCharset("UTF-8").withData(title))
                    .withBody(
                        new Body().withText(new Content().withCharset("UTF-8").withData(body))))
            .withSource(sender);
    ses.sendEmail(request);

    LoggerFactory.common().logEnd("이메일 전송 시도 완료", "to=" + email + ", title=" + title);
  }

  /**
   * SES 발신자 이메일 설정이 비어 있는지 검증합니다.
   *
   * <p>발신자 이메일이 비어 있으면 IllegalStateException을 발생시킵니다.
   */
  @PostConstruct
  public void validateProperties() {
    if (sender.isBlank()) {
      throw new IllegalStateException("Ses sender 설정이 올바르지 않습니다.");
    }
  }
}
