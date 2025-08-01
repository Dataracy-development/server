package com.dataracy.modules.auth.domain.enums;

import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 토큰 유형 enum
 */
@Getter
@RequiredArgsConstructor
public enum TokenType {
    REGISTER("REGISTER"),
    ACCESS("ACCESS"),
    REFRESH("REFRESH");

    private final String value;

    public static TokenType of(String input) {
        return Arrays.stream(TokenType.values())
                .filter(type -> type.value.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> {
                    LoggerFactory.domain().logRuleViolation("TokenType", "잘못된 ENUM 타입입니다. REGISTER, ACCESS, REFRESH만 가능합니다.");
                    return new AuthException(AuthErrorStatus.BAD_REQUEST_TOKEN_TYPE);
                });
    }
}
