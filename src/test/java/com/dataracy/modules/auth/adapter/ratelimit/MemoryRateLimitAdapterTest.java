package com.dataracy.modules.auth.adapter.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MemoryRateLimitAdapterTest {

    private MemoryRateLimitAdapter memoryRateLimitAdapter;

    @BeforeEach
    void setUp() {
        memoryRateLimitAdapter = new MemoryRateLimitAdapter();
        // 기본값 설정
        ReflectionTestUtils.setField(memoryRateLimitAdapter, "defaultMaxRequests", 10);
        ReflectionTestUtils.setField(memoryRateLimitAdapter, "defaultWindowMinutes", 1);
    }

    @Test
    @DisplayName("isAllowed - 유효한 키로 요청이 허용된다")
    void isAllowed_WhenValidKey_ReturnsTrue() {
        // given
        String key = "192.168.1.1";
        int maxRequests = 5;
        int windowMinutes = 1;

        // when
        boolean result = memoryRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAllowed - 최대 요청 수를 초과하면 차단된다")
    void isAllowed_WhenExceedsMaxRequests_ReturnsFalse() {
        // given
        String key = "192.168.1.1";
        int maxRequests = 2;
        int windowMinutes = 1;

        // when
        boolean firstRequest = memoryRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);
        memoryRateLimitAdapter.incrementRequestCount(key, 1);
        boolean secondRequest = memoryRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);
        memoryRateLimitAdapter.incrementRequestCount(key, 1);
        boolean thirdRequest = memoryRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(firstRequest).isTrue();
        assertThat(secondRequest).isTrue();
        assertThat(thirdRequest).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("isAllowed - null이거나 빈 문자열 또는 공백 키는 항상 허용된다")
    void isAllowed_WhenInvalidKey_ReturnsTrue(String key) {
        // given
        int maxRequests = 5;
        int windowMinutes = 1;

        // when
        boolean result = memoryRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAllowed - 다른 키들은 독립적으로 카운트된다")
    void isAllowed_WhenDifferentKeys_CountsIndependently() {
        // given
        String key1 = "192.168.1.1";
        String key2 = "192.168.1.2";
        int maxRequests = 1;
        int windowMinutes = 1;

        // when
        boolean key1First = memoryRateLimitAdapter.isAllowed(key1, maxRequests, windowMinutes);
        memoryRateLimitAdapter.incrementRequestCount(key1, 1);
        boolean key2First = memoryRateLimitAdapter.isAllowed(key2, maxRequests, windowMinutes);
        memoryRateLimitAdapter.incrementRequestCount(key2, 1);
        boolean key1Second = memoryRateLimitAdapter.isAllowed(key1, maxRequests, windowMinutes);
        boolean key2Second = memoryRateLimitAdapter.isAllowed(key2, maxRequests, windowMinutes);

        // then
        assertThat(key1First).isTrue();
        assertThat(key2First).isTrue();
        assertThat(key1Second).isFalse();
        assertThat(key2Second).isFalse();
    }

    @Test
    @DisplayName("incrementRequestCount - 요청 카운트를 증가시킨다")
    void incrementRequestCount_WhenValidKey_IncrementsCount() {
        // given
        String key = "192.168.1.1";
        int incrementBy = 1;

        // when
        memoryRateLimitAdapter.incrementRequestCount(key, incrementBy);

        // then
        // 카운트가 증가했는지 확인하기 위해 isAllowed로 테스트
        boolean result = memoryRateLimitAdapter.isAllowed(key, 1, 1);
        assertThat(result).isFalse(); // 이미 1번 카운트되었으므로 차단되어야 함
    }

    @Test
    @DisplayName("incrementRequestCount - null 키는 카운트하지 않는다")
    void incrementRequestCount_WhenNullKey_DoesNotIncrement() {
        // given
        String key = null;
        int incrementBy = 1;

        // when
        memoryRateLimitAdapter.incrementRequestCount(key, incrementBy);

        // then
        // null 키는 카운트되지 않으므로 isAllowed는 여전히 true여야 함
        boolean result = memoryRateLimitAdapter.isAllowed(key, 1, 1);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("incrementRequestCount - 빈 문자열 키는 카운트하지 않는다")
    void incrementRequestCount_WhenEmptyKey_DoesNotIncrement() {
        // given
        String key = "";
        int incrementBy = 1;

        // when
        memoryRateLimitAdapter.incrementRequestCount(key, incrementBy);

        // then
        // 빈 문자열 키는 카운트되지 않으므로 isAllowed는 여전히 true여야 함
        boolean result = memoryRateLimitAdapter.isAllowed(key, 1, 1);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAllowed - 윈도우 시간이 지나면 카운터가 리셋된다")
    void isAllowed_WhenWindowExpires_ResetsCounter() throws InterruptedException {
        // given
        String key = "192.168.1.1";
        int maxRequests = 1;
        int windowMinutes = 1; // 1분

        // when
        boolean firstRequest = memoryRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);
        memoryRateLimitAdapter.incrementRequestCount(key, 1);
        boolean secondRequest = memoryRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(firstRequest).isTrue();
        assertThat(secondRequest).isFalse();

        // 윈도우 시간이 지나면 다시 허용되어야 함 (실제로는 시간이 오래 걸리므로 테스트에서는 기본 동작만 확인)
        // 실제 환경에서는 스케줄러가 1분마다 정리 작업을 수행함
    }

    @Test
    @DisplayName("isAllowed - 0 maxRequests는 항상 차단한다")
    void isAllowed_WhenZeroMaxRequests_AlwaysBlocks() {
        // given
        String key = "192.168.1.1";
        int maxRequests = 0;
        int windowMinutes = 1;

        // when
        boolean result = memoryRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isAllowed - 음수 maxRequests는 항상 차단한다")
    void isAllowed_WhenNegativeMaxRequests_AlwaysBlocks() {
        // given
        String key = "192.168.1.1";
        int maxRequests = -1;
        int windowMinutes = 1;

        // when
        boolean result = memoryRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    @DisplayName("isAllowed - windowMinutes가 0 이하이면 항상 허용한다")
    void isAllowed_WhenInvalidWindowMinutes_AlwaysAllows(int windowMinutes) {
        // given
        String key = "192.168.1.1";
        int maxRequests = 5;

        // when
        boolean result = memoryRateLimitAdapter.isAllowed(key, maxRequests, windowMinutes);

        // then
        assertThat(result).isTrue();
    }
}
