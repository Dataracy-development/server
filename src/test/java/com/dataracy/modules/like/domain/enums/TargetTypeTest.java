package com.dataracy.modules.like.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.dataracy.modules.like.domain.exception.LikeException;
import com.dataracy.modules.like.domain.status.LikeErrorStatus;

class TargetTypeTest {

  @ParameterizedTest(name = "of - {0} 문자열로 {1} enum을 반환한다")
  @CsvSource({"PROJECT, PROJECT", "project, PROJECT", "COMMENT, COMMENT", "comment, COMMENT"})
  @DisplayName("of - 문자열로 해당 enum을 반환한다")
  void ofWhenValidStringReturnsCorrespondingEnum(String input, String expectedEnumName) {
    // when
    TargetType result = TargetType.of(input);

    // then
    assertThat(result.name()).isEqualTo(expectedEnumName);
  }

  @ParameterizedTest(name = "of - {0}로 LikeException이 발생한다")
  @CsvSource({"INVALID, 'INVALID'", "null, null", "'', ''"})
  @DisplayName("of - 잘못된 입력으로 LikeException이 발생한다")
  void ofWhenInvalidInputThrowsLikeException(String input, String expectedInput) {
    // when & then
    String actualInput = "null".equals(input) ? null : input;
    LikeException exception =
        catchThrowableOfType(() -> TargetType.of(actualInput), LikeException.class);
    assertThat(exception)
        .isNotNull()
        .hasFieldOrPropertyWithValue("errorCode", LikeErrorStatus.INVALID_TARGET_TYPE);
  }

  @Test
  @DisplayName("getValue - PROJECT의 value를 반환한다")
  void getValueWhenProjectReturnsProjectValue() {
    // when
    String result = TargetType.PROJECT.getValue();

    // then
    assertThat(result).isEqualTo("PROJECT");
  }

  @Test
  @DisplayName("getValue - COMMENT의 value를 반환한다")
  void getValueWhenCommentReturnsCommentValue() {
    // when
    String result = TargetType.COMMENT.getValue();

    // then
    assertThat(result).isEqualTo("COMMENT");
  }

  @Test
  @DisplayName("values - 모든 enum 값들을 반환한다")
  void valuesReturnsAllEnumValues() {
    // when
    TargetType[] values = TargetType.values();

    // then
    assertThat(values).hasSize(2).containsExactly(TargetType.PROJECT, TargetType.COMMENT);
  }

  @Test
  @DisplayName("valueOf - PROJECT 문자열로 PROJECT enum을 반환한다")
  void valueOfWhenProjectStringReturnsProjectEnum() {
    // when
    TargetType result = TargetType.valueOf("PROJECT");

    // then
    assertThat(result).isEqualTo(TargetType.PROJECT);
  }

  @Test
  @DisplayName("valueOf - COMMENT 문자열로 COMMENT enum을 반환한다")
  void valueOfWhenCommentStringReturnsCommentEnum() {
    // when
    TargetType result = TargetType.valueOf("COMMENT");

    // then
    assertThat(result).isEqualTo(TargetType.COMMENT);
  }
}
