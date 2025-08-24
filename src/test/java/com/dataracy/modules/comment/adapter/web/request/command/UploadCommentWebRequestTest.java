package com.dataracy.modules.comment.adapter.web.request.command;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UploadCommentWebRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("content와 parentCommentId가 정상 → 검증 통과")
    void validRequest() {
        // given
        UploadCommentWebRequest req = new UploadCommentWebRequest("댓글 내용", 1L);

        // when
        Set<ConstraintViolation<UploadCommentWebRequest>> violations = validator.validate(req);

        // then
        assertThat(violations).isEmpty();
        assertThat(req.content()).isEqualTo("댓글 내용");
        assertThat(req.parentCommentId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("content가 blank → 검증 실패")
    void invalidContentBlank() {
        // given
        UploadCommentWebRequest req = new UploadCommentWebRequest(" ", 1L);

        // when
        Set<ConstraintViolation<UploadCommentWebRequest>> violations = validator.validate(req);

        // then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("parentCommentId가 null → 루트 댓글 허용")
    void nullParentId() {
        // given
        UploadCommentWebRequest req = new UploadCommentWebRequest("내용", null);

        // when
        Set<ConstraintViolation<UploadCommentWebRequest>> violations = validator.validate(req);

        // then
        assertThat(violations).isEmpty();
        assertThat(req.parentCommentId()).isNull();
    }

    @Test
    @DisplayName("content 비어있음 → 검증 실패")
    void invalidContentEmpty() {
        // given
        UploadCommentWebRequest req = new UploadCommentWebRequest("   ", 1L);

        // when
        Set<ConstraintViolation<UploadCommentWebRequest>> violations = validator.validate(req);

        // then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("parentCommentId 음수 → 검증 실패")
    void invalidParentIdNegative() {
        // given
        UploadCommentWebRequest req = new UploadCommentWebRequest("내용", -5L);

        // when
        Set<ConstraintViolation<UploadCommentWebRequest>> violations = validator.validate(req);

        // then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("정상 값 → 검증 통과")
    void validRequestOk() {
        // given
        UploadCommentWebRequest req = new UploadCommentWebRequest("내용", 10L);

        // when
        Set<ConstraintViolation<UploadCommentWebRequest>> violations = validator.validate(req);

        // then
        assertThat(violations).isEmpty();
    }
}
