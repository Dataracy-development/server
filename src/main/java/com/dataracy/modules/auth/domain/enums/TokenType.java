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
    REFRESH("REFRESH"),
    RESET_PASSWORD("RESET_PASSWORD"),
    ;

    private final String value;

    /**
     * 주어진 문자열 입력값에 해당하는 TokenType 열거형 상수를 반환합니다.
     *
     * 입력값이 "REGISTER", "ACCESS", "REFRESH", "RESET_PASSWORD" 중 하나와 일치하지 않을 경우 규칙 위반을 로그로 남기고 AuthException을 발생시킵니다.
     *
     * @param input 토큰 타입을 나타내는 문자열
     * @return 일치하는 TokenType 열거형 상수
     * @throws AuthException 입력값이 유효하지 않은 경우 발생
     */
    public static TokenType of(String input) {
        return Arrays.stream(TokenType.values())
                .filter(type -> type.value.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> {
                    LoggerFactory.domain().logRuleViolation("TokenType", "잘못된 ENUM 타입입니다. REGISTER, ACCESS, REFRESH, RESET_PASSWORD만 가능합니다.");
                    return new AuthException(AuthErrorStatus.BAD_REQUEST_TOKEN_TYPE);
                });
    }
}
