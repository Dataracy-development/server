package com.dataracy.modules.auth.adapter.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NoOpRateLimitAdapterTest {

    private NoOpRateLimitAdapter noOpRateLimitAdapter;

    @BeforeEach
    void setUp() {
        noOpRateLimitAdapter = new NoOpRateLimitAdapter();
    }

    @Test
    @DisplayName("isAllowed - 항상 true를 반환한다")
    void isAllowed_AlwaysReturnsTrue() {
        // given
        String key = "192.168.1.1";
        int maxRequests = 5;
        int windowMinutes = 1;

        // when
        boolean result = noOpRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAllowed - null 키로도 true를 반환한다")
    void isAllowed_WithNullKey_ReturnsTrue() {
        // given
        String key = null;
        int maxRequests = 5;
        int windowMinutes = 1;

        // when
        boolean result = noOpRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAllowed - 빈 문자열 키로도 true를 반환한다")
    void isAllowed_WithEmptyKey_ReturnsTrue() {
        // given
        String key = "";
        int maxRequests = 5;
        int windowMinutes = 1;

        // when
        boolean result = noOpRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAllowed - 0 maxRequests로도 true를 반환한다")
    void isAllowed_WithZeroMaxRequests_ReturnsTrue() {
        // given
        String key = "192.168.1.1";
        int maxRequests = 0;
        int windowMinutes = 1;

        // when
        boolean result = noOpRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAllowed - 음수 maxRequests로도 true를 반환한다")
    void isAllowed_WithNegativeMaxRequests_ReturnsTrue() {
        // given
        String key = "192.168.1.1";
        int maxRequests = -1;
        int windowMinutes = 1;

        // when
        boolean result = noOpRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAllowed - 0 windowMinutes로도 true를 반환한다")
    void isAllowed_WithZeroWindowMinutes_ReturnsTrue() {
        // given
        String key = "192.168.1.1";
        int maxRequests = 5;
        int windowMinutes = 0;

        // when
        boolean result = noOpRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAllowed - 음수 windowMinutes로도 true를 반환한다")
    void isAllowed_WithNegativeWindowMinutes_ReturnsTrue() {
        // given
        String key = "192.168.1.1";
        int maxRequests = 5;
        int windowMinutes = -1;

        // when
        boolean result = noOpRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("incrementRequestCount - 아무것도 하지 않는다")
    void incrementRequestCount_DoesNothing() {
        // given
        String key = "192.168.1.1";
        int incrementBy = 5;

        // when & then
        // 예외가 발생하지 않아야 함
        noOpRateLimitAdapter.incrementRequestCount(key, incrementBy);
    }

    @Test
    @DisplayName("incrementRequestCount - null 키로도 아무것도 하지 않는다")
    void incrementRequestCount_WithNullKey_DoesNothing() {
        // given
        String key = null;
        int incrementBy = 5;

        // when & then
        // 예외가 발생하지 않아야 함
        noOpRateLimitAdapter.incrementRequestCount(key, incrementBy);
    }

    @Test
    @DisplayName("incrementRequestCount - 빈 문자열 키로도 아무것도 하지 않는다")
    void incrementRequestCount_WithEmptyKey_DoesNothing() {
        // given
        String key = "";
        int incrementBy = 5;

        // when & then
        // 예외가 발생하지 않아야 함
        noOpRateLimitAdapter.incrementRequestCount(key, incrementBy);
    }

    @Test
    @DisplayName("incrementRequestCount - 0 incrementBy로도 아무것도 하지 않는다")
    void incrementRequestCount_WithZeroIncrement_DoesNothing() {
        // given
        String key = "192.168.1.1";
        int incrementBy = 0;

        // when & then
        // 예외가 발생하지 않아야 함
        noOpRateLimitAdapter.incrementRequestCount(key, incrementBy);
    }

    @Test
    @DisplayName("incrementRequestCount - 음수 incrementBy로도 아무것도 하지 않는다")
    void incrementRequestCount_WithNegativeIncrement_DoesNothing() {
        // given
        String key = "192.168.1.1";
        int incrementBy = -1;

        // when & then
        // 예외가 발생하지 않아야 함
        noOpRateLimitAdapter.incrementRequestCount(key, incrementBy);
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

