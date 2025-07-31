package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 제공자 enum
 */
@Getter
@RequiredArgsConstructor
public enum ProviderType {
    LOCAL("LOCAL"),
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE");

    private final String value;

    public static ProviderType of(String input) {
        return Arrays.stream(ProviderType.values())
                .filter(type -> type.value.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> {
                    LoggerFactory.domain().logRuleViolation("ProviderType", "잘못된 ENUM 타입입니다. LOCAL, KAKAO, GOOGLE만 가능합니다.");
                    return new UserException(UserErrorStatus.INVALID_PROVIDER_TYPE);
                });
    }
}
