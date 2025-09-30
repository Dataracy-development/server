package com.dataracy.modules.behaviorlog.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LogType 테스트")
class LogTypeTest {

    @Test
    @DisplayName("LogType enum 값들 확인")
    void logType_ShouldHaveCorrectValues() {
        // Given & When & Then
        assertThat(LogType.values()).hasSize(2);
        
        assertThat(LogType.ACTION).isNotNull();
        assertThat(LogType.ERROR).isNotNull();
    }

    @Test
    @DisplayName("LogType name() 메서드 테스트")
    void logType_ShouldHaveCorrectNames() {
        // Given & When & Then
        assertThat(LogType.ACTION.name()).isEqualTo("ACTION");
        assertThat(LogType.ERROR.name()).isEqualTo("ERROR");
    }

    @Test
    @DisplayName("LogType valueOf() 메서드 테스트")
    void logType_ShouldParseFromString() {
        // Given & When & Then
        assertThat(LogType.valueOf("ACTION")).isEqualTo(LogType.ACTION);
        assertThat(LogType.valueOf("ERROR")).isEqualTo(LogType.ERROR);
    }

    @Test
    @DisplayName("LogType ordinal() 메서드 테스트")
    void logType_ShouldHaveCorrectOrdinals() {
        // Given & When & Then
        assertThat(LogType.ACTION.ordinal()).isZero();
        assertThat(LogType.ERROR.ordinal()).isEqualTo(1);
    }

    @Test
    @DisplayName("LogType toString() 메서드 테스트")
    void logType_ShouldHaveCorrectToString() {
        // Given & When & Then
        assertThat(LogType.ACTION.toString()).isEqualTo("ACTION");
        assertThat(LogType.ERROR.toString()).isEqualTo("ERROR");
    }

    @Test
    @DisplayName("LogType description 테스트")
    void logType_ShouldHaveCorrectDescriptions() {
        // Given & When & Then
        assertThat(LogType.ACTION.getDescription()).isEqualTo("행동");
        assertThat(LogType.ERROR.getDescription()).isEqualTo("에러");
    }
}
