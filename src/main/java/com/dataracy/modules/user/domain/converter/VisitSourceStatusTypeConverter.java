package com.dataracy.modules.user.domain.converter;

import com.dataracy.modules.user.domain.enums.VisitSourceStatusType;
import com.dataracy.modules.user.status.UserErrorStatus;
import com.dataracy.modules.user.status.UserException;

import java.util.Arrays;

public class VisitSourceStatusTypeConverter {

    public static VisitSourceStatusType of(String input) {

        if (input == null || input.isBlank()) {
            throw new UserException(UserErrorStatus.BAD_REQUEST_VISIT_SOURCE_STATUS_TYPE);
        }

        return Arrays.stream(VisitSourceStatusType.values())
                .filter(type -> type.getValue().equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorStatus.BAD_REQUEST_VISIT_SOURCE_STATUS_TYPE));
    }
}
