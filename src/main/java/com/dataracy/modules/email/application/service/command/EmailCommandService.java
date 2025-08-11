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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;

@Service
public class EmailCommandService implements SendEmailUseCase {
    private final SendEmailPort sendEmailPort;
    private final CacheEmailPort cacheEmailPort;

    /**
     * EmailCommandService의 인스턴스를 생성하고 이메일 전송 및 캐시 포트 의존성을 주입합니다.
     *
     * @param sendEmailPort 이메일 전송을 담당하는 포트 구현체
     * @param cacheEmailPort 이메일 인증 코드 캐싱을 담당하는 포트 구현체
     */
    public EmailCommandService(
            @Qualifier("sendEmailSendGridAdapter") SendEmailPort sendEmailPort,
            CacheEmailPort cacheEmailPort
    ) {
        this.sendEmailPort = sendEmailPort;
        this.cacheEmailPort = cacheEmailPort;
    }

    /**
     * 지정된 이메일 주소로 인증 코드를 생성하여 전송하고, 해당 코드를 캐시에 저장합니다.
     *
     * @param email 인증 코드를 받을 이메일 주소
     * @param type 인증 코드 전송 목적을 나타내는 타입
     * @throws EmailException 이메일 전송에 실패한 경우 발생
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
