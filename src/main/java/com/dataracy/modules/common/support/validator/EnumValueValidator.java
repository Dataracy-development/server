/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.support.validator;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.dataracy.modules.common.support.annotation.ValidEnumValue;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValueValidator implements ConstraintValidator<ValidEnumValue, Object> {
  private Set<String> validValues;
  private boolean required;
  private String enumValuesString;

  /**
   * enum value값을 주입받아 초기화한다.
   *
   * <p>Enum의 getValue() public 메서드를 통해 값을 추출하여 캡슐화를 준수합니다.
   *
   * @param annotation Enum 유효성 애노테이션
   */
  @Override
  @SuppressWarnings("rawtypes")
  public void initialize(ValidEnumValue annotation) {
    this.required = annotation.required();
    this.validValues = new LinkedHashSet<>();

    Class<? extends Enum> enumClass = annotation.enumClass();
    try {
      // public getter 메서드를 통해 접근 (캡슐화 준수)
      Method getValueMethod = enumClass.getMethod("getValue");

      for (Enum constant : enumClass.getEnumConstants()) {
        Object val = getValueMethod.invoke(constant);
        if (val instanceof String stringValue) {
          validValues.add(stringValue);
        }
      }

      this.enumValuesString = String.join(", ", validValues);
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Enum 클래스에 public getValue() 메서드가 필요합니다: " + enumClass.getSimpleName(), e);
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
        if (!(item instanceof String strItem)
            || strItem.isBlank()
            || !validValues.contains(strItem)) {
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
    context
        .buildConstraintViolationWithTemplate("허용되지 않는 값입니다. 사용 가능한 값: " + enumValuesString)
        .addConstraintViolation();
  }
}
