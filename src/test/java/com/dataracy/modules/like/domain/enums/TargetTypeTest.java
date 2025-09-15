package com.dataracy.modules.like.domain.enums;

import com.dataracy.modules.like.domain.exception.LikeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class TargetTypeTest {

    @Nested
    @DisplayName("유효한 문자열 매핑")
    class ValidMappings {

        @Test
        @DisplayName("PROJECT → 대소문자 무시하고 매핑된다")
        void shouldMapProjectCaseInsensitively() {
            // when
            TargetType upper = TargetType.of("PROJECT");
            TargetType lower = TargetType.of("project");
            TargetType mixed = TargetType.of("PrOjEcT");

            // then
            assertThat(upper).isEqualTo(TargetType.PROJECT);
            assertThat(lower).isEqualTo(TargetType.PROJECT);
            assertThat(mixed).isEqualTo(TargetType.PROJECT);
        }

        @Test
        @DisplayName("COMMENT → 대소문자 무시하고 매핑된다")
        void shouldMapCommentCaseInsensitively() {
            // when
            TargetType upper = TargetType.of("COMMENT");
            TargetType lower = TargetType.of("comment");
            TargetType mixed = TargetType.of("CoMmEnT");

            // then
            assertThat(upper).isEqualTo(TargetType.COMMENT);
            assertThat(lower).isEqualTo(TargetType.COMMENT);
            assertThat(mixed).isEqualTo(TargetType.COMMENT);
        }
    }

    @Nested
    @DisplayName("잘못된 문자열 처리")
    class InvalidMappings {

        @Test
        @DisplayName("정의되지 않은 값이면 LikeException 발생")
        void shouldThrowLikeExceptionForInvalidValue() {
            // when
            LikeException ex = catchThrowableOfType(
                    () -> TargetType.of("POST"),
                    LikeException.class
            );

            // then
            assertThat(ex).isNotNull();
        }
    }
}
