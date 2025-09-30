package com.dataracy.modules.dataset.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DataEsProjectionType 테스트")
class DataEsProjectionTypeTest {

    @Test
    @DisplayName("DataEsProjectionType enum 값들 확인")
    void dataEsProjectionType_ShouldHaveCorrectValues() {
        // Given & When & Then
        assertThat(DataEsProjectionType.values()).hasSize(2);
        
        assertThat(DataEsProjectionType.PENDING).isNotNull();
        assertThat(DataEsProjectionType.RETRYING).isNotNull();
    }

    @Test
    @DisplayName("DataEsProjectionType name() 메서드 테스트")
    void dataEsProjectionType_ShouldHaveCorrectNames() {
        // Given & When & Then
        assertThat(DataEsProjectionType.PENDING.name()).isEqualTo("PENDING");
        assertThat(DataEsProjectionType.RETRYING.name()).isEqualTo("RETRYING");
    }

    @Test
    @DisplayName("DataEsProjectionType valueOf() 메서드 테스트")
    void dataEsProjectionType_ShouldParseFromString() {
        // Given & When & Then
        assertThat(DataEsProjectionType.valueOf("PENDING")).isEqualTo(DataEsProjectionType.PENDING);
        assertThat(DataEsProjectionType.valueOf("RETRYING")).isEqualTo(DataEsProjectionType.RETRYING);
    }

    @Test
    @DisplayName("DataEsProjectionType ordinal() 메서드 테스트")
    void dataEsProjectionType_ShouldHaveCorrectOrdinals() {
        // Given & When & Then
        assertThat(DataEsProjectionType.PENDING.ordinal()).isZero();
        assertThat(DataEsProjectionType.RETRYING.ordinal()).isEqualTo(1);
    }

    @Test
    @DisplayName("DataEsProjectionType toString() 메서드 테스트")
    void dataEsProjectionType_ShouldHaveCorrectToString() {
        // Given & When & Then
        assertThat(DataEsProjectionType.PENDING).hasToString("PENDING");
        assertThat(DataEsProjectionType.RETRYING).hasToString("RETRYING");
    }
}
