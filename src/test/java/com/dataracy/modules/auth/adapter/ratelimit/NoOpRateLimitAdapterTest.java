package com.dataracy.modules.auth.adapter.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.thenCode;

class NoOpRateLimitAdapterTest {

    private NoOpRateLimitAdapter noOpRateLimitAdapter;

    @BeforeEach
    void setUp() {
        noOpRateLimitAdapter = new NoOpRateLimitAdapter();
    }

    @ParameterizedTest
    @CsvSource({
            "192.168.1.1, 5, 1",     // 정상 케이스
            ", 5, 1",                 // null 키
            "'', 5, 1",               // 빈 문자열 키
            "192.168.1.1, 0, 1",     // 0 maxRequests
            "192.168.1.1, -1, 1",    // 음수 maxRequests
            "192.168.1.1, 5, 0",     // 0 windowMinutes
            "192.168.1.1, 5, -1"     // 음수 windowMinutes
    })
    @DisplayName("isAllowed - 모든 입력에 대해 항상 true를 반환한다")
    void isAllowed_AlwaysReturnsTrue(String key, int maxRequests, int windowMinutes) {
        // when
        boolean result = noOpRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
            "192.168.1.1, 5",    // 정상 케이스
            ", 5",                // null 키
            "'', 5",              // 빈 문자열 키
            "192.168.1.1, 0",    // 0 incrementBy
            "192.168.1.1, -1"    // 음수 incrementBy
    })
    @DisplayName("incrementRequestCount - 모든 입력에 대해 예외 없이 실행된다")
    void incrementRequestCount_DoesNothing(String key, int incrementBy) {
        // when & then
        thenCode(() -> noOpRateLimitAdapter.incrementRequestCount(key, incrementBy))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("isAllowed - 여러 번 호출해도 항상 true를 반환한다")
    void isAllowed_MultipleCalls_AlwaysReturnsTrue() {
        // given
        String key = "192.168.1.1";
        int maxRequests = 1;
        int windowMinutes = 1;

        // when
        boolean firstCall = noOpRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);
        boolean secondCall = noOpRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);
        boolean thirdCall = noOpRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(firstCall).isTrue();
        assertThat(secondCall).isTrue();
        assertThat(thirdCall).isTrue();
    }
}

