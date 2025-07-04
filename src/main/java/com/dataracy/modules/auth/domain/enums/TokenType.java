package com.dataracy.modules.auth.domain.enums;

import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 토큰 유형
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
                .orElseThrow(() -> new AuthException(AuthErrorStatus.BAD_REQUEST_TOKEN_TYPE));
    }
}
