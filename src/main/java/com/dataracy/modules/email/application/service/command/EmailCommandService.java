package com.dataracy.modules.email.application.service.command;

import com.dataracy.modules.email.application.port.in.EmailSendUseCase;
import com.dataracy.modules.email.application.port.out.EmailRedisPort;
import com.dataracy.modules.email.application.port.out.EmailSenderPort;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.exception.EmailException;
import com.dataracy.modules.email.domain.model.EmailContent;
import com.dataracy.modules.email.domain.status.EmailErrorStatus;
import com.dataracy.modules.email.domain.support.EmailContentFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class EmailCommandService implements EmailSendUseCase {
    private final EmailSenderPort emailSenderPort;
    private final EmailRedisPort emailRedisPort;

    // 이메일 인증 방식이 여러개이므로 명시적으로 설정
    // Qualifier는 생성자 주입시점에 반영되지 않아 필드위에 바로 사용할 수 없다.
    public EmailCommandService(
            @Qualifier("emailSendGridAdapter") EmailSenderPort emailSenderPort,
            EmailRedisPort emailRedisPort
    ) {
        this.emailSenderPort = emailSenderPort;
        this.emailRedisPort = emailRedisPort;
    }

    /**
     * 이메일 인증 코드 전송
     * @param email 이메일
     */
    @Override
    public void sendEmailVerificationCode(String email, EmailVerificationType type) {
        // 인증 코드 생성
        String code = generateCode();
        // 이메일 인증 코드 전송 목적에 따라 내용 설정
        EmailContent content = EmailContentFactory.generate(type, code);

        // 이메일 전송
        try {
            emailSenderPort.send(email, content.subject(), content.body());
        } catch (Exception e) {
            log.error("이메일 전송 실패: {}", e.getMessage(), e);
            throw new EmailException(EmailErrorStatus.FAIL_SEND_EMAIL_CODE);
        }

        // 레디스에 이메일 인증 코드 저장
        emailRedisPort.saveCode(email, code, type);
    }

    // 6자리 숫자 형식
    private String generateCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}
