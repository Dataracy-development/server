package com.dataracy.modules.common.logging;

import com.dataracy.modules.common.logging.support.BaseLogger;

import java.time.Duration;
import java.time.Instant;

/**
 * 분산 락 처리 로직에서의 로깅을 담당하는 로거 클래스입니다.
 * 락 획득 시도, 성공, 실패, 예외 등에 대한 로그를 일관된 포맷으로 출력합니다.
 */
public class DistributedLockLogger extends BaseLogger {

    private static final String PREFIX = "[Lock]";

    /**
     * 락 관련 작업 시작 시 로깅
     */
    public Instant logStart(String key, String message) {
        Instant start = Instant.now();
        info("{} 시작 - key={} message={}", PREFIX, key, message);
        return start;
    }

    /**
     * 락 관련 작업 성공 시 로깅
     */
    public void logSuccess(String key, String message, Instant startTime) {
        long durationMs = Duration.between(startTime, Instant.now()).toMillis();
        info("{} 완료 - key={} message={} duration={}ms", PREFIX, key, message, durationMs);
    }

    /**
     * 락 획득 시도 로그
     */
    public void logTry(String key, int attempt) {
        debug("{} 시도 - key={} attempt={}", PREFIX, key, attempt);
    }

    /**
     * 락 획득 실패 로그
     */
    public void logFail(String key, int attempt) {
        warn("{} 실패 - key={} attempt={}", PREFIX, key, attempt);
    }

    /**
     * 락 해제 성공 로그
     */
    public void logUnlock(String key) {
        debug("{} 해제 - key={}", PREFIX, key);
    }

    /**
     * 락 해제 실패 로그
     */
    public void logUnlockFail(String key, Throwable e) {
        error(e, "{} 해제 실패 - key={}", PREFIX, key);
    }

    /**
     * 예외 로그
     */
    public void logException(String key, String message, Throwable e) {
        error(e, "{} 예외 - key={} message={}", PREFIX, key, message);
    }

    /**
     * 경고 로그
     */
    public void logWarning(String key, String message) {
        warn("{} 경고 - key={} message={}", PREFIX, key, message);
    }

    /**
     * 재시도 초과 로그
     */
    public void logRetryExceeded(String key) {
        error("{} 재시도 초과 - key={}", PREFIX, key);
    }

    /**
     * 일반 info 로그 (외부에서 사용하기 위해 추가)
     */
    public void logInfo(String message, Object... args) {
        info(PREFIX + " " + message, args);
    }

    /**
     * 일반 debug 로그
     */
    public void logDebug(String message, Object... args) {
        debug(PREFIX + " " + message, args);
    }

    /**
     * 일반 warn 로그
     */
    public void logWarn(String message, Object... args) {
        warn(PREFIX + " " + message, args);
    }

    /**
     * 일반 error 로그
     */
    public void logError(String message, Object... args) {
        error(PREFIX + " " + message, args);
    }

    /**
     * 일반 error 로그 + 예외
     */
    public void logError(String message, Throwable e) {
        error(e, PREFIX + " " + message);
    }
}
