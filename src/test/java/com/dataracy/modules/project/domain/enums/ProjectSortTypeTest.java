package com.dataracy.modules.project.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;

class ProjectSortTypeTest {

  @ParameterizedTest(name = "of - {0} 문자열로 {1} enum을 반환한다")
  @CsvSource({
    "LATEST, LATEST",
    "latest, LATEST",
    "OLDEST, OLDEST",
    "oldest, OLDEST",
    "MOST_LIKED, MOST_LIKED",
    "most_liked, MOST_LIKED",
    "MOST_VIEWED, MOST_VIEWED",
    "most_viewed, MOST_VIEWED",
    "MOST_COMMENTED, MOST_COMMENTED",
    "most_commented, MOST_COMMENTED",
    "LEAST_COMMENTED, LEAST_COMMENTED",
    "least_commented, LEAST_COMMENTED"
  })
  @DisplayName("of - 문자열로 해당 enum을 반환한다")
  void ofWhenValidStringReturnsCorrespondingEnum(String input, String expectedEnumName) {
    // when
    ProjectSortType result = ProjectSortType.of(input);

    // then
    assertThat(result.name()).isEqualTo(expectedEnumName);
  }

  @ParameterizedTest(name = "of - {0}로 ProjectException이 발생한다")
  @CsvSource({"INVALID, 'INVALID'", "null, null", "'', ''"})
  @DisplayName("of - 잘못된 입력으로 ProjectException이 발생한다")
  void ofWhenInvalidInputThrowsProjectException(String input, String expectedInput) {
    // when & then
    String actualInput = "null".equals(input) ? null : input;
    ProjectException exception =
        catchThrowableOfType(() -> ProjectSortType.of(actualInput), ProjectException.class);
    assertAll(
        () -> assertThat(exception).isNotNull(),
        () ->
            assertThat(exception)
                .hasFieldOrPropertyWithValue(
                    "errorCode", ProjectErrorStatus.INVALID_PROJECT_SORT_TYPE));
  }

  @Test
  @DisplayName("getValue - LATEST의 value를 반환한다")
  void getValueWhenLatestReturnsLatestValue() {
    // when
    String result = ProjectSortType.LATEST.getValue();

    // then
    assertThat(result).isEqualTo("LATEST");
  }

  @Test
  @DisplayName("getValue - OLDEST의 value를 반환한다")
  void getValueWhenOldestReturnsOldestValue() {
    // when
    String result = ProjectSortType.OLDEST.getValue();

    // then
    assertThat(result).isEqualTo("OLDEST");
  }

  @Test
  @DisplayName("getValue - MOST_LIKED의 value를 반환한다")
  void getValueWhenMostLikedReturnsMostLikedValue() {
    // when
    String result = ProjectSortType.MOST_LIKED.getValue();

    // then
    assertThat(result).isEqualTo("MOST_LIKED");
  }

  @Test
  @DisplayName("getValue - MOST_VIEWED의 value를 반환한다")
  void getValueWhenMostViewedReturnsMostViewedValue() {
    // when
    String result = ProjectSortType.MOST_VIEWED.getValue();

    // then
    assertThat(result).isEqualTo("MOST_VIEWED");
  }

  @Test
  @DisplayName("getValue - MOST_COMMENTED의 value를 반환한다")
  void getValueWhenMostCommentedReturnsMostCommentedValue() {
    // when
    String result = ProjectSortType.MOST_COMMENTED.getValue();

    // then
    assertThat(result).isEqualTo("MOST_COMMENTED");
  }

  @Test
  @DisplayName("getValue - LEAST_COMMENTED의 value를 반환한다")
  void getValueWhenLeastCommentedReturnsLeastCommentedValue() {
    // when
    String result = ProjectSortType.LEAST_COMMENTED.getValue();

    // then
    assertThat(result).isEqualTo("LEAST_COMMENTED");
  }
}
