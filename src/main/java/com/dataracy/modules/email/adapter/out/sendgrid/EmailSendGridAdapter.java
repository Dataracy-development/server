package com.dataracy.modules.email.adapter.out.sendgrid;

import com.dataracy.modules.email.application.port.out.EmailSenderPort;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * SendGrid 이메일 전송 어댑터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSendGridAdapter implements EmailSenderPort {

    private final SendGrid sendGrid;

    @Value("${sendgrid.sender}")
    private String sender;

    @Override
    public void send(String email, String subject, String body) {
        // SendGrid API 호출
        log.info("📨 [이메일 전송 시도 전] to={}, subject={}, content={}",1,2,3);

        Email from = new Email(sender);
        Email to = new Email(email);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail();
        log.info("📨 [이메일 전송 시도 전] to={}, subject={}, content={}", to, subject, content);

        mail.setFrom(from);
        mail.setSubject(subject);
        mail.addContent(content);
        mail.addPersonalization(new Personalization() {{
            addTo(to);
        }});
        log.info("✅ [SendGrid 전송] status={}, body={}", 1,2);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            log.info("✅ [SendGrid 응답] status={}, body={}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode() >= 400) {
                log.error("SendGrid 전송 실패 - status: {}, body: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("SendGrid 전송 실패 - status: " + response.getStatusCode());
            }
        } catch (IOException e) {
            throw new RuntimeException("SendGrid 전송 중 IOException 발생", e);
        }
    }
}
