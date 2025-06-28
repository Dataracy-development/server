package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.user.status.UserErrorStatus;
import com.dataracy.modules.user.status.UserException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthorLevelStatusType {

    BEGINNER("초심자"),
    PRACTITIONER("실무자"),
    EXPERT("전문가"),
    GPT("GPT활용");

    private final String authorLevel;

    public static AuthorLevelStatusType of(String authorLevel) {
        for (AuthorLevelStatusType status : AuthorLevelStatusType.values()) {
            if (status.getAuthorLevel().equalsIgnoreCase(authorLevel)) {
                return status;
            }
        }
        throw new UserException(UserErrorStatus.BAD_REQUEST_AUTHOR_LEVEL_STATUS_TYPE);
    }
}
