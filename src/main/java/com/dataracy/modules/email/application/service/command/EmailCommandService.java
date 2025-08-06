package com.dataracy.modules.email.application.service.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.email.application.port.in.command.SendEmailUseCase;
import com.dataracy.modules.email.application.port.out.cache.CacheEmailPort;
import com.dataracy.modules.email.application.port.out.command.SendEmailPort;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.exception.EmailException;
import com.dataracy.modules.email.domain.model.EmailContent;
import com.dataracy.modules.email.domain.status.EmailErrorStatus;
import com.dataracy.modules.email.domain.support.EmailContentFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;

@Slf4j
@Service
public class EmailCommandService implements SendEmailUseCase {
    private final SendEmailPort sendEmailPort;
    private final CacheEmailPort cacheEmailPort;

    // 이메일 인증 방식이 여러개이므로 명시적으로 설정
    // Qualifier는 생성자 주입시점에 반영되지 않아 필드위에 바로 사용할 수 없다.
    public EmailCommandService(
            @Qualifier("sendEmailSendGridAdapter") SendEmailPort sendEmailPort,
            CacheEmailPort cacheEmailPort
    ) {
        this.sendEmailPort = sendEmailPort;
        this.cacheEmailPort = cacheEmailPort;
    }

    /**
     * 이메일 인증 코드 전송
     * @param email 이메일
     */
    @Override
    public void sendEmailVerificationCode(String email, EmailVerificationType type) {
        Instant startTime = LoggerFactory.service().logStart("SendEmailUseCase", "이메일 인증 코드 전송 서비스 시작 email=" + email);

        // 인증 코드 생성
        String code = generateCode();
        // 이메일 인증 코드 전송 목적에 따라 내용 설정
        EmailContent content = EmailContentFactory.generate(type, code);

        // 이메일 전송
        try {
            sendEmailPort.send(email, content.subject(), content.body());
        } catch (Exception e) {
            LoggerFactory.service().logException("SendEmailUseCase", "이메일 전송 실패. email=" + email, e);
            throw new EmailException(EmailErrorStatus.FAIL_SEND_EMAIL_CODE);
        }

        // 레디스에 이메일 인증 코드 저장
        cacheEmailPort.saveCode(email, code, type);
        LoggerFactory.service().logSuccess("SendEmailUseCase", "이메일 인증 코드 전송 서비스 종료 email=" + email, startTime);
    }

    // 6자리 숫자 형식
    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
