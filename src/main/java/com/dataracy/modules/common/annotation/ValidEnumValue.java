package com.dataracy.modules.common.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = com.dataracy.modules.common.validator.EnumValueValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnumValue {

    String message() default "허용되지 않는 값입니다. 사용 가능한 값: {enumValues}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> enumClass();
    boolean required() default false;
}
