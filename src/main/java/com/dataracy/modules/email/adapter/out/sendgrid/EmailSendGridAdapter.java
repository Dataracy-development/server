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
import jakarta.annotation.PostConstruct;
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

    @Value("${sendgrid.sender:}")
    private String sender;

    /**
     * 지정된 이메일 주소로 SendGrid를 통해 이메일을 전송합니다.
     *
     * @param email   수신자 이메일 주소
     * @param subject 이메일 제목
     * @param body    이메일 본문 내용
     * @throws RuntimeException SendGrid 전송 실패 또는 IO 예외 발생 시
     */
    @Override
    public void send(String email, String subject, String body) {
        // SendGrid API 호출
        Email from = new Email(sender);
        Email to = new Email(email);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail();
        log.info("[이메일 전송 시도 전] to={}, subject={}, content={}", to, subject, content);

        mail.setFrom(from);
        mail.setSubject(subject);
        mail.addContent(content);
        mail.addPersonalization(new Personalization() {{
            addTo(to);
        }});
        log.info("[SendGrid 요청 준비] to={}, subject={}", to, subject);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            log.info("[SendGrid 응답] status={}, body={}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode() >= 400) {
                log.error("SendGrid 전송 실패 - status: {}, body: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("SendGrid 전송 실패 - status: " + response.getStatusCode());
            }
        } catch (IOException e) {
            throw new RuntimeException("SendGrid 전송 중 IOException 발생", e);
        }
    }

    @PostConstruct
    public void validateProperties() {
        if (sender.isBlank()) {
            throw new IllegalStateException("Sendgrid sender 설정이 올바르지 않습니다.");
        }
    }
}
