package com.dataracy.modules.email.application.port.out;

import com.dataracy.modules.email.domain.enums.EmailVerificationType;

/**
 * 이메일 레디스 저장 및 조회 포트
 */
public interface EmailCachePort {
    void saveCode(String email, String code, EmailVerificationType verificationType);
    String verifyCode(String email, String code, EmailVerificationType verificationType);
    void deleteCode(String email, EmailVerificationType verificationType);
}
