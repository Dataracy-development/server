package com.dataracy.modules.like.domain.enums;

import com.dataracy.modules.like.domain.exception.LikeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TargetTypeTest {

    @Test
    @DisplayName("of_should_map_valid_project_names_case_insensitively")
    void of_should_map_valid_project_names_case_insensitively() {
        // given / when
        TargetType upper = TargetType.of("PROJECT");
        TargetType lower = TargetType.of("project");
        TargetType name  = TargetType.of("PrOjEcT");

        // then
        assertThat(upper).isEqualTo(TargetType.PROJECT);
        assertThat(lower).isEqualTo(TargetType.PROJECT);
        assertThat(name).isEqualTo(TargetType.PROJECT);
    }

    @Test
    @DisplayName("of_should_map_valid_comment_names_case_insensitively")
    void of_should_map_valid_comment_names_case_insensitively() {
        // given / when
        TargetType upper = TargetType.of("COMMENT");
        TargetType lower = TargetType.of("comment");
        TargetType name  = TargetType.of("CoMmEnT");

        // then
        assertThat(upper).isEqualTo(TargetType.COMMENT);
        assertThat(lower).isEqualTo(TargetType.COMMENT);
        assertThat(name).isEqualTo(TargetType.COMMENT);
    }

    @Test
    @DisplayName("of_should_throw_LikeException_for_invalid_value")
    void of_should_throw_LikeException_for_invalid_value() {
        // when
        LikeException ex = catchThrowableOfType(() -> TargetType.of("POST"), LikeException.class);

        // then
        assertThat(ex).isNotNull();
    }
}
