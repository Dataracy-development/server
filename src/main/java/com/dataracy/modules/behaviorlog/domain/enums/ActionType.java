package com.dataracy.modules.behaviorlog.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 행동 로그 분석을 위한 액션 타입
 */
@Getter
@RequiredArgsConstructor
public enum ActionType {
    CLICK("클릭"),
    NAVIGATION("이동"),
    OTHER("기타");

    private final String description;
}
