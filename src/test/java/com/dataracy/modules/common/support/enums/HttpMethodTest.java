package com.dataracy.modules.common.support.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpMethod 테스트")
class HttpMethodTest {

    @Test
    @DisplayName("HttpMethod enum 값들 확인")
    void httpMethod_ShouldHaveCorrectValues() {
        // Given & When & Then
        assertThat(HttpMethod.values()).hasSize(8);
        
        assertThat(HttpMethod.GET).isNotNull();
        assertThat(HttpMethod.POST).isNotNull();
        assertThat(HttpMethod.PUT).isNotNull();
        assertThat(HttpMethod.PATCH).isNotNull();
        assertThat(HttpMethod.DELETE).isNotNull();
        assertThat(HttpMethod.HEAD).isNotNull();
        assertThat(HttpMethod.OPTIONS).isNotNull();
        assertThat(HttpMethod.TRACE).isNotNull();
    }

    @Test
    @DisplayName("HttpMethod name() 메서드 테스트")
    void httpMethod_ShouldHaveCorrectNames() {
        // Given & When & Then
        assertThat(HttpMethod.GET.name()).isEqualTo("GET");
        assertThat(HttpMethod.POST.name()).isEqualTo("POST");
        assertThat(HttpMethod.PUT.name()).isEqualTo("PUT");
        assertThat(HttpMethod.PATCH.name()).isEqualTo("PATCH");
        assertThat(HttpMethod.DELETE.name()).isEqualTo("DELETE");
        assertThat(HttpMethod.HEAD.name()).isEqualTo("HEAD");
        assertThat(HttpMethod.OPTIONS.name()).isEqualTo("OPTIONS");
        assertThat(HttpMethod.TRACE.name()).isEqualTo("TRACE");
    }

    @Test
    @DisplayName("HttpMethod valueOf() 메서드 테스트")
    void httpMethod_ShouldParseFromString() {
        // Given & When & Then
        assertThat(HttpMethod.valueOf("GET")).isEqualTo(HttpMethod.GET);
        assertThat(HttpMethod.valueOf("POST")).isEqualTo(HttpMethod.POST);
        assertThat(HttpMethod.valueOf("PUT")).isEqualTo(HttpMethod.PUT);
        assertThat(HttpMethod.valueOf("PATCH")).isEqualTo(HttpMethod.PATCH);
        assertThat(HttpMethod.valueOf("DELETE")).isEqualTo(HttpMethod.DELETE);
        assertThat(HttpMethod.valueOf("HEAD")).isEqualTo(HttpMethod.HEAD);
        assertThat(HttpMethod.valueOf("OPTIONS")).isEqualTo(HttpMethod.OPTIONS);
        assertThat(HttpMethod.valueOf("TRACE")).isEqualTo(HttpMethod.TRACE);
    }

    @Test
    @DisplayName("HttpMethod ordinal() 메서드 테스트")
    void httpMethod_ShouldHaveCorrectOrdinals() {
        // Given & When & Then
        assertThat(HttpMethod.GET.ordinal()).isZero();
        assertThat(HttpMethod.POST.ordinal()).isEqualTo(1);
        assertThat(HttpMethod.PUT.ordinal()).isEqualTo(2);
        assertThat(HttpMethod.DELETE.ordinal()).isEqualTo(3);
        assertThat(HttpMethod.PATCH.ordinal()).isEqualTo(4);
        assertThat(HttpMethod.HEAD.ordinal()).isEqualTo(5);
        assertThat(HttpMethod.OPTIONS.ordinal()).isEqualTo(6);
        assertThat(HttpMethod.TRACE.ordinal()).isEqualTo(7);
    }

    @Test
    @DisplayName("HttpMethod toString() 메서드 테스트")
    void httpMethod_ShouldHaveCorrectToString() {
        // Given & When & Then
        assertThat(HttpMethod.GET).hasToString("GET");
        assertThat(HttpMethod.POST).hasToString("POST");
        assertThat(HttpMethod.PUT).hasToString("PUT");
        assertThat(HttpMethod.PATCH).hasToString("PATCH");
        assertThat(HttpMethod.DELETE).hasToString("DELETE");
        assertThat(HttpMethod.HEAD).hasToString("HEAD");
        assertThat(HttpMethod.OPTIONS).hasToString("OPTIONS");
        assertThat(HttpMethod.TRACE).hasToString("TRACE");
    }
}
