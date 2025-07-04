package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 직업 enum
 */
@Getter
@RequiredArgsConstructor
public enum OccupationType {

    STUDENT("학생"),
    DEVELOPER("개발자"),
    PLANNER("기획자"),
    DESIGNER("디자이너"),
    MARKETER("마케터"),
    OTHER("기타"),
    ;

    private final String value;

    public static OccupationType of(String input) {

        return Arrays.stream(OccupationType.values())
                .filter(type -> type.value.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorStatus.INVALID_OCCUPATION_TYPE));
    }
}
