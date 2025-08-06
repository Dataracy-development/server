package com.dataracy.modules.email.application.port.out.command;

/**
 * 이메일 전송 포트
 */
public interface SendEmailPort {
    void send(String email, String title, String body);
}
