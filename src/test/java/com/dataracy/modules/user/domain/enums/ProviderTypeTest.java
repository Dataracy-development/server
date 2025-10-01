package com.dataracy.modules.user.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("ProviderType 테스트")
class ProviderTypeTest {

    @Test
    @DisplayName("모든 ProviderType 값 확인")
    void allProviderTypes_ShouldBeDefined() {
        // Then
        assertAll(
                () -> assertThat(ProviderType.values()).hasSize(3),
                () -> assertThat(ProviderType.GOOGLE).isNotNull(),
                () -> assertThat(ProviderType.KAKAO).isNotNull(),
                () -> assertThat(ProviderType.LOCAL).isNotNull()
        );
    }

    @Test
    @DisplayName("ProviderType name 확인")
    void providerTypeNames_ShouldBeCorrect() {
        // Then
        assertAll(
                () -> assertThat(ProviderType.GOOGLE.name()).isEqualTo("GOOGLE"),
                () -> assertThat(ProviderType.KAKAO.name()).isEqualTo("KAKAO"),
                () -> assertThat(ProviderType.LOCAL.name()).isEqualTo("LOCAL")
        );
    }

    @Test
    @DisplayName("ProviderType toString 확인")
    void providerTypeToString_ShouldReturnName() {
        // Then
        assertAll(
                () -> assertThat(ProviderType.GOOGLE.toString()).isEqualTo("GOOGLE"),
                () -> assertThat(ProviderType.KAKAO.toString()).isEqualTo("KAKAO"),
                () -> assertThat(ProviderType.LOCAL.toString()).isEqualTo("LOCAL")
        );
    }
}