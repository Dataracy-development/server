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
