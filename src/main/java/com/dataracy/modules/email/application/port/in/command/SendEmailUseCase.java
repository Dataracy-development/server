package com.dataracy.modules.email.application.port.in.command;

import com.dataracy.modules.email.domain.enums.EmailVerificationType;

/**
 * 이메일 인증코드 전송 유스케이스
 */
public interface SendEmailUseCase {
    void sendEmailVerificationCode(String email, EmailVerificationType type);
}
