package com.dataracy.modules.email.application.service.command;

import com.dataracy.modules.email.application.port.in.EmailSendUseCase;
import com.dataracy.modules.email.application.port.out.EmailRedisPort;
import com.dataracy.modules.email.application.port.out.EmailSenderPort;
import com.dataracy.modules.email.domain.exception.EmailException;
import com.dataracy.modules.email.domain.status.EmailErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailCommandService implements EmailSendUseCase {

    private final EmailSenderPort emailSenderPort;
    private final EmailRedisPort emailRedisPort;

    /**
     * 이메일 인증 코드 전송
     * @param email 이메일
     */
    public void sendEmailVerificationCode(String email) {
        String code = generateCode();
        String title = "이메일 인증 코드";
        String body = "Dataracy 이메일 인증 코드 : " + code;
        try {
            emailSenderPort.send(email, title, body);
        } catch (Exception e) {
            log.error("이메일 전송 실패: {}", e.getMessage(), e);
            throw new EmailException(EmailErrorStatus.FAIL_SEND_EMAIL_CODE);
        }
        emailRedisPort.saveCode(email, code);
    }

    // 6자리 숫자 형식
    private String generateCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}
