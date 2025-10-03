/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.behaviorlog.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 로그 타입 */
@Getter
@RequiredArgsConstructor
public enum LogType {
  ACTION("행동"),
  ERROR("에러");

  private final String description;
}
