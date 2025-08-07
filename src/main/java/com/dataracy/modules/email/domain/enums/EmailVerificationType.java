package com.dataracy.modules.email.domain.enums;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.email.domain.exception.EmailException;
import com.dataracy.modules.email.domain.status.EmailErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 이메일 전송 enum
 */
@Getter
@RequiredArgsConstructor
public enum EmailVerificationType {
    SIGN_UP("SIGN_UP"),
    PASSWORD_SEARCH("PASSWORD_SEARCH"),
    PASSWORD_RESET("PASSWORD_RESET"),
    ;

    private final String value;

    /**
     * 주어진 문자열 입력값에 해당하는 EmailVerificationType 열거형 상수를 반환합니다.
     *
     * 입력값은 열거형 상수의 이름 또는 value 필드와 대소문자 구분 없이 매칭됩니다.
     * 일치하는 상수가 없을 경우, 규칙 위반 로그를 남기고 EmailException을 발생시킵니다.
     *
     * @param input 매칭할 이메일 인증 타입 문자열
     * @return 입력값에 해당하는 EmailVerificationType 상수
     * @throws EmailException 유효하지 않은 이메일 인증 타입 입력 시 발생
     */
    public static EmailVerificationType of(String input) {
        return Arrays.stream(EmailVerificationType.values())
                .filter(type -> type.value.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> {
                    LoggerFactory.domain().logRuleViolation("EmailVerificationType", "잘못된 ENUM 타입입니다. SIGN_UP, PASSWORD_SEARCH, PASSWORD_RESET만 가능합니다.");
                    return new EmailException(EmailErrorStatus.INVALID_EMAIL_SEND_TYPE);
                });
    }
}
