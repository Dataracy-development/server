/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.behaviorlog.support.annotation;

import java.lang.annotation.*;

/** 사용자의 클릭 행동 추적용 AOP 어노테이션 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrackClick {
  String value() default "";
}
