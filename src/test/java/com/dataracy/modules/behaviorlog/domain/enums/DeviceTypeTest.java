package com.dataracy.modules.behaviorlog.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DeviceType 테스트")
class DeviceTypeTest {

    @Test
    @DisplayName("DeviceType enum 값들 확인")
    void deviceType_ShouldHaveCorrectValues() {
        // Given & When & Then
        assertThat(DeviceType.values()).hasSize(4);
        
        assertThat(DeviceType.PC).isNotNull();
        assertThat(DeviceType.MOBILE).isNotNull();
        assertThat(DeviceType.TABLET).isNotNull();
        assertThat(DeviceType.UNKNOWN).isNotNull();
    }

    @Test
    @DisplayName("DeviceType name() 메서드 테스트")
    void deviceType_ShouldHaveCorrectNames() {
        // Given & When & Then
        assertThat(DeviceType.PC.name()).isEqualTo("PC");
        assertThat(DeviceType.MOBILE.name()).isEqualTo("MOBILE");
        assertThat(DeviceType.TABLET.name()).isEqualTo("TABLET");
        assertThat(DeviceType.UNKNOWN.name()).isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("DeviceType valueOf() 메서드 테스트")
    void deviceType_ShouldParseFromString() {
        // Given & When & Then
        assertThat(DeviceType.valueOf("PC")).isEqualTo(DeviceType.PC);
        assertThat(DeviceType.valueOf("MOBILE")).isEqualTo(DeviceType.MOBILE);
        assertThat(DeviceType.valueOf("TABLET")).isEqualTo(DeviceType.TABLET);
        assertThat(DeviceType.valueOf("UNKNOWN")).isEqualTo(DeviceType.UNKNOWN);
    }

    @Test
    @DisplayName("DeviceType ordinal() 메서드 테스트")
    void deviceType_ShouldHaveCorrectOrdinals() {
        // Given & When & Then
        assertThat(DeviceType.PC.ordinal()).isEqualTo(0);
        assertThat(DeviceType.MOBILE.ordinal()).isEqualTo(1);
        assertThat(DeviceType.TABLET.ordinal()).isEqualTo(2);
        assertThat(DeviceType.UNKNOWN.ordinal()).isEqualTo(3);
    }

    @Test
    @DisplayName("DeviceType toString() 메서드 테스트")
    void deviceType_ShouldHaveCorrectToString() {
        // Given & When & Then
        assertThat(DeviceType.PC.toString()).isEqualTo("PC");
        assertThat(DeviceType.MOBILE.toString()).isEqualTo("MOBILE");
        assertThat(DeviceType.TABLET.toString()).isEqualTo("TABLET");
        assertThat(DeviceType.UNKNOWN.toString()).isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("DeviceType resolve() 메서드 테스트")
    void deviceType_ShouldResolveFromUserAgent() {
        // Given & When & Then
        assertThat(DeviceType.resolve(null)).isEqualTo(DeviceType.UNKNOWN);
        assertThat(DeviceType.resolve("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)")).isEqualTo(DeviceType.MOBILE);
        assertThat(DeviceType.resolve("Mozilla/5.0 (iPad; CPU OS 14_0 like Mac OS X)")).isEqualTo(DeviceType.TABLET);
        assertThat(DeviceType.resolve("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")).isEqualTo(DeviceType.PC);
        assertThat(DeviceType.resolve("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)")).isEqualTo(DeviceType.PC);
        assertThat(DeviceType.resolve("Mozilla/5.0 (Linux; Android 10)")).isEqualTo(DeviceType.MOBILE);
        assertThat(DeviceType.resolve("Unknown User Agent")).isEqualTo(DeviceType.UNKNOWN);
    }
}
