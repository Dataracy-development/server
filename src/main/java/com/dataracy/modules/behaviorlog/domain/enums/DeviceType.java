package com.dataracy.modules.behaviorlog.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 접속한 디바이스 타입
 */
@Getter
@RequiredArgsConstructor
public enum DeviceType {
    MOBILE("모바일"),
    PC("PC"),
    TABLET("태블릿"),
    UNKNOWN("기타");

    private final String description;
}
