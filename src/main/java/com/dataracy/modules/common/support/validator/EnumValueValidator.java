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

    /**
     * enum value값을 주입받아 초기화한다.
     *
     * @param annotation Enum 유효성 애노테이션
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void initialize(ValidEnumValue annotation) {
        this.required = annotation.required();
        this.validValues = new LinkedHashSet<>();

        Class<? extends Enum> enumClass = annotation.enumClass();
        try {
            Field valueField = enumClass.getDeclaredField("value");
            valueField.setAccessible(true);

            for (Enum constant : enumClass.getEnumConstants()) {
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

    /**
     * 클라이언트로부터 전달받은 enum 값의 유효성을 검증한다.
     *
     * @param input 클라이언트로부터 전달받은 입력 값
     * @param context enum 유효성 검증
     * @return 유효성 검증 성공 여부
     */
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

    /**
     * 잘못된 값을 입력받았을 경우 예외처리
     *
     * @param context enum 유효성 검증
     */
    private void injectCustomMessage(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("허용되지 않는 값입니다. 사용 가능한 값: " + enumValuesString)
                .addConstraintViolation();
    }
}
