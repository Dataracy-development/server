package com.dataracy.modules.reference.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("AnalysisPurpose 테스트")
class AnalysisPurposeTest {

    @Test
    @DisplayName("AnalysisPurpose record 생성 및 속성 확인")
    void analysisPurpose_ShouldCreateCorrectly() {
        // Given
        Long id = 1L;
        String value = "research";
        String label = "연구";

        // When
        AnalysisPurpose analysisPurpose = new AnalysisPurpose(id, value, label);

        // Then
        assertAll(
                () -> assertThat(analysisPurpose.id()).isEqualTo(id),
                () -> assertThat(analysisPurpose.value()).isEqualTo(value),
                () -> assertThat(analysisPurpose.label()).isEqualTo(label)
        );
    }

    @Test
    @DisplayName("AnalysisPurpose record equals 및 hashCode 테스트")
    void analysisPurpose_ShouldHaveCorrectEqualsAndHashCode() {
        // Given
        AnalysisPurpose analysisPurpose1 = new AnalysisPurpose(1L, "business", "비즈니스");
        AnalysisPurpose analysisPurpose2 = new AnalysisPurpose(1L, "business", "비즈니스");
        AnalysisPurpose analysisPurpose3 = new AnalysisPurpose(2L, "business", "비즈니스");

        // When & Then
        assertThat(analysisPurpose1)
                .isEqualTo(analysisPurpose2)
                .hasSameHashCodeAs(analysisPurpose2)
                .isNotEqualTo(analysisPurpose3);
    }

    @Test
    @DisplayName("AnalysisPurpose record toString 테스트")
    void analysisPurpose_ShouldHaveCorrectToString() {
        // Given
        AnalysisPurpose analysisPurpose = new AnalysisPurpose(1L, "education", "교육");

        // When
        String toString = analysisPurpose.toString();

        // Then
        assertThat(toString)
                .contains("AnalysisPurpose")
                .contains("1")
                .contains("education")
                .contains("교육");
    }

    @Test
    @DisplayName("AnalysisPurpose record - null 값 처리")
    void analysisPurpose_ShouldHandleNullValues() {
        // Given & When
        AnalysisPurpose analysisPurpose = new AnalysisPurpose(null, null, null);

        // Then
        assertAll(
                () -> assertThat(analysisPurpose.id()).isNull(),
                () -> assertThat(analysisPurpose.value()).isNull(),
                () -> assertThat(analysisPurpose.label()).isNull()
        );
    }

    @Test
    @DisplayName("AnalysisPurpose record - 다양한 분석 목적들 테스트")
    void analysisPurpose_ShouldHandleVariousPurposes() {
        // Given & When
        AnalysisPurpose analysisPurpose1 = new AnalysisPurpose(1L, "research", "연구");
        AnalysisPurpose analysisPurpose2 = new AnalysisPurpose(2L, "business", "비즈니스");
        AnalysisPurpose analysisPurpose3 = new AnalysisPurpose(3L, "education", "교육");

        // Then
        assertAll(
                () -> assertThat(analysisPurpose1.value()).isEqualTo("research"),
                () -> assertThat(analysisPurpose2.value()).isEqualTo("business"),
                () -> assertThat(analysisPurpose3.value()).isEqualTo("education"),
                () -> assertThat(analysisPurpose1.label()).isEqualTo("연구"),
                () -> assertThat(analysisPurpose2.label()).isEqualTo("비즈니스"),
                () -> assertThat(analysisPurpose3.label()).isEqualTo("교육")
        );
    }
}
