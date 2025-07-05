package com.dataracy.modules.email.domain.support;

import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.model.EmailContent;

public class EmailContentFactory {
    /**
     * 이메일 전송 목적에 따라 내용을 결정한다.
     *
     * @param type 이메일 전송 목적 enum
     * @param code 6자리 숫자
     * @return
     */
    public static EmailContent generate(EmailVerificationType type, String code) {
        return switch (type) {
            case SIGN_UP -> new EmailContent(
                    "[Dataracy] 회원가입 이메일 인증번호 안내",
                    String.format("""
                            안녕하세요.
                            Dataracy 회원가입을 위한 인증번호는 아래와 같습니다.

                            🔐 인증번호: %s

                            해당 인증번호는 5분 동안 유효합니다.
                            본인이 요청하지 않으셨다면 이 이메일은 무시해주세요.
                            """, code)
            );

            case PASSWORD_RESET -> new EmailContent(
                    "[Dataracy] 비밀번호 재설정 인증번호 안내",
                    String.format("""
                            안녕하세요.
                            비밀번호 재설정을 위한 인증번호는 아래와 같습니다.

                            🔐 인증번호: %s

                            해당 인증번호는 5분 동안 유효합니다.
                            요청하지 않으셨다면 이 이메일은 무시해주세요.
                            """, code)
            );
        };
    }
}
