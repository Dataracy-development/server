package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 작성자 유형 enum
 */
@Getter
@RequiredArgsConstructor
public enum AuthorLevelType {

    BEGINNER("초심자"),
    PRACTITIONER("실무자"),
    EXPERT("전문가"),
    GPT("GPT활용");

    private final String value;

    public static AuthorLevelType of(String input) {
        return Arrays.stream(AuthorLevelType.values())
                .filter(type -> type.value.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorStatus.INVALID_AUTHOR_LEVEL_TYPE));
    }
}
