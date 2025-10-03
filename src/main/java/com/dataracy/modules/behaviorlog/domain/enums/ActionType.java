/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.behaviorlog.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 행동 로그 분석을 위한 액션 타입 */
@Slf4j
@Getter
@RequiredArgsConstructor
public enum ActionType {
  CLICK("클릭"),
  NAVIGATION("이동"),
  OTHER("기타");

  private final String description;

  /**
   * 주어진 문자열을 ActionType으로 변환합니다.
   *
   * <p>입력값이 null이거나 비어 있으면 OTHER를 반환하며, 일치하는 enum 상수가 없을 경우에도 OTHER를 반환합니다.
   *
   * @param value 변환할 문자열 값
   * @return 변환된 ActionType, 알 수 없는 값 또는 빈 값일 경우 OTHER
   */
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
