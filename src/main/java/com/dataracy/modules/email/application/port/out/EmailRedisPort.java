package com.dataracy.modules.email.application.port.out;

/**
 * 이메일 레디스 저장 및 조회 포트
 */
public interface EmailRedisPort {
    void saveCode(String email, String code);
    String verifyCode(String email, String code);
}
