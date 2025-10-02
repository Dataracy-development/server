package com.dataracy.modules.like.domain.enums;

import com.dataracy.modules.like.domain.exception.LikeException;
import com.dataracy.modules.like.domain.status.LikeErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

class TargetTypeTest {

    @ParameterizedTest(name = "of - {0} 문자열로 {1} enum을 반환한다")
    @CsvSource({
            "PROJECT, PROJECT",
            "project, PROJECT",
            "COMMENT, COMMENT",
            "comment, COMMENT"
    })
    @DisplayName("of - 문자열로 해당 enum을 반환한다")
    void of_WhenValidString_ReturnsCorrespondingEnum(String input, String expectedEnumName) {
        // when
        TargetType result = TargetType.of(input);

        // then
        assertThat(result.name()).isEqualTo(expectedEnumName);
    }

    @ParameterizedTest(name = "of - {0}로 LikeException이 발생한다")
    @CsvSource({
            "INVALID, 'INVALID'",
            "null, null",
            "'', ''"
    })
    @DisplayName("of - 잘못된 입력으로 LikeException이 발생한다")
    void of_WhenInvalidInput_ThrowsLikeException(String input, String expectedInput) {
        // when & then
        String actualInput = "null".equals(input) ? null : input;
        LikeException exception = catchThrowableOfType(
                () -> TargetType.of(actualInput),
                LikeException.class
        );
        assertAll(
                () -> assertThat(exception).isNotNull(),
                () -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", LikeErrorStatus.INVALID_TARGET_TYPE)
        );
    }

    @Test
    @DisplayName("getValue - PROJECT의 value를 반환한다")
    void getValue_WhenProject_ReturnsProjectValue() {
        // when
        String result = TargetType.PROJECT.getValue();

        // then
        assertThat(result).isEqualTo("PROJECT");
    }

    @Test
    @DisplayName("getValue - COMMENT의 value를 반환한다")
    void getValue_WhenComment_ReturnsCommentValue() {
        // when
        String result = TargetType.COMMENT.getValue();

        // then
        assertThat(result).isEqualTo("COMMENT");
    }
}