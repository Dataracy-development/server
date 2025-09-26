package com.dataracy.modules.email.application.service.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.email.application.port.in.command.SendEmailUseCase;
import com.dataracy.modules.email.application.port.out.code.ManageEmailCodePort;
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
    private final ManageEmailCodePort manageEmailCodePort;

    /**
     * EmailCommandService를 생성하고 필요한 포트 의존성(sendEmailPort, manageEmailCodePort)을 주입합니다.
     *
     * 생성된 인스턴스는 주입된 포트를 사용해 이메일 전송 및 이메일 인증 코드 관리를 수행합니다.
     */
    public EmailCommandService(
            @Qualifier("sendEmailSendGridAdapter") SendEmailPort sendEmailPort,
            ManageEmailCodePort manageEmailCodePort
    ) {
        this.sendEmailPort = sendEmailPort;
        this.manageEmailCodePort = manageEmailCodePort;
    }

    /**
     * 지정된 이메일로 6자리 인증 코드를 생성해 전송하고, 전송 성공 시 해당 코드를 저장합니다.
     *
     * 이 메서드는 인증 코드를 생성한 뒤 전송을 시도하고, 전송에 실패하면 EmailException을 던집니다.
     * 전송이 성공하면 이메일과 목적(type)에 맞춰 생성한 코드를 영속/캐시 계층에 저장합니다.
     *
     * @param email 인증 코드를 받을 이메일 주소
     * @param type  인증 코드 전송 목적을 나타내는 값(예: 회원가입, 비밀번호 재설정 등)
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
        manageEmailCodePort.saveCode(email, code, type);
        LoggerFactory.service().logSuccess("SendEmailUseCase", "이메일 인증 코드 전송 서비스 종료 email=" + email, startTime);
    }

    // 6자리 숫자 형식
    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
