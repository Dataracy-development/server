package com.dataracy.modules.common.support.annotation;

import com.dataracy.modules.common.support.validator.EnumValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 해당하는 ENUM의 값 리스트에 속하는지 유효성을 체크하는 어노테이션
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EnumValueValidator.class)
public @interface ValidEnumValue {
    String message() default "허용되지 않는 값입니다. 사용 가능한 값: {enumValues}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> enumClass();
    boolean required() default false;
}
