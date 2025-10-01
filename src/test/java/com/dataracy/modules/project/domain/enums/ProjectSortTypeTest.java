package com.dataracy.modules.project.domain.enums;

import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

class ProjectSortTypeTest {

    @Test
    @DisplayName("of - LATEST 문자열로 LATEST enum을 반환한다")
    void of_WhenLatestString_ReturnsLatestEnum() {
        // when
        ProjectSortType result = ProjectSortType.of("LATEST");

        // then
        assertThat(result).isEqualTo(ProjectSortType.LATEST);
    }

    @Test
    @DisplayName("of - latest 소문자로 LATEST enum을 반환한다")
    void of_WhenLatestLowerCase_ReturnsLatestEnum() {
        // when
        ProjectSortType result = ProjectSortType.of("latest");

        // then
        assertThat(result).isEqualTo(ProjectSortType.LATEST);
    }

    @Test
    @DisplayName("of - OLDEST 문자열로 OLDEST enum을 반환한다")
    void of_WhenOldestString_ReturnsOldestEnum() {
        // when
        ProjectSortType result = ProjectSortType.of("OLDEST");

        // then
        assertThat(result).isEqualTo(ProjectSortType.OLDEST);
    }

    @Test
    @DisplayName("of - oldest 소문자로 OLDEST enum을 반환한다")
    void of_WhenOldestLowerCase_ReturnsOldestEnum() {
        // when
        ProjectSortType result = ProjectSortType.of("oldest");

        // then
        assertThat(result).isEqualTo(ProjectSortType.OLDEST);
    }

    @Test
    @DisplayName("of - MOST_LIKED 문자열로 MOST_LIKED enum을 반환한다")
    void of_WhenMostLikedString_ReturnsMostLikedEnum() {
        // when
        ProjectSortType result = ProjectSortType.of("MOST_LIKED");

        // then
        assertThat(result).isEqualTo(ProjectSortType.MOST_LIKED);
    }

    @Test
    @DisplayName("of - most_liked 소문자로 MOST_LIKED enum을 반환한다")
    void of_WhenMostLikedLowerCase_ReturnsMostLikedEnum() {
        // when
        ProjectSortType result = ProjectSortType.of("most_liked");

        // then
        assertThat(result).isEqualTo(ProjectSortType.MOST_LIKED);
    }

    @Test
    @DisplayName("of - MOST_VIEWED 문자열로 MOST_VIEWED enum을 반환한다")
    void of_WhenMostViewedString_ReturnsMostViewedEnum() {
        // when
        ProjectSortType result = ProjectSortType.of("MOST_VIEWED");

        // then
        assertThat(result).isEqualTo(ProjectSortType.MOST_VIEWED);
    }

    @Test
    @DisplayName("of - most_viewed 소문자로 MOST_VIEWED enum을 반환한다")
    void of_WhenMostViewedLowerCase_ReturnsMostViewedEnum() {
        // when
        ProjectSortType result = ProjectSortType.of("most_viewed");

        // then
        assertThat(result).isEqualTo(ProjectSortType.MOST_VIEWED);
    }

    @Test
    @DisplayName("of - MOST_COMMENTED 문자열로 MOST_COMMENTED enum을 반환한다")
    void of_WhenMostCommentedString_ReturnsMostCommentedEnum() {
        // when
        ProjectSortType result = ProjectSortType.of("MOST_COMMENTED");

        // then
        assertThat(result).isEqualTo(ProjectSortType.MOST_COMMENTED);
    }

    @Test
    @DisplayName("of - most_commented 소문자로 MOST_COMMENTED enum을 반환한다")
    void of_WhenMostCommentedLowerCase_ReturnsMostCommentedEnum() {
        // when
        ProjectSortType result = ProjectSortType.of("most_commented");

        // then
        assertThat(result).isEqualTo(ProjectSortType.MOST_COMMENTED);
    }

    @Test
    @DisplayName("of - LEAST_COMMENTED 문자열로 LEAST_COMMENTED enum을 반환한다")
    void of_WhenLeastCommentedString_ReturnsLeastCommentedEnum() {
        // when
        ProjectSortType result = ProjectSortType.of("LEAST_COMMENTED");

        // then
        assertThat(result).isEqualTo(ProjectSortType.LEAST_COMMENTED);
    }

    @Test
    @DisplayName("of - least_commented 소문자로 LEAST_COMMENTED enum을 반환한다")
    void of_WhenLeastCommentedLowerCase_ReturnsLeastCommentedEnum() {
        // when
        ProjectSortType result = ProjectSortType.of("least_commented");

        // then
        assertThat(result).isEqualTo(ProjectSortType.LEAST_COMMENTED);
    }

    @Test
    @DisplayName("of - 유효하지 않은 문자열로 ProjectException이 발생한다")
    void of_WhenInvalidString_ThrowsProjectException() {
        // when & then
        ProjectException exception = catchThrowableOfType(
                () -> ProjectSortType.of("INVALID"),
                ProjectException.class
        );
        assertAll(
                () -> org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(ProjectException.class),
                () -> org.assertj.core.api.Assertions.assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ProjectErrorStatus.INVALID_PROJECT_SORT_TYPE)
        );
    }

    @Test
    @DisplayName("of - null로 ProjectException이 발생한다")
    void of_WhenNull_ThrowsProjectException() {
        // when & then
        ProjectException exception = catchThrowableOfType(
                () -> ProjectSortType.of(null),
                ProjectException.class
        );
        assertAll(
                () -> org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(ProjectException.class),
                () -> org.assertj.core.api.Assertions.assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ProjectErrorStatus.INVALID_PROJECT_SORT_TYPE)
        );
    }

    @Test
    @DisplayName("of - 빈 문자열로 ProjectException이 발생한다")
    void of_WhenEmptyString_ThrowsProjectException() {
        // when & then
        ProjectException exception = catchThrowableOfType(
                () -> ProjectSortType.of(""),
                ProjectException.class
        );
        assertAll(
                () -> org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(ProjectException.class),
                () -> org.assertj.core.api.Assertions.assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ProjectErrorStatus.INVALID_PROJECT_SORT_TYPE)
        );
    }

    @Test
    @DisplayName("getValue - LATEST의 value를 반환한다")
    void getValue_WhenLatest_ReturnsLatestValue() {
        // when
        String result = ProjectSortType.LATEST.getValue();

        // then
        assertThat(result).isEqualTo("LATEST");
    }

    @Test
    @DisplayName("getValue - OLDEST의 value를 반환한다")
    void getValue_WhenOldest_ReturnsOldestValue() {
        // when
        String result = ProjectSortType.OLDEST.getValue();

        // then
        assertThat(result).isEqualTo("OLDEST");
    }

    @Test
    @DisplayName("getValue - MOST_LIKED의 value를 반환한다")
    void getValue_WhenMostLiked_ReturnsMostLikedValue() {
        // when
        String result = ProjectSortType.MOST_LIKED.getValue();

        // then
        assertThat(result).isEqualTo("MOST_LIKED");
    }

    @Test
    @DisplayName("getValue - MOST_VIEWED의 value를 반환한다")
    void getValue_WhenMostViewed_ReturnsMostViewedValue() {
        // when
        String result = ProjectSortType.MOST_VIEWED.getValue();

        // then
        assertThat(result).isEqualTo("MOST_VIEWED");
    }

    @Test
    @DisplayName("getValue - MOST_COMMENTED의 value를 반환한다")
    void getValue_WhenMostCommented_ReturnsMostCommentedValue() {
        // when
        String result = ProjectSortType.MOST_COMMENTED.getValue();

        // then
        assertThat(result).isEqualTo("MOST_COMMENTED");
    }

    @Test
    @DisplayName("getValue - LEAST_COMMENTED의 value를 반환한다")
    void getValue_WhenLeastCommented_ReturnsLeastCommentedValue() {
        // when
        String result = ProjectSortType.LEAST_COMMENTED.getValue();

        // then
        assertThat(result).isEqualTo("LEAST_COMMENTED");
    }
}