package com.dataracy.modules.common.validator;

import com.dataracy.modules.common.annotation.ValidEnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnumValueValidator implements ConstraintValidator<ValidEnumValue, Object> {

    private Set<String> validValues;
    private boolean required;

    @Override
    public void initialize(ValidEnumValue annotation) {
        this.required = annotation.required();
        this.validValues = new HashSet<>();

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
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Enum 클래스에서 'value' 필드를 찾을 수 없습니다.", e);
        }
    }

    @Override
    public boolean isValid(Object input, ConstraintValidatorContext context) {
        // null 처리
        if (input == null) return !required;

        // String 단일 값
        if (input instanceof String str) {
            return !str.isBlank() && validValues.contains(str);
        }

        // List<String>
        if (input instanceof List<?> list) {
            if (required && list.isEmpty()) return false;

            for (Object item : list) {
                if (!(item instanceof String strItem) || strItem.isBlank() || !validValues.contains(strItem)) {
                    return false;
                }
            }
            return true;
        }

        // 다른 타입은 지원하지 않음
        return false;
    }
}
