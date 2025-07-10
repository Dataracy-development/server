package com.dataracy.modules.behaviorlog.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 행동 로그 분석을 위한 액션 타입
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public enum ActionType {
    CLICK("클릭"),
    NAVIGATION("이동"),
    OTHER("기타");

    private final String description;

    public static ActionType fromNullableString(String value) {
        if (value == null || value.isBlank()) {
            return ActionType.OTHER; // 또는 DEFAULT, NAVIGATION, CLICK 등
        }
        try {
            return ActionType.valueOf(value);
        } catch (IllegalArgumentException e) {
            log.debug("알 수 없는 ActionType 값: {}, OTHER로 변환됨", value);
            return ActionType.OTHER;
        }
    }
}
