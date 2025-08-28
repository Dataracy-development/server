package com.dataracy.modules.comment.adapter.web.request.command;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ModifyCommentWebRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("content 값이 정상일 경우 → 검증 통과")
    void validContent() {
        // given
        ModifyCommentWebRequest req = new ModifyCommentWebRequest("수정할 내용");

        // when
        Set<ConstraintViolation<ModifyCommentWebRequest>> violations = validator.validate(req);

        // then
        assertThat(violations).isEmpty();
        assertThat(req.content()).isEqualTo("수정할 내용");
    }

    @Test
    @DisplayName("content 값이 blank → 검증 실패")
    void invalidContentBlank() {
        // given
        ModifyCommentWebRequest req = new ModifyCommentWebRequest("   ");

        // when
        Set<ConstraintViolation<ModifyCommentWebRequest>> violations = validator.validate(req);

        // then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("content 값이 null → 검증 실패")
    void invalidContentNull() {
        // given
        ModifyCommentWebRequest req = new ModifyCommentWebRequest(null);

        // when
        Set<ConstraintViolation<ModifyCommentWebRequest>> violations = validator.validate(req);

        // then
        assertThat(violations).isNotEmpty();
    }
}
