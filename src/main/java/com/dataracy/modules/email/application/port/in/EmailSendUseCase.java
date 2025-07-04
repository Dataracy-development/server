package com.dataracy.modules.email.application.port.in;

/**
 * 이메일 인증코드 전송 유스케이스
 */
public interface EmailSendUseCase {
    void sendEmailVerificationCode(String email);
}
