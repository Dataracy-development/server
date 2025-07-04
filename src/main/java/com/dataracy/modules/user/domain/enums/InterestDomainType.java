package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 흥미있는 도메인 enum
 */
@Getter
@RequiredArgsConstructor
public enum InterestDomainType {

    FRONTEND("프론트엔드"),
    BACKEND("백엔드"),
    AI("인공지능"),
    DATA("데이터분석"),
    SECURITY("보안"),
    DESIGN("디자인"),
    STARTUP("스타트업"),
    ;

    private final String value;

    public static InterestDomainType of(String input) {
        return Arrays.stream(InterestDomainType.values())
                .filter(type -> type.value.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorStatus.INVALID_DOMAIN_TOPIC_TYPE));
    }
}
