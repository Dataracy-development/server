package com.dataracy.modules.email.application;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.dataracy.modules.email.infra.redis.EmailRedisManager;
import com.dataracy.modules.email.status.EmailErrorStatus;
import com.dataracy.modules.email.status.EmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailApplicationService {

    private final AmazonSimpleEmailService ses;
    private final EmailRedisManager emailRedisManager;

    @Value("${aws.ses.sender}")
    private String sender;

    public void sendVerificationEmail(String email) {
        String code = generateCode();
        emailRedisManager.saveCode(email, code);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withSubject(new Content().withCharset("UTF-8").withData("이메일 인증 코드"))
                        .withBody(new Body().withText(new Content().withCharset("UTF-8")
                                .withData("Dataracy 이메일 인증코드: " + code))))
                .withSource(sender);

        ses.sendEmail(request);
    }

    public void verifyCode(String email, String code) {
        boolean verified = emailRedisManager.verifyCode(email, code);
        if (!verified) {
            throw new EmailException(EmailErrorStatus.FAIL_VERIFY_EMAIL_CODE);
        }
    }

    private String generateCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}

