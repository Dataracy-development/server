package com.dataracy.modules.common.logging;

import java.time.Duration;
import java.time.Instant;

public class ServiceLogger extends BaseLogger {

    public Instant logStart(String useCase, String input) {
        Instant start = Instant.now();
        info("[Service 시작] {} input={}", useCase, input);
        return start;
    }

    public void logSuccess(String useCase, String result, Instant startTime) {
        long durationMs = Duration.between(startTime, Instant.now()).toMillis();
        info("[Service 완료] {} result={} duration={}ms", useCase, result, durationMs);
    }

    public void logWarning(String context, String reason) {
        warn("[Service 경고] {} - {}", context, reason);
    }

    public void logException(String useCase, String message, Throwable e) {
        error(e, "[Service 예외] useCase={} message={}", useCase, message);
    }
}

