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
     * 분산 락 작업의 시작을 로그로 기록하고 현재 시각을 반환합니다.
     *
     * @param key     락의 식별자
     * @param message 작업에 대한 설명 메시지
     * @return        작업 시작 시각의 Instant 객체
     */
    public Instant logStart(String key, String message) {
        Instant start = Instant.now();
        info("{} 시작 - key={} message={}", PREFIX, key, message);
        return start;
    }

    /**
     * 분산 락 작업이 성공적으로 완료되었을 때, 작업 키, 메시지, 소요 시간을 로그로 기록합니다.
     *
     * @param key      락 작업의 식별자
     * @param message  작업에 대한 설명 메시지
     * @param startTime 작업 시작 시각
     */
    public void logSuccess(String key, String message, Instant startTime) {
        long durationMs = Duration.between(startTime, Instant.now()).toMillis();
        info("{} 완료 - key={} message={} duration={}ms", PREFIX, key, message, durationMs);
    }

    /**
     * 분산 락 획득을 시도할 때 디버그 로그를 기록합니다.
     *
     * @param key     락의 식별자
     * @param attempt 현재 시도 횟수
     */
    public void logTry(String key, int attempt) {
        debug("{} 시도 - key={} attempt={}", PREFIX, key, attempt);
    }

    /**
     * 분산 락 획득 시도에 실패했을 때 경고 로그를 남깁니다.
     *
     * @param key     실패한 락의 식별자
     * @param attempt 시도 횟수
     */
    public void logFail(String key, int attempt) {
        warn("{} 실패 - key={} attempt={}", PREFIX, key, attempt);
    }

    /**
     * 분산 락 해제 성공 시 디버그 로그를 기록합니다.
     *
     * @param key 해제된 락의 식별자
     */
    public void logUnlock(String key) {
        debug("{} 해제 - key={}", PREFIX, key);
    }

    /**
     * 분산 락 해제에 실패한 경우 에러 로그를 기록합니다.
     *
     * @param key 해제하려는 락의 식별자
     * @param e   발생한 예외 객체
     */
    public void logUnlockFail(String key, Throwable e) {
        error(e, "{} 해제 실패 - key={}", PREFIX, key);
    }

    /**
     * 분산 락 처리 중 발생한 예외를 에러 레벨로 기록합니다.
     *
     * @param key     예외가 발생한 락의 식별자
     * @param message 예외와 관련된 추가 메시지
     * @param e       발생한 예외 객체
     */
    public void logException(String key, String message, Throwable e) {
        error(e, "{} 예외 - key={} message={}", PREFIX, key, message);
    }

    /**
     * 분산 락 처리와 관련된 경고 메시지를 key와 함께 경고 레벨로 기록합니다.
     *
     * @param key     경고가 발생한 락의 식별자
     * @param message 경고의 상세 내용
     */
    public void logWarning(String key, String message) {
        warn("{} 경고 - key={} message={}", PREFIX, key, message);
    }

    /**
     * 분산 락 처리에서 재시도 횟수가 초과되었음을 에러 레벨로 기록합니다.
     *
     * @param key 재시도 초과가 발생한 락의 식별자
     */
    public void logRetryExceeded(String key) {
        error("{} 재시도 초과 - key={}", PREFIX, key);
    }

    /**
     * 분산 락 처리와 관련된 일반 정보 로그를 "[Lock]" 접두사와 함께 기록합니다.
     *
     * @param message 로그 메시지
     * @param args 메시지 포맷에 사용할 추가 인자
     */
    public void logInfo(String message, Object... args) {
        info(PREFIX + " " + message, args);
    }

    /**
     * 분산 락 처리와 관련된 일반 debug 로그를 기록합니다.
     *
     * @param message 로그 메시지
     * @param args 메시지에 포맷팅될 추가 인자
     */
    public void logDebug(String message, Object... args) {
        debug(PREFIX + " " + message, args);
    }

    /**
     * 분산 락 처리와 관련된 일반 경고(warn) 로그를 "[Lock]" 접두사와 함께 기록합니다.
     *
     * @param message 로그 메시지
     * @param args 메시지 포맷에 사용할 추가 인자
     */
    public void logWarn(String message, Object... args) {
        warn(PREFIX + " " + message, args);
    }

    /**
     * "[Lock]" 접두사가 포함된 일반 에러 메시지를 기록합니다.
     *
     * @param message 에러 메시지 템플릿
     * @param args    메시지 포맷에 사용할 가변 인자
     */
    public void logError(String message, Object... args) {
        error(PREFIX + " " + message, args);
    }

    /**
     * "[Lock]" 접두사와 함께 일반적인 에러 메시지와 예외를 로그로 기록합니다.
     *
     * @param message 에러 메시지
     * @param e       기록할 예외 객체
     */
    public void logError(String message, Throwable e) {
        error(e, PREFIX + " " + message);
    }
}
