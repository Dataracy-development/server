package com.dataracy.modules.common.support.annotation;

import java.lang.annotation.*;

/** 파라미터에 붙여 유저 id를 주입받는 애노테이션 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUserId {}
