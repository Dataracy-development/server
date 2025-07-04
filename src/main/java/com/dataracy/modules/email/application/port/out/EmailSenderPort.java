package com.dataracy.modules.email.application.port.out;

/**
 * 이메일 전송 포트
 */
public interface EmailSenderPort {
    void send(String email, String title, String body);
}
