package com.dataracy.modules.project.domain.enums;

import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProjectSortTypeTest {

    @Test
    @DisplayName("of() - 문자열로 Enum 매핑 성공")
    void of_shouldReturnEnum_whenValidInput() {
        // given
        String input1 = "LATEST";
        String input2 = "most_viewed"; // 소문자, 언더스코어

        // when
        ProjectSortType result1 = ProjectSortType.of(input1);
        ProjectSortType result2 = ProjectSortType.of(input2);

        // then
        assertThat(result1).isEqualTo(ProjectSortType.LATEST);
        assertThat(result2).isEqualTo(ProjectSortType.MOST_VIEWED);
    }

    @Test
    @DisplayName("of() - 잘못된 입력 시 ProjectException 발생")
    void of_shouldThrow_whenInvalidInput() {
        // given
        String wrongInput = "WRONG_TYPE";

        // when
        ProjectException ex = catchThrowableOfType(() -> ProjectSortType.of(wrongInput), ProjectException.class);

        // then
        assertThat(ex).isNotNull();
        assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.INVALID_PROJECT_SORT_TYPE);
    }
}

