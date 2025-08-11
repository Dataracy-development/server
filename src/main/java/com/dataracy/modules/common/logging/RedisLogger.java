package com.dataracy.modules.common.logging;

import com.dataracy.modules.common.logging.support.BaseLogger;

import java.time.Duration;
import java.time.Instant;

public class RedisLogger extends BaseLogger {
    /**
     * Redis 조회 작업의 시작을 디버그 레벨로 로그에 기록하고 현재 시각을 반환합니다.
     *
     * @param key     Redis 키 값
     * @param message 조회 작업에 대한 설명 메시지
     * @return        작업 시작 시각의 Instant 객체
     */
    public Instant logQueryStart(String key, String message) {
        debug("[Redis 조회 시작] key={} message={}", key, message);
        return Instant.now();
    }

    /**
     * Redis 쿼리의 완료를 로그로 기록하며, 실행 시간을 밀리초 단위로 포함합니다.
     *
     * @param key      조회한 Redis 키
     * @param message  추가 설명 메시지
     * @param startTime 쿼리 시작 시각
     */
    public void logQueryEnd(String key, String message, Instant startTime) {
        long durationMs = Duration.between(startTime, Instant.now()).toMillis();
        debug("[Redis 조회 완료] key={} message={} duration={}ms", key, message, durationMs);
    }

    /**
     * Redis에서 저장 또는 업데이트 작업을 수행할 때 관련 키와 메시지를 info 레벨로 기록합니다.
     *
     * @param key     Redis 작업 대상 키
     * @param message 작업에 대한 설명 메시지
     */
    public void logSaveOrUpdate(String key, String message) {
        info("[Redis 저장] {} - {}", key, message);
    }

    /**
     * 지정된 Redis 키에 대한 삭제 작업을 정보 수준으로 로그에 기록합니다.
     *
     * @param key     삭제 대상 Redis 키
     * @param message 삭제 작업에 대한 설명 또는 추가 메시지
     */
    public void logDelete(String key, String message) {
        info("[Redis 삭제] {} - {}", key, message);
    }

    /**
     * Redis에서 키의 존재 여부를 확인하는 작업을 정보 레벨로 로그에 기록합니다.
     *
     * @param key     존재 여부를 확인할 Redis 키
     * @param message 추가 설명 또는 관련 메시지
     */
    public void logExist(String key, String message) {
        info("[Redis 존재 여부 확인] {} - {}", key, message);
    }

    /**
     * Redis 관련 경고 메시지를 지정된 키와 함께 로그에 기록합니다.
     *
     * @param key     경고와 관련된 Redis 키
     * @param message 경고에 대한 설명 메시지
     */
    public void logWarning(String key, String message) {
        warn("[Redis 경고] {} - {}", key, message);
    }

    /**
     * Redis 작업 중 발생한 오류를 지정된 키와 메시지, 예외 정보와 함께 에러 레벨로 기록합니다.
     *
     * @param key     Redis 연산과 관련된 키
     * @param message 오류에 대한 설명 메시지
     * @param e       발생한 예외 객체
     */
    public void logError(String key, String message, Throwable e) {
        error(e, "[Redis 오류] {} - {}", key, message);
    }
}
