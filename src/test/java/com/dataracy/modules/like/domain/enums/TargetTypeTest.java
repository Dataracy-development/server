package com.dataracy.modules.like.domain.enums;

import com.dataracy.modules.like.domain.exception.LikeException;
import com.dataracy.modules.like.domain.status.LikeErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TargetTypeTest {

    @Test
    @DisplayName("of - PROJECT 문자열로 PROJECT enum을 반환한다")
    void of_WhenProjectString_ReturnsProjectEnum() {
        // when
        TargetType result = TargetType.of("PROJECT");

        // then
        assertThat(result).isEqualTo(TargetType.PROJECT);
    }

    @Test
    @DisplayName("of - project 소문자로 PROJECT enum을 반환한다")
    void of_WhenProjectLowerCase_ReturnsProjectEnum() {
        // when
        TargetType result = TargetType.of("project");

        // then
        assertThat(result).isEqualTo(TargetType.PROJECT);
    }

    @Test
    @DisplayName("of - COMMENT 문자열로 COMMENT enum을 반환한다")
    void of_WhenCommentString_ReturnsCommentEnum() {
        // when
        TargetType result = TargetType.of("COMMENT");

        // then
        assertThat(result).isEqualTo(TargetType.COMMENT);
    }

    @Test
    @DisplayName("of - comment 소문자로 COMMENT enum을 반환한다")
    void of_WhenCommentLowerCase_ReturnsCommentEnum() {
        // when
        TargetType result = TargetType.of("comment");

        // then
        assertThat(result).isEqualTo(TargetType.COMMENT);
    }

    @Test
    @DisplayName("of - 유효하지 않은 문자열로 LikeException이 발생한다")
    void of_WhenInvalidString_ThrowsLikeException() {
        // when & then
        assertThatThrownBy(() -> TargetType.of("INVALID"))
                .isInstanceOf(LikeException.class)
                .hasFieldOrPropertyWithValue("errorCode", LikeErrorStatus.INVALID_TARGET_TYPE);
    }

    @Test
    @DisplayName("of - null로 LikeException이 발생한다")
    void of_WhenNull_ThrowsLikeException() {
        // when & then
        assertThatThrownBy(() -> TargetType.of(null))
                .isInstanceOf(LikeException.class)
                .hasFieldOrPropertyWithValue("errorCode", LikeErrorStatus.INVALID_TARGET_TYPE);
    }

    @Test
    @DisplayName("of - 빈 문자열로 LikeException이 발생한다")
    void of_WhenEmptyString_ThrowsLikeException() {
        // when & then
        assertThatThrownBy(() -> TargetType.of(""))
                .isInstanceOf(LikeException.class)
                .hasFieldOrPropertyWithValue("errorCode", LikeErrorStatus.INVALID_TARGET_TYPE);
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