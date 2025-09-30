package com.dataracy.modules.common.support.validator;

import com.dataracy.modules.common.support.annotation.ValidEnumValue;
import com.dataracy.modules.user.domain.enums.RoleType;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest
    @CsvSource({
            "role_user, false, false",         // 소문자 enum 값
            "INVALID_ROLE, false, false",      // 유효하지 않은 enum
            ", false, true",                   // null이고 required=false
            ", true, false",                    // null이고 required=true
            "'', false, false",                 // 빈 문자열이고 required=false
            "'', true, false",                  // 빈 문자열이고 required=true
            "'   ', false, false",              // 공백이고 required=false
            "'   ', true, false"                // 공백이고 required=true
    })
    @DisplayName("isValid - 다양한 유효하지 않은 값들에 대한 검증")
    void isValid_WhenVariousInvalidValues_ReturnsExpectedResult(String value, boolean required, boolean expectedResult) {
        // given
        ValidEnumValue annotation = mock(ValidEnumValue.class);
        given(annotation.enumClass()).willReturn((Class) RoleType.class);
        given(annotation.required()).willReturn(required);
        validator.initialize(annotation);

        // when
        boolean result = validator.isValid(value, context);

        // then
        assertThat(result).isEqualTo(expectedResult);
    }
}