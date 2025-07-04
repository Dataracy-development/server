package com.dataracy.modules.common.support.validator;

import com.dataracy.modules.common.support.annotation.ValidEnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class EnumValueValidator implements ConstraintValidator<ValidEnumValue, Object> {

    private Set<String> validValues;
    private boolean required;
    private String enumValuesString;

    @Override
    public void initialize(ValidEnumValue annotation) {
        this.required = annotation.required();
        this.validValues = new LinkedHashSet<>();

        Class<? extends Enum<?>> enumClass = annotation.enumClass();
        try {
            Field valueField = enumClass.getDeclaredField("value");
            valueField.setAccessible(true);

            for (Enum<?> constant : enumClass.getEnumConstants()) {
                Object val = valueField.get(constant);
                if (val instanceof String stringValue) {
                    validValues.add(stringValue);
                }
            }

            this.enumValuesString = String.join(", ", validValues);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Enum 클래스에서 'value' 필드를 찾을 수 없습니다.", e);
        }
    }

    @Override
    public boolean isValid(Object input, ConstraintValidatorContext context) {
        // null 처리
        if (input == null) return !required;

        // 단일 값 검증
        if (input instanceof String str) {
            boolean result = !str.isBlank() && validValues.contains(str);
            if (!result) injectCustomMessage(context);
            return result;
        }

        // 리스트 검증
        if (input instanceof List<?> list) {
            if (required && list.isEmpty()) {
                injectCustomMessage(context);
                return false;
            }

            for (Object item : list) {
                if (!(item instanceof String strItem) || strItem.isBlank() || !validValues.contains(strItem)) {
                    injectCustomMessage(context);
                    return false;
                }
            }
            return true;
        }

        // 지원하지 않는 타입
        injectCustomMessage(context);
        return false;
    }

    private void injectCustomMessage(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("허용되지 않는 값입니다. 사용 가능한 값: " + enumValuesString)
                .addConstraintViolation();
    }
}
