package com.dataracy.modules.common.logging;

import java.time.Duration;
import java.time.Instant;

public class RedisLogger extends BaseLogger {

    public Instant logQueryStart(String key, String message) {
        debug("[Redis 조회 시작] key={} message={}", key, message);
        return Instant.now();
    }

    public void logQueryEnd(String key, String message, Instant startTime) {
        long durationMs = Duration.between(startTime, Instant.now()).toMillis();
        debug("[Redis 조회 완료] key={} message={} duration={}ms", key, message, durationMs);
    }

    public void logSaveOrUpdate(String key, String message) {
        info("[Redis 저장] {} - {}", key, message);
    }

    public void logDelete(String key, String message) {
        info("[Redis 삭제] {} - {}", key, message);
    }

    public void logExist(String key, String message) {
        info("[Redis 존재 여부 확인] {} - {}", key, message);
    }

    public void logWarning(String key, String message) {
        warn("[Redis 경고] {} - {}", key, message);
    }

    public void logError(String key, String message, Throwable e) {
        error(e, "[Redis 오류] {} - {}", key, message);
    }
}
