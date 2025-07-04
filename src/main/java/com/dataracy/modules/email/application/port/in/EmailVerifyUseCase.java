package com.dataracy.modules.email.application.port.in;

/**
 * 이메일 인증코드 검증 유스케이스
 */
public interface EmailVerifyUseCase {
    void verifyCode(String email, String code);
}
