package com.dataracy.modules.email.application.port.in.command;

import com.dataracy.modules.email.domain.enums.EmailVerificationType;

/**
 * 이메일 인증코드 전송 유스케이스
 */
public interface SendEmailUseCase {
    /**
 * 지정된 이메일 주소로 이메일 인증 코드를 전송합니다.
 *
 * @param email 인증 코드를 받을 이메일 주소
 * @param type  이메일 인증 유형
 */
void sendEmailVerificationCode(String email, EmailVerificationType type);
}
