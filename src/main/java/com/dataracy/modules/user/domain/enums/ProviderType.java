package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ProviderType {
    LOCAL("LOCAL"),
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE");

    private final String value;

    /**
     * 입력된 문자열에 해당하는 ProviderType 열거형 상수를 반환합니다.
     *
     * 입력값이 열거형의 value 또는 이름과(대소문자 구분 없이) 일치할 경우 해당 ProviderType을 반환합니다.
     * 일치하는 값이 없으면 로그를 남기고 UserException(UserErrorStatus.INVALID_PROVIDER_TYPE)을 발생시킵니다.
     *
     * @param input 인증 제공자 타입을 나타내는 문자열
     * @return 일치하는 ProviderType 열거형 상수
     * @throws UserException 입력값이 유효하지 않은 경우 발생합니다.
     */
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
