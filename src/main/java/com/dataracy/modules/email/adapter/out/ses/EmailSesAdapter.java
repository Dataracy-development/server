package com.dataracy.modules.email.adapter.out.ses;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.dataracy.modules.email.application.port.out.EmailSenderPort;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * SES를 통해 이메일을 보내는 어댑터
 */
@Component
@RequiredArgsConstructor
public class EmailSesAdapter implements EmailSenderPort {

    private final AmazonSimpleEmailService ses;

    @Value("${aws.ses.sender:}")
    private String sender;

    @Override
    public void send(String email, String title, String body) {
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withSubject(new Content().withCharset("UTF-8").withData(title))
                        .withBody(new Body().withText(new Content().withCharset("UTF-8").withData(body))))
                .withSource(sender);
        ses.sendEmail(request);
    }

    @PostConstruct
    public void validateProperties() {
        if (sender.isBlank()) {
            throw new IllegalStateException("Ses sender 설정이 올바르지 않습니다.");
        }
    }
}
