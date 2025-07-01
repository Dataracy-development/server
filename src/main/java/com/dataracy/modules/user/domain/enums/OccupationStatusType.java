package com.dataracy.modules.user.domain.enums;

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

    private final String value;
}
