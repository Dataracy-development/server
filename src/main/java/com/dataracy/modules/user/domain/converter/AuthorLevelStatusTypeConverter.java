package com.dataracy.modules.user.domain.converter;

import com.dataracy.modules.user.domain.enums.AuthorLevelStatusType;
import com.dataracy.modules.user.status.UserErrorStatus;
import com.dataracy.modules.user.status.UserException;

import java.util.Arrays;

public class AuthorLevelStatusTypeConverter {

    public static AuthorLevelStatusType of(String input) {

        if (input == null || input.isBlank()) {
            throw new UserException(UserErrorStatus.BAD_REQUEST_AUTHOR_LEVEL_STATUS_TYPE);
        }

        return Arrays.stream(AuthorLevelStatusType.values())
                .filter(type -> type.getValue().equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorStatus.BAD_REQUEST_AUTHOR_LEVEL_STATUS_TYPE));
    }
}
