package com.dataracy.modules.behaviorlog.domain.model;

import com.dataracy.modules.behaviorlog.domain.enums.ActionType;
import com.dataracy.modules.behaviorlog.domain.enums.DeviceType;
import com.dataracy.modules.behaviorlog.domain.enums.LogType;
import com.dataracy.modules.common.support.enums.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class BehaviorLogTest {

    @Test
    @DisplayName("isValid - userId가 유효한 경우 true를 반환한다")
    void isValid_WhenUserIdIsValid_ReturnsTrue() {
        // given
        BehaviorLog behaviorLog = BehaviorLog.builder()
                .userId("user123")
                .build();

        // when
        boolean result = behaviorLog.isValid();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isValid - anonymousId가 유효한 경우 true를 반환한다")
    void isValid_WhenAnonymousIdIsValid_ReturnsTrue() {
        // given
        BehaviorLog behaviorLog = BehaviorLog.builder()
                .anonymousId("anonymous123")
                .build();

        // when
        boolean result = behaviorLog.isValid();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isValid - userId와 anonymousId가 모두 유효한 경우 true를 반환한다")
    void isValid_WhenBothIdsAreValid_ReturnsTrue() {
        // given
        BehaviorLog behaviorLog = BehaviorLog.builder()
                .userId("user123")
                .anonymousId("anonymous123")
                .build();

        // when
        boolean result = behaviorLog.isValid();

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
            ",",           // 둘 다 null
            "'',",         // userId 빈 문자열, anonymousId null
            "'   ',",      // userId 공백, anonymousId null
            ",''"          // userId null, anonymousId 빈 문자열
            ,",'   '"      // userId null, anonymousId 공백
    })
    @DisplayName("isValid - userId와 anonymousId가 모두 유효하지 않으면 false를 반환한다")
    void isValid_WhenBothIdsAreInvalid_ReturnsFalse(String userId, String anonymousId) {
        // given
        BehaviorLog behaviorLog = BehaviorLog.builder()
                .userId(userId)
                .anonymousId(anonymousId)
                .build();

        // when
        boolean result = behaviorLog.isValid();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("withNewTimestamp - 새로운 타임스탬프로 BehaviorLog를 생성한다")
    void withNewTimestamp_CreatesBehaviorLogWithNewTimestamp() {
        // given
        BehaviorLog original = BehaviorLog.builder()
                .userId("user123")
                .path("/test")
                .httpMethod(HttpMethod.GET)
                .action(ActionType.CLICK)
                .deviceType(DeviceType.PC)
                .logType(LogType.ACTION)
                .timestamp("2024-01-01T00:00:00Z")
                .build();

        String newTimestamp = "2024-01-02T00:00:00Z";

        // when
        BehaviorLog result = original.toBuilder()
                .timestamp(newTimestamp)
                .build();

        // then
        assertThat(result.getUserId()).isEqualTo(original.getUserId());
        assertThat(result.getPath()).isEqualTo(original.getPath());
        assertThat(result.getHttpMethod()).isEqualTo(original.getHttpMethod());
        assertThat(result.getAction()).isEqualTo(original.getAction());
        assertThat(result.getDeviceType()).isEqualTo(original.getDeviceType());
        assertThat(result.getLogType()).isEqualTo(original.getLogType());
        assertThat(result.getTimestamp()).isEqualTo(newTimestamp);
    }

    @Test
    @DisplayName("builder - 모든 필드를 포함한 BehaviorLog를 생성한다")
    void builder_WithAllFields_CreatesBehaviorLog() {
        // when
        BehaviorLog behaviorLog = BehaviorLog.builder()
                .userId("user123")
                .anonymousId("anonymous123")
                .path("/test")
                .httpMethod(HttpMethod.GET)
                .ip("192.168.1.1")
                .requestId("req123")
                .sessionId("session123")
                .userAgent("Mozilla/5.0")
                .referrer("https://example.com")
                .nextPath("/next")
                .action(ActionType.CLICK)
                .stayTime(5000L)
                .responseTime(100L)
                .dbLatency(50L)
                .externalLatency(30L)
                .deviceType(DeviceType.PC)
                .os("Windows")
                .browser("Chrome")
                .logType(LogType.ACTION)
                .timestamp("2024-01-01T00:00:00Z")
                .build();

        // then
        assertThat(behaviorLog.getUserId()).isEqualTo("user123");
        assertThat(behaviorLog.getAnonymousId()).isEqualTo("anonymous123");
        assertThat(behaviorLog.getPath()).isEqualTo("/test");
        assertThat(behaviorLog.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(behaviorLog.getIp()).isEqualTo("192.168.1.1");
        assertThat(behaviorLog.getRequestId()).isEqualTo("req123");
        assertThat(behaviorLog.getSessionId()).isEqualTo("session123");
        assertThat(behaviorLog.getUserAgent()).isEqualTo("Mozilla/5.0");
        assertThat(behaviorLog.getReferrer()).isEqualTo("https://example.com");
        assertThat(behaviorLog.getNextPath()).isEqualTo("/next");
        assertThat(behaviorLog.getAction()).isEqualTo(ActionType.CLICK);
        assertThat(behaviorLog.getStayTime()).isEqualTo(5000L);
        assertThat(behaviorLog.getResponseTime()).isEqualTo(100L);
        assertThat(behaviorLog.getDbLatency()).isEqualTo(50L);
        assertThat(behaviorLog.getExternalLatency()).isEqualTo(30L);
        assertThat(behaviorLog.getDeviceType()).isEqualTo(DeviceType.PC);
        assertThat(behaviorLog.getOs()).isEqualTo("Windows");
        assertThat(behaviorLog.getBrowser()).isEqualTo("Chrome");
        assertThat(behaviorLog.getLogType()).isEqualTo(LogType.ACTION);
        assertThat(behaviorLog.getTimestamp()).isEqualTo("2024-01-01T00:00:00Z");
    }
}