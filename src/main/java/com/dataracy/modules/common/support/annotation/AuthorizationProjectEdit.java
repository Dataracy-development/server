package com.dataracy.modules.common.support.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthorizationProjectEdit {
    boolean restore() default false;
}
