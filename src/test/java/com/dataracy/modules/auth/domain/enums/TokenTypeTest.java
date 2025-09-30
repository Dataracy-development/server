package com.dataracy.modules.auth.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TokenType 테스트")
class TokenTypeTest {

    @Test
    @DisplayName("TokenType enum 값들 확인")
    void tokenType_ShouldHaveCorrectValues() {
        // Given & When & Then
        assertThat(TokenType.values()).hasSize(4);
        
        assertThat(TokenType.REGISTER).isNotNull();
        assertThat(TokenType.ACCESS).isNotNull();
        assertThat(TokenType.REFRESH).isNotNull();
        assertThat(TokenType.RESET_PASSWORD).isNotNull();
    }

    @Test
    @DisplayName("TokenType name() 메서드 테스트")
    void tokenType_ShouldHaveCorrectNames() {
        // Given & When & Then
        assertThat(TokenType.REGISTER.name()).isEqualTo("REGISTER");
        assertThat(TokenType.ACCESS.name()).isEqualTo("ACCESS");
        assertThat(TokenType.REFRESH.name()).isEqualTo("REFRESH");
        assertThat(TokenType.RESET_PASSWORD.name()).isEqualTo("RESET_PASSWORD");
    }

    @Test
    @DisplayName("TokenType valueOf() 메서드 테스트")
    void tokenType_ShouldParseFromString() {
        // Given & When & Then
        assertThat(TokenType.valueOf("REGISTER")).isEqualTo(TokenType.REGISTER);
        assertThat(TokenType.valueOf("ACCESS")).isEqualTo(TokenType.ACCESS);
        assertThat(TokenType.valueOf("REFRESH")).isEqualTo(TokenType.REFRESH);
        assertThat(TokenType.valueOf("RESET_PASSWORD")).isEqualTo(TokenType.RESET_PASSWORD);
    }

    @Test
    @DisplayName("TokenType ordinal() 메서드 테스트")
    void tokenType_ShouldHaveCorrectOrdinals() {
        // Given & When & Then
        assertThat(TokenType.REGISTER.ordinal()).isZero();
        assertThat(TokenType.ACCESS.ordinal()).isEqualTo(1);
        assertThat(TokenType.REFRESH.ordinal()).isEqualTo(2);
        assertThat(TokenType.RESET_PASSWORD.ordinal()).isEqualTo(3);
    }

    @Test
    @DisplayName("TokenType toString() 메서드 테스트")
    void tokenType_ShouldHaveCorrectToString() {
        // Given & When & Then
        assertThat(TokenType.REGISTER).hasToString("REGISTER");
        assertThat(TokenType.ACCESS).hasToString("ACCESS");
        assertThat(TokenType.REFRESH).hasToString("REFRESH");
        assertThat(TokenType.RESET_PASSWORD).hasToString("RESET_PASSWORD");
    }

    @Test
    @DisplayName("TokenType value 테스트")
    void tokenType_ShouldHaveCorrectValueProperties() {
        // Given & When & Then
        assertThat(TokenType.REGISTER.getValue()).isEqualTo("REGISTER");
        assertThat(TokenType.ACCESS.getValue()).isEqualTo("ACCESS");
        assertThat(TokenType.REFRESH.getValue()).isEqualTo("REFRESH");
        assertThat(TokenType.RESET_PASSWORD.getValue()).isEqualTo("RESET_PASSWORD");
    }

    @Test
    @DisplayName("TokenType of() 메서드 테스트")
    void tokenType_ShouldParseFromOf() {
        // Given & When & Then
        assertThat(TokenType.of("REGISTER")).isEqualTo(TokenType.REGISTER);
        assertThat(TokenType.of("ACCESS")).isEqualTo(TokenType.ACCESS);
        assertThat(TokenType.of("REFRESH")).isEqualTo(TokenType.REFRESH);
        assertThat(TokenType.of("RESET_PASSWORD")).isEqualTo(TokenType.RESET_PASSWORD);
        assertThat(TokenType.of("register")).isEqualTo(TokenType.REGISTER);
        assertThat(TokenType.of("access")).isEqualTo(TokenType.ACCESS);
    }
}
