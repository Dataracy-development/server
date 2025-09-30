package com.dataracy.modules.email.adapter.sendgrid;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.email.application.port.out.command.SendEmailPort;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * SendGrid 이메일 전송 어댑터
 */
@Component
@RequiredArgsConstructor
public class SendEmailSendGridAdapter implements SendEmailPort {
    private final SendGrid sendGrid;

    @Value("${sendgrid.sender:}")
    private String sender;

    /**
     * SendGrid를 이용해 지정된 이메일 주소로 제목과 본문이 포함된 이메일을 전송합니다.
     *
     * @param email   수신자 이메일 주소
     * @param subject 이메일 제목
     * @param body    이메일 본문 내용
     * @throws RuntimeException SendGrid API 호출 실패 또는 IO 예외 발생 시
     */
    @Override
    public void send(String email, String subject, String body) {
        LoggerFactory.common().logStart("이메일 전송", "to=" + email + ", subject=" + subject);

        // SendGrid API 호출
        Email from = new Email(sender);
        Email to = new Email(email);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail();

        mail.setFrom(from);
        mail.setSubject(subject);
        mail.addContent(content);
        
        Personalization personalization = new Personalization();
        personalization.addTo(to);
        mail.addPersonalization(personalization);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 400) {
                LoggerFactory.common().logError("이메일 전송 실패", "status:" + response.getStatusCode() + ", body:" + response.getBody());
                throw new CommonException(CommonErrorStatus.EMAIL_SEND_FAILURE);
            }
            LoggerFactory.common().logEnd("이메일 전송 시도 완료", "to=" + to + ", subject=" + subject + ",content=" + content);
        } catch (IOException e) {
            LoggerFactory.common().logError("SendGrid", "SendGrid 전송 중 IOException 발생");
            throw new CommonException(CommonErrorStatus.EMAIL_SEND_FAILURE);
        }
    }

    @PostConstruct
    public void validateProperties() {
        if (sender.isBlank()) {
            throw new IllegalStateException("Sendgrid sender 설정이 올바르지 않습니다.");
        }
    }
}
