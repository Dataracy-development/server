package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.user.status.UserErrorStatus;
import com.dataracy.modules.user.status.UserException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OccupationStatusType {

    STUDENT("학생"),
    DEVELOPER("개발자"),
    PLANNER("기획자"),
    DESIGNER("디자이너"),
    MARKETER("마케터"),
    OTHER("기타"),
    ;

    private final String occuapation;

    public static OccupationStatusType of(String occuapation) {
        for (OccupationStatusType status : OccupationStatusType.values()) {
            if (status.getOccuapation().equalsIgnoreCase(occuapation)) {
                return status;
            }
        }
        throw new UserException(UserErrorStatus.BAD_REQUEST_OCCUPATION_STATUS_TYPE);
    }
}
