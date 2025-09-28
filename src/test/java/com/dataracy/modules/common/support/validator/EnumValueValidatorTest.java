package com.dataracy.modules.common.support.validator;

import com.dataracy.modules.common.support.annotation.ValidEnumValue;
import com.dataracy.modules.user.domain.enums.RoleType;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EnumValueValidatorTest {

    private EnumValueValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new EnumValueValidator();
        context = mock(ConstraintValidatorContext.class);
        
        // ConstraintValidatorContext mock 설정
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(org.mockito.ArgumentMatchers.anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    @DisplayName("initialize - RoleType enum으로 초기화한다")
    void initialize_WithRoleTypeEnum_InitializesSuccessfully() {
        // given
        ValidEnumValue annotation = mock(ValidEnumValue.class);
        given(annotation.enumClass()).willReturn((Class) RoleType.class);
        given(annotation.required()).willReturn(false);

        // when
        validator.initialize(annotation);

        // then
        assertThat(validator).isNotNull();
    }

    @Test
    @DisplayName("isValid - 유효한 enum 값인 경우 true를 반환한다")
    void isValid_WhenValidEnumValue_ReturnsTrue() {
        // given
        ValidEnumValue annotation = mock(ValidEnumValue.class);
        given(annotation.enumClass()).willReturn((Class) RoleType.class);
        given(annotation.required()).willReturn(false);
        validator.initialize(annotation);

        // when
        boolean result = validator.isValid("ROLE_USER", context);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isValid - 유효한 enum 값(소문자)인 경우 false를 반환한다")
    void isValid_WhenValidEnumValueLowerCase_ReturnsFalse() {
        // given
        ValidEnumValue annotation = mock(ValidEnumValue.class);
        given(annotation.enumClass()).willReturn((Class) RoleType.class);
        given(annotation.required()).willReturn(false);
        validator.initialize(annotation);

        // when
        boolean result = validator.isValid("role_user", context);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValid - 유효하지 않은 enum 값인 경우 false를 반환한다")
    void isValid_WhenInvalidEnumValue_ReturnsFalse() {
        // given
        ValidEnumValue annotation = mock(ValidEnumValue.class);
        given(annotation.enumClass()).willReturn((Class) RoleType.class);
        given(annotation.required()).willReturn(false);
        validator.initialize(annotation);

        // when
        boolean result = validator.isValid("INVALID_ROLE", context);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValid - null 값이고 required가 false인 경우 true를 반환한다")
    void isValid_WhenNullAndNotRequired_ReturnsTrue() {
        // given
        ValidEnumValue annotation = mock(ValidEnumValue.class);
        given(annotation.enumClass()).willReturn((Class) RoleType.class);
        given(annotation.required()).willReturn(false);
        validator.initialize(annotation);

        // when
        boolean result = validator.isValid(null, context);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isValid - null 값이고 required가 true인 경우 false를 반환한다")
    void isValid_WhenNullAndRequired_ReturnsFalse() {
        // given
        ValidEnumValue annotation = mock(ValidEnumValue.class);
        given(annotation.enumClass()).willReturn((Class) RoleType.class);
        given(annotation.required()).willReturn(true);
        validator.initialize(annotation);

        // when
        boolean result = validator.isValid(null, context);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValid - 빈 문자열이고 required가 false인 경우 false를 반환한다")
    void isValid_WhenEmptyStringAndNotRequired_ReturnsFalse() {
        // given
        ValidEnumValue annotation = mock(ValidEnumValue.class);
        given(annotation.enumClass()).willReturn((Class) RoleType.class);
        given(annotation.required()).willReturn(false);
        validator.initialize(annotation);

        // when
        boolean result = validator.isValid("", context);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValid - 빈 문자열이고 required가 true인 경우 false를 반환한다")
    void isValid_WhenEmptyStringAndRequired_ReturnsFalse() {
        // given
        ValidEnumValue annotation = mock(ValidEnumValue.class);
        given(annotation.enumClass()).willReturn((Class) RoleType.class);
        given(annotation.required()).willReturn(true);
        validator.initialize(annotation);

        // when
        boolean result = validator.isValid("", context);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValid - 공백 문자열이고 required가 false인 경우 false를 반환한다")
    void isValid_WhenWhitespaceStringAndNotRequired_ReturnsFalse() {
        // given
        ValidEnumValue annotation = mock(ValidEnumValue.class);
        given(annotation.enumClass()).willReturn((Class) RoleType.class);
        given(annotation.required()).willReturn(false);
        validator.initialize(annotation);

        // when
        boolean result = validator.isValid("   ", context);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValid - 공백 문자열이고 required가 true인 경우 false를 반환한다")
    void isValid_WhenWhitespaceStringAndRequired_ReturnsFalse() {
        // given
        ValidEnumValue annotation = mock(ValidEnumValue.class);
        given(annotation.enumClass()).willReturn((Class) RoleType.class);
        given(annotation.required()).willReturn(true);
        validator.initialize(annotation);

        // when
        boolean result = validator.isValid("   ", context);

        // then
        assertThat(result).isFalse();
    }
}